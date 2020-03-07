package me.cmpt276.restaurantinspector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import me.cmpt276.restaurantinspector.Model.CSVReader;
import me.cmpt276.restaurantinspector.Model.Inspection;
import me.cmpt276.restaurantinspector.Model.Restaurant;
import me.cmpt276.restaurantinspector.Model.RestaurantManager;
import me.cmpt276.restaurantinspector.Model.Violation;

public class MainActivity extends AppCompatActivity {

    private RestaurantManager restaurantManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restaurantManager = restaurantManager.getInstance();

        CSVReader.readRestaurantData(getResources().openRawResource(R.raw.restaurants_itr1));
        CSVReader.readInspectionReportData(getResources().openRawResource(R.raw.inspectionreports_itr1));

        /**
        // Iterate through all restaurants
        for (Restaurant restaurant : restaurantManager.getRestaurants()) {
            Log.d("Restaurants list: ", restaurant + "");
        }

        // Get restaurant by tracking number. Can also get by index using getRestaurantByIndex(int);
        Restaurant restaurant = restaurantManager.getRestaurantByTrackingNumber("SDFO-8HKP7E");
        Log.d("Individual restaurant: ", restaurant + "");

        // List of inspections from restaurant above
        List<Inspection> inspections = restaurant.getInspections();
        for (Inspection inspection : inspections) {
            Log.d("Inspections list: ", inspection + "");

            // List of violations from inspection above
            List<Violation> violations = inspection.getViolations();
            for (Violation violation : violations) {
                Log.d("Violations list: ", violation + "");
            }
        }
         */
    }
}
