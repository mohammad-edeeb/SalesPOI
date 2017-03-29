package com.no.badeeb.salespoi;

import android.location.Location;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.no.badeeb.salespoi.models.Customer;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private List<Customer> customers = new ArrayList<>();
    private Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        customers = getIntent().getParcelableArrayListExtra(Constants.EXTRA_CUSTOMERS);
        userLocation = getIntent().getParcelableExtra(Constants.EXTRA_USER_LOCATION);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng userPosition = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        LatLngBounds.Builder builder = LatLngBounds.builder();
        builder.include(userPosition);
        addUserPositionMarker(userPosition);
        for (Customer c: customers) {
            builder.include(c.getPosition());
            addCustomerMarker(c);
        }
        final LatLngBounds bounds = builder.build();
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 40));
            }
        });
    }

    private void addUserPositionMarker(LatLng userPosition) {
        MarkerOptions options = new MarkerOptions();
        options.title("Your location");
        options.position(userPosition);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        map.addMarker(options);
    }

    private void addCustomerMarker(Customer customer){
        MarkerOptions options = new MarkerOptions();
        LatLng position = new LatLng(customer.getLatitude(), customer.getLongitude());
        options.position(position);
        options.title("Name: " + customer.getName());
        options.snippet(createCustomerInfo(customer));
        Customer.CustomerStatus statusEnum = Customer.CustomerStatus.findByStatus(customer.getStatus());
        switch (statusEnum){
            case Active:
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                break;
            case InProgress:
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                break;
            case Inactive:
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                break;
        }
        map.addMarker(options);
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View layout = MapActivity.this.getLayoutInflater().inflate(R.layout.map_info_window, null);
                ((TextView)layout.findViewById(R.id.window_title_text_view)).setText(marker.getTitle());
                ((TextView)layout.findViewById(R.id.window_snippet_text_view)).setText(marker.getSnippet());
                return layout;
            }
        });
    }

    private String createCustomerInfo(Customer customer){
        StringBuilder builder = new StringBuilder();
        builder.append("Id: " + customer.getCustomerId());
        builder.append("\n");
        builder.append("Zone: " + customer.getZoneName());
        builder.append("\n");
        builder.append("Salesman: " + customer.getSalesManName());
        builder.append("\n");
        builder.append("Last visit: " + customer.getLastVisitedAtString());
        builder.append("\n");
        builder.append("Last invoice: " + customer.getLastInvoiceAtString());
        builder.append("\n");
        builder.append("Last trx amount: " + customer.getLastTrxAmountString());
        return builder.toString();
    }

}
