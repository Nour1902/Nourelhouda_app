package com.example.nourelhoudaapp.Services;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves a city name to its country (in English) using the
 * Nominatim OpenStreetMap API — free, no API key required.
 *
 * API: https://nominatim.openstreetmap.org/search?city={city}&format=json&addressdetails=1&limit=1
 */
@ApplicationScoped
public class CityCountryService {

    // Cache: city (lowercase) → country (English name)
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    /**
     * Returns the English country name for the given city.
     * Returns "Algeria" as fallback if resolution fails.
     */
    public String getCountry(String city) {
        if (city == null || city.trim().isEmpty()) return "Algeria";

        String key = city.trim().toLowerCase();
        if (cache.containsKey(key)) return cache.get(key);

        String country = fetchCountry(city);
        if (country != null && !country.isEmpty()) {
            cache.put(key, country);
            return country;
        }

        // Default fallback
        cache.put(key, "Algeria");
        return "Algeria";
    }

    private String fetchCountry(String city) {
        try {
            String encoded = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
            // Nominatim Terms of Service require a User-Agent header
            String urlStr = "https://nominatim.openstreetmap.org/search"
                    + "?city=" + encoded
                    + "&format=json&addressdetails=1&limit=1&accept-language=en";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            // Nominatim requires a User-Agent
            conn.setRequestProperty("User-Agent", "NourElHoudaApp/1.0");

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                return parseCountry(sb.toString());
            }
        } catch (Exception e) {
            System.err.println("[CityCountryService] Error resolving country for city '"
                    + city + "': " + e.getMessage());
        }
        return null;
    }

    /**
     * Parses the "country" field from Nominatim's JSON response.
     * The response is a JSON array: [{"address":{"country":"France",...},...},...]
     */
    private String parseCountry(String json) {
        if (json == null || json.trim().equals("[]")) return null;
        try {
            // Extract "country":"..." from the address block
            Pattern p = Pattern.compile("\"country\"\\s*:\\s*\"([^\"]+)\"");
            Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            System.err.println("[CityCountryService] Parse error: " + e.getMessage());
        }
        return null;
    }
}
