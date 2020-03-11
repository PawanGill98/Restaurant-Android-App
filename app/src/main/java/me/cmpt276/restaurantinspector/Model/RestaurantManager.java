package me.cmpt276.restaurantinspector.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 *  stores list of restaurant
 */

public class RestaurantManager implements Iterable<Restaurant> {
    private static RestaurantManager instance;

    private RestaurantManager() {
    }

    public static RestaurantManager getInstance() {
        if (instance == null) {
            instance = new RestaurantManager();
        }
        return instance;
    }

    private List<Restaurant> restaurants = new ArrayList<>();

    public void sortRestaurantNames() {
        Collections.sort(restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant restaurant1, Restaurant restaurant2) {
                return restaurant1.getName().compareToIgnoreCase(restaurant2.getName());
            }
        });
    }

    public Restaurant getRestaurantByIndex(int index) {
        return restaurants.get(index);
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

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    @Override
    public Iterator<Restaurant> iterator() {
        return restaurants.iterator();
    }
}
