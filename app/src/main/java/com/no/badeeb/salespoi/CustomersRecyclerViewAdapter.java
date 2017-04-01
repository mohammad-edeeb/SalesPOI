package com.no.badeeb.salespoi;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.no.badeeb.salespoi.activities.CustomerDetailActivity;
import com.no.badeeb.salespoi.activities.CustomerListActivity;
import com.no.badeeb.salespoi.models.Customer;

/**
 * Created by meldeeb on 4/1/17.
 */

public class CustomersRecyclerViewAdapter extends RecyclerView.Adapter<CustomersRecyclerViewAdapter.ViewHolder>
        implements View.OnClickListener {

    CustomerListActivity activity;

    public CustomersRecyclerViewAdapter(CustomerListActivity activity) {
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Customer c = activity.getCustomers().get(position);
        holder.customer = c;
        holder.mIdView.setText(c.getCustomerId());
        holder.mContentView.setText(c.getName());

        Customer.CustomerStatus statusEnum = Customer.CustomerStatus.findByStatus(c.getStatus());
        holder.mView.setAlpha(0.7f);
        switch (statusEnum) {
            case Active:
                holder.mView.setBackgroundColor(activity.getResources().getColor(R.color.activeCustomer));
                break;
            case InProgress:
                holder.mView.setBackgroundColor(activity.getResources().getColor(R.color.inprogressCustomer));
                break;
            case Inactive:
                holder.mView.setBackgroundColor(activity.getResources().getColor(R.color.inactiveCustomer));
                break;
        }
        holder.mView.setTag(holder.customer);
        holder.mView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return activity.getCustomers().size();
    }

    @Override
    public void onClick(View v) {
        Context context = v.getContext();
        Intent intent = new Intent(context, CustomerDetailActivity.class);
        intent.putExtra(Constants.EXTRA_CUSTOMER, (Customer) v.getTag());
        intent.putParcelableArrayListExtra(Constants.EXTRA_CUSTOMERS, activity.getCustomers());
        context.startActivity(intent);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Customer customer;

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
