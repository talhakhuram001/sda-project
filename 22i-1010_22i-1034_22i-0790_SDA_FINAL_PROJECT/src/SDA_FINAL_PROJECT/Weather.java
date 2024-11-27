package SDA_FINAL_PROJECT;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Weather {
    private static final String API_KEY = "035327fb6ceaa4718c35acd481cfe274";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public static void main(String[] args) {
        String location = "London"; 
        try {
            String weatherData = getWeather(location);
            System.out.println("Weather Data: " + weatherData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getWeather(String location) throws Exception {
        String url = String.format("%s?q=%s&appid=%s&units=metric", BASE_URL, location, API_KEY);
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) { // Success
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString(); 
        } else {
            throw new RuntimeException("Error: HTTP code " + responseCode);
        }
    }

    public static void displayWeatherDetails(String jsonResponse) {
        try {
            
            String description = jsonResponse.split("\"description\":\"")[1].split("\"")[0];
            String temp = jsonResponse.split("\"temp\":")[1].split(",")[0];
            String windSpeed = jsonResponse.split("\"speed\":")[1].split(",")[0];

            System.out.println("Weather Information:");
            System.out.println("Description: " + description);
            System.out.println("Temperature: " + temp + " °C");
            System.out.println("Wind Speed: " + windSpeed + " m/s");
        } catch (Exception e) {
            System.out.println("Error parsing weather data.");
            e.printStackTrace();
        }
    }}
