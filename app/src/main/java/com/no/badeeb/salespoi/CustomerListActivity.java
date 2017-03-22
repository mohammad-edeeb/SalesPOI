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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.no.badeeb.salespoi.models.Customer;
import com.no.badeeb.salespoi.models.CustomersManager;

import org.json.JSONArray;
import org.json.JSONException;
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
            Toast.makeText(this, "No GPS signal detected", Toast.LENGTH_LONG).show();
            return;
        }
        String url = Constants.HOST + "/customers/near_customers.json";
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat", userLocation.getLatitude());
            jsonObject.put("long", userLocation.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.put(jsonObject);

        StringRequest jsonRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<Customer> customers = Arrays.asList(gson.fromJson(response, Customer[].class));
                        CustomersManager.add(customers);
                        recyclerViewAdapter.notifyDataSetChanged();
                        System.out.println("Response: " + response);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error: " + error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String authToken = CustomerListActivity.this.getSharedPreferences("User", MODE_PRIVATE).getString("auth_token", "no_token");
                Map<String, String> newHeaders = new HashMap<>();
                newHeaders.put("Authorization", authToken);
                return newHeaders;
            }
        };
        getRequestQueue().add(jsonRequest);
    }

    private void showLocationToast(Location location) {
        Toast.makeText(this, "Lat: " + location.getLatitude() + "\nLong: " + location.getLongitude(), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_action:
                findLocationAndTriggerRequest();
                return true;
            case R.id.logout_action:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showSettingsDialog() {
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
                            showProgress(false);
                            getCustomers();
                        }
                    });
                }
            }, 2000);
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            CustomerListActivity.this.gpsTask.cancel();
                            showProgress(false);
                            CustomerListActivity.this.showLocationToast(location);
                            if (location != null) {
                                CustomerListActivity.this.userLocation = location;
                            }
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
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            Toast.makeText(CustomerListActivity.this, "Long: " + loc.getLongitude() + "/n Lat: " + loc.getLatitude(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public class CustomersRecyclerViewAdapter
            extends RecyclerView.Adapter<CustomersRecyclerViewAdapter.ViewHolder> {

        private List<Customer> mCustomers;

        public CustomersRecyclerViewAdapter() {
            mCustomers = CustomersManager.getData();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.customer_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mCustomer = mCustomers.get(position);
            holder.mIdView.setText(mCustomers.get(position).getCustomerId());
            holder.mContentView.setText(mCustomers.get(position).getName());

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
