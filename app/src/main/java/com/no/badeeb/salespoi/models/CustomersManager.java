package com.no.badeeb.salespoi.models;

import java.util.ArrayList;
import java.util.List;

public class CustomersManager {
    private final static List<Customer> data = new ArrayList<>();

    public static void add(Customer customer){
        data.add(customer);
    }

    public static void add(List<Customer> customers){
        data.addAll(customers);
    }

    public static void clear(){
        data.clear();
    }

    public static Customer getByIndex(int index){
        return data.get(index);
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
