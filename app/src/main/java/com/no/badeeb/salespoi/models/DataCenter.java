package com.no.badeeb.salespoi.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DataCenter {
    private static DataCenter instance;

    private List<Customer> customers = new ArrayList<>();
    private Location userLocation = null;

    private DataCenter(){}

    public static DataCenter getInstance(){
        if(instance == null){
            return instance = new DataCenter();
        }
        return instance;
    }

    public boolean hasCustomers(){
        return !customers.isEmpty();
    }

    public Location getUserLocation(){
        return userLocation;
    }

    public void setUserLocation(Location location){
        userLocation = location;
    }

    public void add(List<Customer> customers){
        this.customers.addAll(customers);
    }

    public void clearCustomers(){
        customers.clear();
    }

    public Customer getCustomerById(Long id){
        for (Customer customer: customers) {
            if(customer.getId().equals(id)){
                return customer;
            }
        }
        return null;
    }

    public List<Customer> getCustomers(){
        return customers;
    }

}
