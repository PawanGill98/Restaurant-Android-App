package me.cmpt276.restaurantinspector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import me.cmpt276.restaurantinspector.Model.Restaurant;

public class SingleRestaurantInspection extends AppCompatActivity {

    private static Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signle_restaurant_inspection);
        setupTextView();
    }

    private void setupTextView() {
        TextView view = findViewById(R.id.screen2_restaurant_name);
        view.setText(restaurant.getName());
        view = findViewById(R.id.screen2_restaurant_address);
        view.setText(restaurant.getAddress());
        view = findViewById(R.id.screen2_restaurant_coords);
        view.setText("(" + restaurant.getLatitude() + ", "+ restaurant.getLongitude() + ")");
    }

    public static Intent makeIntent(Context c, Restaurant restaurant){
        SingleRestaurantInspection.restaurant = restaurant;
        return new Intent(c, SingleRestaurantInspection.class);
    }
}
