package me.cmpt276.restaurantinspector.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RestaurantManager implements Iterable<Restaurant> {
    // Singleton design
    private static RestaurantManager instance;

    private RestaurantManager() {
        // Prevent others from instantiating
    }

    public static RestaurantManager getInstance() {
        if (instance == null) {
            instance = new RestaurantManager();
        }
        return instance;
    }

    private List<Restaurant> restaurants = new ArrayList<>();

    public Restaurant getRestaurantByTrackingNumber(String trackingNumber) {
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getTrackingNumber().equals(trackingNumber)) {
                return restaurant;
            }
        }
        return null;
    }

    public void addInspectionToRestaurant(Inspection inspection) {
        for (Restaurant restaurant : restaurants) {
            if (inspection.getTrackingNumber().equals(restaurant.getTrackingNumber())) {
                restaurant.addInspection(inspection);
            }
        }
    }

    public void addRestaurant(Restaurant restaurant) {
        restaurants.add(restaurant);
    }

    public List getList() {
        return restaurants;
    }

    public void remove(int index) {
        restaurants.remove(index);
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurants.iterator();
    }
}
