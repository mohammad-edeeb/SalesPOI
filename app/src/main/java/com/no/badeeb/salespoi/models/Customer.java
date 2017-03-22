package com.no.badeeb.salespoi.models;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import static android.R.attr.format;
import static android.R.attr.key;

/**
 * Created by meldeeb on 3/18/17.
 */

public class Customer {


    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public enum CustomerStatus{
        Active(1), Inactive(2), InProgress(3);

        private int status;

        CustomerStatus(int status) {
            this.status = status;
        }

        public static CustomerStatus findByStatus(int status){
            for(CustomerStatus s : values()){
                if(s.status == status){
                    return s;
                }
            }
            return null;
        }
    }

    private Long id;
    @SerializedName("customer_id")
    private String customerId;
    @SerializedName("name")
    private String name;
    @SerializedName("long")
    private Double longitude;
    @SerializedName("lat")
    private Double latitude;
    @SerializedName("zone_name")
    private String zoneName;
    @SerializedName("salesman_name")
    private String salesManName;
    @SerializedName("status")
    private int status;
    @SerializedName("last_visited_at")
    private Date lastVisitedAt;
    @SerializedName("last_invoice_at")
    private Date lastInvoiceAt;
    @SerializedName("last_trx_amount")
    private Double lastTrxAmount;
    @SerializedName("extra1")
    private String extra1;
    @SerializedName("extra2")
    private String extra2;
    @SerializedName("extra3")
    private String extra3;

    public String getLastVisitedAtString(){
        if(lastVisitedAt == null){
            return "";
        }
        return DATE_FORMAT.format(lastVisitedAt);
    }

    public String getLastInvoiceAtString(){
        if(lastInvoiceAt == null){
            return "";
        }
        return DATE_FORMAT.format(lastInvoiceAt);
    }

    public String getLastTrxAmountString(){
        if(lastTrxAmount == null){
            return "";
        }
        return lastTrxAmount.toString();
    }

    public LatLng getPosition(){
        return new LatLng(latitude, longitude);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
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

    public Double getLastTrxAmount() {
        return lastTrxAmount;
    }

    public void setLastTrxAmount(Double lastTrxAmount) {
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
