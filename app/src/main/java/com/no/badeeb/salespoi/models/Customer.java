package com.no.badeeb.salespoi.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by meldeeb on 3/18/17.
 */

public class Customer {

    private Long id;
    private String customerId;
    private String customerName;
    private double longitude, latitude;
    private String zoneName;
    private String salesManName;
    private int status;
    private Date lastVisitedAt;
    private Date lastInvoiceAt;
    private double lastTrxAmount;
    private String extra1, extra2, extra3;

    public static Customer fromJSON(JSONObject json) throws JSONException {
        Customer customer = new Customer();
        customer.id = json.getLong("id");
        customer.customerId = json.getString("customer_id");
        customer.customerName = json.getString("name");
        customer.longitude = json.getDouble("long");
        customer.latitude = json.getDouble("lat");
        customer.zoneName = json.getString("zone_name");
        customer.salesManName = json.getString("salesman_name");
        customer.status = json.getInt("status");
        customer.lastVisitedAt = parseDate(json.getString("last_visited_at"));
        customer.lastInvoiceAt = parseDate(json.getString("last_invoice_at"));
        customer.lastTrxAmount = json.getDouble("last_trx_amount");
        customer.extra1 = json.getString("extra1");
        customer.extra2 = json.getString("extra2");
        customer.extra3 = json.getString("extra3");
        return customer;
    }

    private static Date parseDate(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getSalesManName() {
        return salesManName;
    }

    public void setSalesManName(String salesManName) {
        this.salesManName = salesManName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getLastVisitedAt() {
        return lastVisitedAt;
    }

    public void setLastVisitedAt(Date lastVisitedAt) {
        this.lastVisitedAt = lastVisitedAt;
    }

    public Date getLastInvoiceAt() {
        return lastInvoiceAt;
    }

    public void setLastInvoiceAt(Date lastInvoiceAt) {
        this.lastInvoiceAt = lastInvoiceAt;
    }

    public double getLastTrxAmount() {
        return lastTrxAmount;
    }

    public void setLastTrxAmount(double lastTrxAmount) {
        this.lastTrxAmount = lastTrxAmount;
    }

    public String getExtra1() {
        return extra1;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public String getExtra3() {
        return extra3;
    }

    public void setExtra3(String extra3) {
        this.extra3 = extra3;
    }

}
