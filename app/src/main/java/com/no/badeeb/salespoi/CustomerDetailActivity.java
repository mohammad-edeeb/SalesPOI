package com.no.badeeb.salespoi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

import com.no.badeeb.salespoi.models.Customer;
import com.no.badeeb.salespoi.models.DataCenter;

/**
 * An activity representing a single Customer detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link CustomerListActivity}.
 */
public class CustomerDetailActivity extends AppCompatActivity {

    public static final String ARG_ITEM_ID = "item_id";

    private TextView customerIdTextview;
    private TextView customerNameTextview;
    private TextView customerZoneTextview;
    private TextView customerSalesmanTextview;
    private TextView customerStatusTextview;
    private TextView customerLastVisitTextview;
    private TextView customerLastInvoiceTextview;
    private TextView customerLastTrxTextview;
    private TextView customerExtra1Textview;
    private TextView customerExtra2Textview;
    private TextView customerExtra3Textview;

    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        customerIdTextview = (TextView) findViewById(R.id.customer_id_text_view);
        customerNameTextview = (TextView) findViewById(R.id.customer_name_text_view);
        customerZoneTextview = (TextView) findViewById(R.id.customer_zone_text_view);
        customerStatusTextview = (TextView) findViewById(R.id.customer_status_text_view);
        customerLastVisitTextview = (TextView) findViewById(R.id.customer_last_visit_text_view);
        customerLastInvoiceTextview = (TextView) findViewById(R.id.customer_last_invoice_text_view);
        customerLastTrxTextview = (TextView) findViewById(R.id.customer_last_trx_text_view);
        customerExtra1Textview = (TextView) findViewById(R.id.customer_extra1_text_view);
        customerExtra2Textview = (TextView) findViewById(R.id.customer_extra2_text_view);
        customerExtra3Textview = (TextView) findViewById(R.id.customer_extra3_text_view);

        Long customerId = getIntent().getLongExtra(ARG_ITEM_ID, -1);
        customer = DataCenter.getInstance().getCustomerById(customerId);

        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(customer.getName());
        }

        customerNameTextview.setText(customer.getName());
        customerIdTextview.setText(customer.getCustomerId());
        customerZoneTextview.setText(customer.getZoneName());
        customerStatusTextview.setText(Customer.CustomerStatus.findByStatus(customer.getStatus()).toString());
        customerLastVisitTextview.setText(customer.getLastVisitedAtString());
        customerLastInvoiceTextview.setText(customer.getLastInvoiceAtString());
        customerLastTrxTextview.setText(customer.getLastTrxAmountString());
        customerExtra1Textview.setText(customer.getExtra1());
        customerExtra2Textview.setText(customer.getExtra2());
        customerExtra3Textview.setText(customer.getExtra3());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, CustomerListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
