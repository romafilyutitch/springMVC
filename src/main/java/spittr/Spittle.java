package spittr;

import java.util.Date;
import java.util.Objects;

public class Spittle {
    private final Long id;
    private final String message;
    private final Date time;
    private final Double latitude;
    private final Double longitude;

    public Spittle(String message, Date time) {
        this(message, time, null, null);
    }

    public Spittle(String message, Date time, Double longitude, Double latitude) {
        this.id = null;
        this.message = message;
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Date getTime() {
        return time;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spittle spittle = (Spittle) o;
        return Objects.equals(id, spittle.id) && Objects.equals(message, spittle.message) && Objects.equals(time, spittle.time) && Objects.equals(latitude, spittle.latitude) && Objects.equals(longitude, spittle.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, time, latitude, longitude);
    }

    @Override
    public String toString() {
        return "Spittle{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", time=" + time +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
