package me.cmpt276.restaurantinspector.Model;

/**
 *  stores inspection's violation
 */

public class Violation {
    private int id;
    private String criticality;
    private String description;
    private String repeatability;
    private String briefDescription;

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public int getId() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getCriticality() {
        return criticality;
    }

    public void setCriticality(String criticality) {
        this.criticality = criticality;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRepeatability(String repeatability) {
        this.repeatability = repeatability;
    }

    @Override
    public String toString() {
        return "Violation{" +
                "id=" + id +
                ", criticality='" + criticality + '\'' +
                ", description='" + description + '\'' +
                ", repeatability='" + repeatability + '\'' +
                ", briefDescription='" + briefDescription + '\'' +
                '}';
    }
}
