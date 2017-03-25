package com.no.badeeb.salespoi.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DataCenter {
    private static List<Customer> data = new ArrayList<>();
    private static LatLng userLocation = null;

    public static LatLng getUserLocation(){
        return userLocation;
    }

    public static void setUserLocation(LatLng location){
        userLocation = location;
    }

    public static void add(Customer customer){
        data.add(customer);
    }

    public static void add(List<Customer> customers){
        data.addAll(customers);
    }

    public static void clear(){
        data.clear();
    }

    public static Customer getById(Long id){
        for (Customer customer: data) {
            if(customer.getId().equals(id)){
                return customer;
            }
        }
        return null;
    }

    public static List<Customer> getData(){
        return data;
    }

}
