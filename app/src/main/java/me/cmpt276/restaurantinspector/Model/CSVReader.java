package me.cmpt276.restaurantinspector.Model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Read data from csv files and stores in data structure
 */
public class CSVReader {

    private static final int DESCRIPTION_START_INDEX = 4;
    private static final int VIOLATION_START_INDEX = 4;

    public static final int BRIEF_DESC_FIRST_ATTRIBUTE = 0;
    public static final int BRIEF_DESC_SECOND_ATTRIBUTE = 1;

    public static final int ALL_VIOL_FIRST_ATTRIBUTE = 0;
    public static final int ALL_VIOL_SECOND_ATTRIBUTE = 1;
    public static final String NOT_REPEAT = "Not Repeat";
    public static final String REPEAT = "Repeat";
    public static final String HIGH = "High";
    public static final String MODERATE = "Moderate";
    public static final String LOW = "Low";
    public static final String UNDEFINED = "Undefined";
    public static final int ID_309 = 309;
    public static final int ID_502 = 502;

    private static RestaurantManager restaurantManager = RestaurantManager.getInstance();

    public static void readRestaurantData(FileInputStream fis) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        String line = "";
        try {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                int index = 0;
                Restaurant restaurant = new Restaurant();
                restaurant.setTrackingNumber(tokens[index++].replace("\"", ""));
                String restaurantName = tokens[index++];
                if (restaurantName.startsWith("\"")) {
                    int nameIndex = index;

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
                restaurantManager.addRestaurant(restaurant);
            }
            restaurantManager.sortRestaurantNames();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void readUpdatedInspectionReportData(InputStream is, InputStream is2, InputStream is3) {
        List<String> briefDescriptions = readBriefDescription(is2);
        List<String> allViolations = readAllViolations(is3);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try {
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                int index = 0;
                String[] tokens = line.split(",");
                if (tokens.length <= 0) {
                    break;
                }
                Inspection inspection = new Inspection();
                inspection.setTrackingNumber(tokens[index++].replace("\"", ""));
                inspection.setInspectionDate(tokens[index++].replace("\"", ""));
                inspection.setInspectionType(tokens[index++].replace("\"", ""));
                inspection.setNumCritical(Integer.parseInt(tokens[index++].replace("\"", "")));
                inspection.setNumNonCritical(Integer.parseInt(tokens[index++].replace("\"", "")));

                if (!isColumnEmpty(tokens, index)) {
                    int count = index;
                    Violation violation = new Violation();
                    violation.setID(Integer.parseInt(tokens[count++].replace("\"", "")));
                    violation.setCriticality(tokens[count++].replace("\"", ""));



                    for (String description : allViolations) {
                        if (description.startsWith(Integer.toString(violation.getId()))) {
                            violation.setDescription(description.substring(VIOLATION_START_INDEX));
                        }
                    }
                    count++;
                    if (violation.getId() == ID_502) {
                        count++;
                    }
                    if (violation.getId() == ID_309) {
                        count = count + 2;
                    }

                    for (String description : briefDescriptions) {
                        if (description.startsWith(Integer.toString(violation.getId()))) {
                            violation.setBriefDescription(description.substring(DESCRIPTION_START_INDEX));
                        }
                    }

                    if (tokens[count].startsWith(NOT_REPEAT)) {
                        violation.setRepeatability(NOT_REPEAT);
                    } else {
                        violation.setRepeatability(REPEAT);
                    }
                    inspection.addViolation(violation);

                    while (tokens[count].contains("|")) {
                        String[] tokens2 = tokens[count++].split("\\|");
                        Violation nextViolation = new Violation();

                        nextViolation.setID(Integer.parseInt(tokens2[1].substring(0, 3)));
                        nextViolation.setCriticality(tokens[count++]);

                        for (String description : allViolations) {
                            if (description.startsWith(Integer.toString(nextViolation.getId()))) {
                                nextViolation.setDescription(description.substring(VIOLATION_START_INDEX));
                            }
                        }
                        count++;
                        if (nextViolation.getId() == ID_502) {
                            count++;
                        }
                        if (nextViolation.getId() == ID_309) {
                            count++;
                            count++;
                        }
                        for (String description : briefDescriptions) {
                            if (description.startsWith(Integer.toString(nextViolation.getId()))) {
                                nextViolation.setBriefDescription(description.substring(DESCRIPTION_START_INDEX));
                            }
                        }
                        if (tokens[count].startsWith(NOT_REPEAT)) {
                            nextViolation.setRepeatability(NOT_REPEAT);
                        }
                        else {
                            nextViolation.setRepeatability(REPEAT);
                        }
                        inspection.addViolation(nextViolation);
                    }

                }
                if (!tokens[tokens.length-1].equals(HIGH) && !tokens[tokens.length - 1].equals(MODERATE) && !tokens[tokens.length - 1].equals(LOW)) {
                    inspection.setHazardRating(UNDEFINED);
                } else {
                    inspection.setHazardRating(tokens[tokens.length-1]);
                }
                Time time = new Time();
                long daysSinceInspection = time.calculateDaysSince(inspection.getInspectionDate());
                inspection.setDaysSinceInspection((int) daysSinceInspection);
                inspection.setFullInspectionDate(inspection.getInspectionDate());
                inspection.setHowLongAgo((int) daysSinceInspection);
                restaurantManager.addInspectionToRestaurant(inspection);
            }
        } catch (IOException e) {
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
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",|\\|");

                int index = 0;

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

                if (!isColumnEmpty(tokens, index)) {

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

                restaurantManager.addInspectionToRestaurant(inspection);

                for (Restaurant restaurant : restaurantManager.getRestaurants()) {
                    if (restaurant.hasInspections()) {
                        restaurant.sortInspectionDates();
                    }
                }
            }
        } catch (IOException e) {
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
            e.printStackTrace();
        }
        return briefDescriptions;
    }

    private static List<String> readAllViolations(InputStream is) {
        List<String> allViolations = new ArrayList<>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                allViolations.add(tokens[ALL_VIOL_FIRST_ATTRIBUTE] + "," + tokens[ALL_VIOL_SECOND_ATTRIBUTE]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allViolations;
    }

    private static boolean isColumnEmpty(String[] tokens, int col) {
        return tokens.length <= col + 1 || tokens[col].length() <= 0;
    }
}
