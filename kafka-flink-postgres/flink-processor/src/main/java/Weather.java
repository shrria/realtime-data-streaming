import java.util.Objects;

public class Weather {

    /*
    {
    "city": "Berlin",
    "temperature": 20.0,
    }
    */

    public String city;
    public double temperature;

    public Weather() {
    }

    public Weather(String city, double temperature) {
        this.city = city;
        this.temperature = Double.valueOf(temperature);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Weather{");
        sb.append("city='").append(city).append('\'');
        sb.append(", temperature=").append(String.valueOf(temperature));
        sb.append('}');
        return sb.toString();
    }

    public int hashCode() {
    return Objects.hash(super.hashCode(), city, temperature);
    }

}