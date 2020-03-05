package me.cmpt276.restaurantinspector.Model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class CSVReader {
    private static RestaurantManager restaurantManager = RestaurantManager.getInstance();

    public static void readRestaurantData(InputStream is) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            // Step over headers
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                //Log.d("MyActivity", "Line: " + line);
                // Split by ','
                String[] tokens = line.split(",");

                // Read the data (excluding quotes for strings)
                Restaurant restaurant = new Restaurant();
                restaurant.setTrackingNumber(tokens[0].replace("\"", ""));
                restaurant.setName(tokens[1].replace("\"", ""));
                restaurant.setAddress(tokens[2].replace("\"", ""));
                restaurant.setCity(tokens[3].replace("\"", ""));
                restaurant.setFacilityType(tokens[4].replace("\"", ""));
                restaurant.setLatitude(Double.parseDouble(tokens[5]));
                restaurant.setLongitude(Double.parseDouble(tokens[6]));
                restaurantManager.addRestaurant(restaurant);
                //Log.d("MyActivity", "Just created: " + restaurant);
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }

    public static void readInspectionReportData(InputStream is) {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            // Step over headers
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                //Log.d("MyActivity", "Line: " + line);
                // Split by ','
                String[] tokens = line.split(",");

                // Read the data (excluding quotes for strings)
                Inspection inspection = new Inspection();
                inspection.setTrackingNumber(tokens[0].replace("\"", ""));
                inspection.setInspectionDate(tokens[1].replace("\"", ""));
                inspection.setInspectionType(tokens[2].replace("\"", ""));
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
                restaurantManager.addInspectionToRestaurant(inspection);
                //Log.d("MyActivity", "Just created: " + inspection);
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }
}
