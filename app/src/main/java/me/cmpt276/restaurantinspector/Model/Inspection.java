package me.cmpt276.restaurantinspector.Model;

import java.util.ArrayList;
import java.util.List;

public class Inspection {
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
        if (daysSinceInspection <= 30) {
            howLongAgo = daysSinceInspection + " days ago";
        }
        else if (daysSinceInspection <= 365) {
            String month = Time.getMonthFromNumber(Integer.parseInt(inspectionDate.substring(4, 6)));
            int dayNumber = Integer.parseInt(inspectionDate.substring(6, 8));
            String day = Integer.toString(dayNumber);
            howLongAgo = month + " " + day;
        }
        else {
            String year = inspectionDate.substring(0, 4);
            String month = Time.getMonthFromNumber(Integer.parseInt(inspectionDate.substring(4, 6)));
            howLongAgo = month + " " + year;
        }
    }

    public String getFullInspectionDate() {
        return fullInspectionDate;
    }

    public void setFullInspectionDate(String date) {
        String year = date.substring(0, 4);
        String month = Time.getMonthFromNumber(Integer.parseInt(date.substring(4, 6)));
        int dayNumber = Integer.parseInt(date.substring(6, 8));
        String day = Integer.toString(dayNumber);
        this.fullInspectionDate = month + " " + day + ", " + year;
    }

    public int getDaysSinceInspection() {
        return daysSinceInspection;
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

    public void setViolations(List<Violation> violations) {
        this.violations = violations;
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
