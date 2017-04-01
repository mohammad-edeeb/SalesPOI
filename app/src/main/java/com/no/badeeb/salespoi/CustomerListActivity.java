package com.no.badeeb.salespoi;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.no.badeeb.salespoi.models.Customer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CustomerListActivity extends AppCompatActivity {

    private static final int LOCATION_PERM_RQST_CODE = 1;
    private static final int GPS_SIGNAL_TIMEOUT = 10 * 1000; // 10 seconds
    private static final int GPS_SIGNAL_TIMEOUT_LOCATION_FOUND = 5 * 1000; // 10 seconds


    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;

    private CustomersRecyclerViewAdapter recyclerViewAdapter;
    private LocationManager locationManager;
    private RequestQueue requestQueue;

    private Gson gson;
    private TimerTask gpsTask;
    private Location userLocation;
    private ArrayList<Customer> customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        customers = new ArrayList<>();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        progressBar = (ProgressBar) findViewById(R.id.gps_progress);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        recyclerView = (RecyclerView) findViewById(R.id.customer_list);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerViewAdapter = new CustomersRecyclerViewAdapter(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd");
        gson = gsonBuilder.create();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    private RequestQueue getRequestQueue() {
        return requestQueue == null ? Volley.newRequestQueue(this) : requestQueue;
    }

    private void findLocationAndTriggerRequest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERM_RQST_CODE);
        } else {
            getLocation();
        }
    }

    public ArrayList<Customer> getCustomers(){
        return customers;
    }

    private void fetchCustomers() {
        if (userLocation == null) {
            showProgress(false);
            Toast.makeText(this, "No GPS signal detected", Toast.LENGTH_LONG).show();
            return;
        }
        String url = Constants.HOST + "/api/sales_men/near_customers.json?";
        url += "lat=" + userLocation.getLatitude() + "&long=" + userLocation.getLongitude();
        StringRequest jsonRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<Customer> from = Arrays.asList(gson.fromJson(response, Customer[].class));
                        customers.clear();
                        customers.addAll(from);
                        showProgress(false);
                        if(customers == null || customers.isEmpty()){
                            Toast.makeText(CustomerListActivity.this, "No near customers found", Toast.LENGTH_LONG).show();
                        }
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        NetworkResponse networkResponse = error.networkResponse;
                        if(networkResponse != null && networkResponse.statusCode == 401){
                            CustomerListActivity.this.switchToLogin();
                        }
                        else{
                            Toast.makeText(CustomerListActivity.this, "No network connection", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String authToken = Utils.getAuthToken(CustomerListActivity.this);
                Map<String, String> newHeaders = new HashMap<>();
                newHeaders.put("Authorization", authToken);
                return newHeaders;
            }
        };
        jsonRequest.setRetryPolicy(Utils.getRetryPolicy());
        getRequestQueue().add(jsonRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_action:
                findLocationAndTriggerRequest();
                return true;
            case R.id.logout_action:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        String authToken = Utils.getAuthToken(this);
        if (TextUtils.isEmpty(authToken)) {
            switchToLogin();
        }
        String url = Constants.HOST + "/api/sales_men/sign_out.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CustomerListActivity.this.switchToLogin();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if(networkResponse != null && networkResponse.statusCode == 401){
                            CustomerListActivity.this.switchToLogin();
                        }
                        else{
                            Toast.makeText(CustomerListActivity.this, "No network connection", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String authToken = Utils.getAuthToken(CustomerListActivity.this);
                Map<String, String> newHeaders = new HashMap<>();
                newHeaders.put("Authorization", authToken);
                return newHeaders;
            }
        };
        request.setRetryPolicy(Utils.getRetryPolicy());
        getRequestQueue().add(request);
    }

    private void switchToLogin() {
        Utils.removeAuthToken(this);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS Disabled");
        alertDialog.setMessage("Do you want to turn GPS on?");

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(CustomerListActivity.this, "Please enable GPS then try again", Toast.LENGTH_LONG).show();
            }
        });

        alertDialog.show();
    }

    private void getLocation() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showSettingsDialog();
            return;
        }
        try {
            showProgress(true);
            userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            int timerTaskTimeout = GPS_SIGNAL_TIMEOUT;
            // If there is an already found location, decrease time to search for GPS update
            if(userLocation != null){
                timerTaskTimeout = GPS_SIGNAL_TIMEOUT_LOCATION_FOUND;
            }
            Timer t = new Timer();
            t.schedule(gpsTask = new TimerTask() {
                @Override
                public void run() {
                    CustomerListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fetchCustomers();
                        }
                    });
                }
            }, timerTaskTimeout);
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            try{
                                locationManager.removeUpdates(this);
                            }catch (SecurityException e){
                                e.printStackTrace();
                            }
                            CustomerListActivity.this.gpsTask.cancel();
                            if (location != null) {
                                CustomerListActivity.this.userLocation = location;
                            }
                            fetchCustomers();
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    }
                    , null);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void showProgress(final boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        frameLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        fab.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERM_RQST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    public void openMap(View view) {
        if(customers.isEmpty()){
            Toast.makeText(this, "No customers to show, please refresh", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, MapActivity.class);
        intent.putParcelableArrayListExtra(Constants.EXTRA_CUSTOMERS, customers);
        intent.putExtra(Constants.EXTRA_USER_LOCATION, userLocation);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

}
