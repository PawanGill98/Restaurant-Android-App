package me.cmpt276.restaurantinspector.Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *  stores restaurant's attributes
 */

public class Restaurant {
    private String trackingNumber;
    private String name;
    private String address;
    private String city;
    private String facilityType;
    private double latitude;
    private double longitude;
    private List<Inspection> inspections = new ArrayList<>();

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<Inspection> getInspections() {
        return inspections;
    }

    public boolean hasInspections() {
        return inspections.size() != 0;
    }

    public void addInspection(Inspection inspection) {
        inspections.add(inspection);
    }

    public void sortInspectionDates() {
        final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Collections.sort(inspections, new Comparator<Inspection>() {
            @Override
            public int compare(Inspection inspection1, Inspection inspection2) {
                try {
                    return dateFormat.parse(inspection1.getInspectionDate()).compareTo(dateFormat.parse(inspection2.getInspectionDate()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        Collections.reverse(inspections);
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "trackingNumber='" + trackingNumber + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", facilityType='" + facilityType + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", inspections=" + inspections +
                '}';
    }
}
