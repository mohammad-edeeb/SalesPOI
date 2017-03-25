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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.no.badeeb.salespoi.models.Customer;
import com.no.badeeb.salespoi.models.DataCenter;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.media.CamcorderProfile.get;

public class CustomerListActivity extends AppCompatActivity {

    private static final int LOCATION_PERM_RQST_CODE = 1;
    private static final int GPS_SIGNAL_TIMEOUT = 1 * 1000; // 10 seconds


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        progressBar = (ProgressBar) findViewById(R.id.gps_progress);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        recyclerView = (RecyclerView) findViewById(R.id.customer_list);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerViewAdapter = new CustomersRecyclerViewAdapter();
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

    private void getCustomers() {
        if (userLocation == null) {
            showProgress(false);
            Toast.makeText(this, "No GPS signal detected", Toast.LENGTH_LONG).show();
            return;
        }
        DataCenter.getInstance().setUserLocation(userLocation);
        String url = Constants.HOST + "/api/sales_men/near_customers.json?";
        url += "lat=" + userLocation.getLatitude() + "&long=" + userLocation.getLongitude();
        StringRequest jsonRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<Customer> customers = Arrays.asList(gson.fromJson(response, Customer[].class));
                        DataCenter.getInstance().clearCustomers();
                        DataCenter.getInstance().add(customers);
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
                        if(error.networkResponse.statusCode == 401){
                            CustomerListActivity.this.switchToLogin();
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
                        if(error.networkResponse.statusCode == 401){
                            CustomerListActivity.this.switchToLogin();
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
            Timer t = new Timer();
            t.schedule(gpsTask = new TimerTask() {
                @Override
                public void run() {
                    CustomerListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getCustomers();
                        }
                    });
                }
            }, GPS_SIGNAL_TIMEOUT);
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
                            getCustomers();
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
        if(!DataCenter.getInstance().hasCustomers()){
            Toast.makeText(this, "No customers to show, please refresh", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    public class CustomersRecyclerViewAdapter
            extends RecyclerView.Adapter<CustomersRecyclerViewAdapter.ViewHolder> {

        private List<Customer> mCustomers;

        public CustomersRecyclerViewAdapter() {
            mCustomers = DataCenter.getInstance().getCustomers();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.customer_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Customer c = mCustomers.get(position);
            holder.mCustomer = c;
            holder.mIdView.setText(c.getCustomerId());
            holder.mContentView.setText(c.getName());

            Customer.CustomerStatus statusEnum = Customer.CustomerStatus.findByStatus(c.getStatus());
            holder.mView.setAlpha(0.7f);
            switch (statusEnum){
                case Active:
                    holder.mView.setBackgroundColor(CustomerListActivity.this.getResources().getColor(R.color.activeCustomer));
                    break;
                case InProgress:
                    holder.mView.setBackgroundColor(CustomerListActivity.this.getResources().getColor(R.color.inprogressCustomer));
                    break;
                case Inactive:
                    holder.mView.setBackgroundColor(CustomerListActivity.this.getResources().getColor(R.color.inactiveCustomer));
                    break;
            }
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, CustomerDetailActivity.class);
                    intent.putExtra(CustomerDetailActivity.ARG_ITEM_ID, holder.mCustomer.getId());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCustomers.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Customer mCustomer;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
