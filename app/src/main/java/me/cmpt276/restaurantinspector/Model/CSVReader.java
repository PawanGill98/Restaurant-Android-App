package me.cmpt276.restaurantinspector.Model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    private static final int DESCRIPTION_START_INDEX = 4;
    public static final int BRIEF_DESC_FIRST_ATTRIBUTE = 0;
    public static final int BRIEF_DESC_SECOND_ATTRIBUTE = 1;

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
                // Split by ','
                String[] tokens = line.split(",");

                int index = 0;
                Restaurant restaurant = new Restaurant();
                restaurant.setTrackingNumber(tokens[index++].replace("\"", ""));
                String restaurantName = tokens[index++];

                // If name is surrounded by quotes
                if (restaurantName.startsWith("\"")) {
                    int nameIndex = index;

                    // Check for existing commas within name
                    while (!tokens[nameIndex].contains("\"")) {
                        restaurantName = restaurantName + "," + tokens[nameIndex];
                        nameIndex++;
                    }
                    if (!tokens[nameIndex].startsWith("\"")) {
                        restaurantName = restaurantName + "," + tokens[nameIndex];
                        nameIndex++;
                    }
                    index = nameIndex;
                }
                restaurant.setName(restaurantName.replace("\"", ""));
                restaurant.setAddress(tokens[index++].replace("\"", ""));
                restaurant.setCity(tokens[index++].replace("\"", ""));
                restaurant.setFacilityType(tokens[index++].replace("\"", ""));
                restaurant.setLatitude(Double.parseDouble(tokens[index++]));
                restaurant.setLongitude(Double.parseDouble(tokens[index]));

                // Add restaurant to list of restaurants
                restaurantManager.addRestaurant(restaurant);
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }

    public static void readInspectionReportData(InputStream is, InputStream is2) {
        List<String> briefDescriptions = readBriefDescription(is2);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            // Step over headers
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                // Split by ',' and '|'
                String[] tokens = line.split(",|\\|");

                int index = 0;

                // Read the data (excluding quotes for strings)
                Inspection inspection = new Inspection();
                inspection.setTrackingNumber(tokens[index++].replace("\"", ""));
                inspection.setInspectionDate(tokens[index++].replace("\"", ""));
                inspection.setInspectionType(tokens[index++].replace("\"", ""));
                inspection.setNumCritical(Integer.parseInt(tokens[index++]));
                inspection.setNumNonCritical(Integer.parseInt(tokens[index++]));
                inspection.setHazardRating(tokens[index++].replace("\"", ""));
                Time time = new Time();
                long daysSinceInspection = time.calculateDaysSince(inspection.getInspectionDate());
                inspection.setDaysSinceInspection((int) daysSinceInspection);
                inspection.setFullInspectionDate(inspection.getInspectionDate());
                inspection.setHowLongAgo((int) daysSinceInspection);

                // Check for empty violation lump
                if (!isColumnEmpty(tokens, index)) {

                    // Add all violations to inspection data
                    int count = index;
                    do {
                        Violation violation = new Violation();
                        violation.setID(Integer.parseInt(tokens[count++].replace("\"","")));
                        violation.setCriticality(tokens[count++]);
                        violation.setDescription(tokens[count++]);
                        violation.setRepeatability(tokens[count++].replace("\"", ""));
                        for (String description : briefDescriptions) {
                            if (description.startsWith(Integer.toString(violation.getId()))) {
                                violation.setBriefDescription(description.substring(DESCRIPTION_START_INDEX));
                            }
                        }
                        inspection.addViolation(violation);
                    } while (tokens.length >= count+1 && tokens[count].length() > 0);
                }

                // Add inspection to matching restaurant
                restaurantManager.addInspectionToRestaurant(inspection);

                for (Restaurant restaurant : restaurantManager.getRestaurants()) {
                    if (restaurant.hasInspections()) {
                        restaurant.sortInspectionDates();
                    }
                }
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }

    private static List<String> readBriefDescription(InputStream is) {
        List<String> briefDescriptions = new ArrayList<>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                briefDescriptions.add(tokens[BRIEF_DESC_FIRST_ATTRIBUTE] + "," + tokens[BRIEF_DESC_SECOND_ATTRIBUTE]);
            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
        return briefDescriptions;
    }

    private static boolean isColumnEmpty(String[] tokens, int col) {
        if (tokens.length > col+1 && tokens[col].length() > 0) {
            return false;
        }
        return true;
    }
}
