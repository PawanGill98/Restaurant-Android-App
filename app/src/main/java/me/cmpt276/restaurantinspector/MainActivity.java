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

import me.cmpt276.restaurantinspector.Model.Inspection;
import me.cmpt276.restaurantinspector.Model.Restaurant;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readRestaurantData();
        readInspectionReportData();
    }

    private List<Restaurant> restaurants = new ArrayList<>();

    private void readRestaurantData() {
        InputStream is = getResources().openRawResource(R.raw.restaurants_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            // Step over headers
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                Log.d("MyActivity", "Line: " + line);
                // Split by ','
                String[] tokens = line.split(",");

                // Read the data
                Restaurant restaurant = new Restaurant();
                restaurant.setTrackingNumber(tokens[0]);
                restaurant.setName(tokens[1]);
                restaurant.setAddress(tokens[2]);
                restaurant.setCity(tokens[3]);
                restaurant.setFacilityType(tokens[4]);
                restaurant.setLatitude(Double.parseDouble(tokens[5]));
                restaurant.setLongitude(Double.parseDouble(tokens[6]));
                restaurants.add(restaurant);
                Log.d("MyActivity", "Just created: " + restaurant);
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }

    private void readInspectionReportData() {
        InputStream is = getResources().openRawResource(R.raw.inspectionreports_itr1);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            // Step over headers
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                Log.d("MyActivity", "Line: " + line);
                // Split by ','
                String[] tokens = line.split(",");

                // Read the data
                Inspection inspection = new Inspection();
                inspection.setTrackingNumber(tokens[0]);
                inspection.setInspectionDate(tokens[1]);
                inspection.setInspectionType(tokens[2]);
                inspection.setNumCritical(Integer.parseInt(tokens[3]));
                inspection.setNumNonCritical(Integer.parseInt(tokens[4]));
                inspection.setHazardRating(tokens[5]);

                // Check for empty violation lump
                if (tokens.length >= 7 && tokens[6].length() > 0) {
                    inspection.setViolationLump(tokens[6]);
                } else {
                    inspection.setViolationLump("");
                }

                // Add inspection to matching restaurant
                for (Restaurant restaurant : restaurants) {
                    if (inspection.getTrackingNumber().equals(restaurant.getTrackingNumber())) {
                        restaurant.addInspection(inspection);
                    }
                }
                Log.d("MyActivity", "Just created: " + inspection);
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }
}
