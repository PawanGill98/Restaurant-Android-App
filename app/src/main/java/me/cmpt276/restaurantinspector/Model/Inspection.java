package me.cmpt276.restaurantinspector.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * stores the restaurant's inspection
 */

public class Inspection {
    private static final int MONTH_START_INDEX = 4;
    private static final int MONTH_END_INDEX = 6;
    private static final int DAY_START_INDEX = 6;
    private static final int DAY_END_INDEX = 8;
    private static final int YEAR_START_INDEX = 0;
    private static final int YEAR_END_INDEX = 4;
    private static final int NUM_DAYS_MONTH = 30;
    private static final int NUM_DAYS_YEAR = 365;

    private String trackingNumber;
    private String inspectionDate;
    private String inspectionType;
    private int numCritical;
    private int numNonCritical;
    private String hazardRating;
    private int daysSinceInspection;
    private String fullInspectionDate;
    private String howLongAgo;

    private List<Violation> violations = new ArrayList<>();

    public String getHowLongAgo() {
        return howLongAgo;
    }

    public void setHowLongAgo(int daysSinceInspection) {
        if (daysSinceInspection <= NUM_DAYS_MONTH) {
            howLongAgo = daysSinceInspection + " days ago";
        }
        else if (daysSinceInspection <= NUM_DAYS_YEAR) {
            String month = Time.getMonthFromNumber(Integer.parseInt(inspectionDate.substring(MONTH_START_INDEX, MONTH_END_INDEX)));
            int dayNumber = Integer.parseInt(inspectionDate.substring(DAY_START_INDEX, DAY_END_INDEX));
            String day = Integer.toString(dayNumber);
            howLongAgo = month + " " + day;
        }
        else {
            String year = inspectionDate.substring(YEAR_START_INDEX, YEAR_END_INDEX);
            String month = Time.getMonthFromNumber(Integer.parseInt(inspectionDate.substring(MONTH_START_INDEX, MONTH_END_INDEX)));
            howLongAgo = month + " " + year;
        }
    }

    public String getFullInspectionDate() {
        return fullInspectionDate;
    }

    public void setFullInspectionDate(String date) {
        String year = date.substring(YEAR_START_INDEX, YEAR_END_INDEX);
        String month = Time.getMonthFromNumber(Integer.parseInt(date.substring(MONTH_START_INDEX, MONTH_END_INDEX)));
        int dayNumber = Integer.parseInt(date.substring(DAY_START_INDEX, DAY_END_INDEX));
        String day = Integer.toString(dayNumber);
        this.fullInspectionDate = month + " " + day + ", " + year;
    }

    public void setDaysSinceInspection(int daysSinceInspection) {
        this.daysSinceInspection = daysSinceInspection;
    }

    public void addViolation(Violation violation) {
        violations.add(violation);
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public int getNumCritical() {
        return numCritical;
    }

    public void setNumCritical(int numCritical) {
        this.numCritical = numCritical;
    }

    public int getNumNonCritical() {
        return numNonCritical;
    }

    public void setNumNonCritical(int numNonCritical) {
        this.numNonCritical = numNonCritical;
    }

    public String getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(String hazardRating) {
        this.hazardRating = hazardRating;
    }

    @Override
    public String toString() {
        return "Inspection{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", inspectionDate='" + inspectionDate + '\'' +
                ", inspectionType='" + inspectionType + '\'' +
                ", numCritical=" + numCritical +
                ", numNonCritical=" + numNonCritical +
                ", hazardRating='" + hazardRating + '\'' +
                ", daysSinceInspection=" + daysSinceInspection +
                ", fullInspectionDate='" + fullInspectionDate + '\'' +
                ", howLongAgo='" + howLongAgo + '\'' +
                ", violations=" + violations +
                '}';
    }
}
