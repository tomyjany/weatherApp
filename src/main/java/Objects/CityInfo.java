package Objects;

public class CityInfo {
    private String name;
    private double longitude;
    private double latitude;

    // constructor, getters, and setters
    public CityInfo(String name, double longitude, double latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}