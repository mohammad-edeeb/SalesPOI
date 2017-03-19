package com.no.badeeb.salespoi;

import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
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
import com.no.badeeb.salespoi.models.CustomersManager;

import org.w3c.dom.Text;

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
        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
        for (Customer c: CustomersManager.getData()) {
            boundsBuilder.include(c.getPosition());
            addCustomerMarker(c);
        }
        LatLngBounds bounds = boundsBuilder.build();
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
    }

    private void addCustomerMarker(Customer customer){
        MarkerOptions options = new MarkerOptions();
        LatLng position = new LatLng(customer.getLatitude(), customer.getLongitude());
        options.position(position);
        options.title(customer.getName());
        options.snippet(createCustomerInfo(customer));
        map.addMarker(options);
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout layout = new LinearLayout(MapsActivity.this);

                TextView titleView = new TextView(MapsActivity.this);
                titleView.setText(marker.getTitle());
                titleView.setTypeface(Typeface.DEFAULT_BOLD);

                TextView textView = new TextView(MapsActivity.this);
                textView.setText(marker.getSnippet());

                layout.addView(titleView);
                layout.addView(textView);
                return layout;
            }
        });
    }

    private String createCustomerInfo(Customer customer){
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("Id: " + customer.getCustomerId());
        builder.append("\n");
        builder.append("Zone: " + customer.getZoneName());
        builder.append("\n");
        builder.append("Salesman: " + customer.getSalesManName());
        builder.append("\n");
        builder.append("Last visit: " + Customer.DATE_FORMAT.format(customer.getLastVisitedAt()));
        builder.append("\n");
        builder.append("Last invoice: " + Customer.DATE_FORMAT.format(customer.getLastInvoiceAt()));
        builder.append("\n");
        builder.append("Last trx amount: " + customer.getLastTrxAmount());
        return builder.toString();
    }

}
