package me.cmpt276.restaurantinspector.Model;

public class Violation {
    private int id;
    private String criticality;
    private String description;
    private String repeatability;

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

    public String getRepeatability() {
        return repeatability;
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
                '}';
    }
}
