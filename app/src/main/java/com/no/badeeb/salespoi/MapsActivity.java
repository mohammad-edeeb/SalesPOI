package com.no.badeeb.salespoi;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.no.badeeb.salespoi.models.Customer;
import com.no.badeeb.salespoi.models.DataCenter;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLngBounds bounds = null;
        for (Customer c: DataCenter.getData()) {
            if(bounds == null){
                bounds = new LatLngBounds(c.getPosition(), c.getPosition());
            }
            else{
                bounds.including(c.getPosition());
            }
            addCustomerMarker(c);
        }
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
    }

    private void addCustomerMarker(Customer customer){
        MarkerOptions options = new MarkerOptions();
        LatLng position = new LatLng(customer.getLatitude(), customer.getLongitude());
        options.position(position);
        options.title("Name: " + customer.getName());
        options.snippet(createCustomerInfo(customer));
        map.addMarker(options);
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View layout = MapsActivity.this.getLayoutInflater().inflate(R.layout.map_info_window, null);
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
