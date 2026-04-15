package com.example.nourelhoudaapp.Services;

import com.example.nourelhoudaapp.entites.HorairePriere;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class PrayerTimeService {

    @Inject
    private CityCountryService cityCountryService;

    // Cache prayer times: key = "city_country_YYYY-MM-DD"
    private final Map<String, HorairePriere> prayerCache = new ConcurrentHashMap<>();

    // Cache hijri numbers: key = "city_country_YYYY-MM-DD", value = [month, day, year]
    private final Map<String, int[]> hijriCache = new ConcurrentHashMap<>();

    // ──────────────────────────────────────────────────────
    // Public API
    // ──────────────────────────────────────────────────────

    public HorairePriere getHoraireDuJour(String city) {
        return getHoraireDuJour(city, "Algeria");
    }

    public HorairePriere getHoraireDuJour(String city, String country) {
        if (city == null || city.trim().isEmpty()) city = "Alger";
        // If country is missing, auto-resolve from city via Nominatim
        if (country == null || country.trim().isEmpty()) {
            country = cityCountryService.getCountry(city);
        }

        LocalDate today = LocalDate.now();
        String key = cacheKey(city, country, today);

        if (!prayerCache.containsKey(key)) {
            fetchAndCache(city, country, today, key);
        }

        if (prayerCache.containsKey(key)) return prayerCache.get(key);

        // Fallback
        return new HorairePriere(city, today,
                LocalTime.of(5, 0), LocalTime.of(12, 45),
                LocalTime.of(16, 15), LocalTime.of(19, 30), LocalTime.of(20, 50));
    }

    /**
     * Returns day of Ramadan (1–30) if currently in Ramadan, -1 otherwise.
     */
    public int getJourRamadan(String city, String country) {
        ensureCached(city, country);
        int[] hijri = hijriCache.get(cacheKey(city, country, LocalDate.now()));
        if (hijri == null) {
            // Java fallback
            java.time.chrono.HijrahDate hd = java.time.chrono.HijrahDate.now();
            int m = hd.get(java.time.temporal.ChronoField.MONTH_OF_YEAR);
            int d = hd.get(java.time.temporal.ChronoField.DAY_OF_MONTH);
            return (m == 9) ? d : -1;
        }
        return (hijri[0] == 9) ? hijri[1] : -1;
    }

    /**
     * Returns current Hijri day (1–30).
     */
    public int getHijriDay(String city, String country) {
        ensureCached(city, country);
        int[] hijri = hijriCache.get(cacheKey(city, country, LocalDate.now()));
        if (hijri != null) return hijri[1];
        return java.time.chrono.HijrahDate.now().get(java.time.temporal.ChronoField.DAY_OF_MONTH);
    }

    /**
     * Returns current Hijri month number (1–12). 9 = Ramadan.
     */
    public int getHijriMonth(String city, String country) {
        ensureCached(city, country);
        int[] hijri = hijriCache.get(cacheKey(city, country, LocalDate.now()));
        if (hijri != null) return hijri[0];
        return java.time.chrono.HijrahDate.now().get(java.time.temporal.ChronoField.MONTH_OF_YEAR);
    }

    /**
     * Returns Arabic name of the current Hijri month from our own clean mapping
     * (avoids JSON unicode escape issues from the API).
     */
    public String getHijriMonthAr(String city, String country) {
        int month = getHijriMonth(city, country);
        return hijriMonthFallback(month);
    }

    /**
     * Returns current Hijri year.
     */
    public int getHijriYear(String city, String country) {
        ensureCached(city, country);
        int[] hijri = hijriCache.get(cacheKey(city, country, LocalDate.now()));
        if (hijri != null && hijri.length > 2) return hijri[2];
        return java.time.chrono.HijrahDate.now().get(java.time.temporal.ChronoField.YEAR);
    }

    // ──────────────────────────────────────────────────────
    // Internal helpers
    // ──────────────────────────────────────────────────────

    private String cacheKey(String city, String country, LocalDate date) {
        return city.toLowerCase() + "_" + country.toLowerCase() + "_" + date;
    }

    private void ensureCached(String city, String country) {
        if (city == null || city.trim().isEmpty()) city = "Alger";
        if (country == null || country.trim().isEmpty()) {
            country = cityCountryService.getCountry(city);
        }
        LocalDate today = LocalDate.now();
        String key = cacheKey(city, country, today);
        if (!hijriCache.containsKey(key)) {
            fetchAndCache(city, country, today, key);
        }
    }

    private void fetchAndCache(String city, String country, LocalDate date, String key) {
        String json = fetchRawJson(city, country);
        if (json == null) return;

        HorairePriere horaire = parsePrayerTimes(json, city, country, date);
        if (horaire != null) prayerCache.put(key, horaire);

        int[] hijri = parseHijriNumbers(json);
        if (hijri != null) hijriCache.put(key, hijri);
    }

    private String fetchRawJson(String city, String country) {
        try {
            String encodedCity    = URLEncoder.encode(city,    StandardCharsets.UTF_8.toString());
            String encodedCountry = URLEncoder.encode(country, StandardCharsets.UTF_8.toString());
            String urlStr = "http://api.aladhan.com/v1/timingsByCity?city=" + encodedCity
                    + "&country=" + encodedCountry + "&method=3";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();
                return sb.toString();
            }
        } catch (Exception e) {
            System.err.println("Error fetching prayer times: " + e.getMessage());
        }
        return null;
    }

    private HorairePriere parsePrayerTimes(String json, String city, String country, LocalDate date) {
        LocalTime fajr    = parseTime(json, "Fajr");
        LocalTime dhuhr   = parseTime(json, "Dhuhr");
        LocalTime asr     = parseTime(json, "Asr");
        LocalTime maghrib = parseTime(json, "Maghrib");
        LocalTime isha    = parseTime(json, "Isha");

        if (fajr != null && dhuhr != null && asr != null && maghrib != null && isha != null) {
            HorairePriere horaire = new HorairePriere(city, date, fajr, dhuhr, asr, maghrib, isha);
            
            try {
                Pattern latPat = Pattern.compile("\"latitude\"\\s*:\\s*([0-9.-]+)");
                Pattern lonPat = Pattern.compile("\"longitude\"\\s*:\\s*([0-9.-]+)");
                Matcher latMat = latPat.matcher(json);
                Matcher lonMat = lonPat.matcher(json);
                
                if (latMat.find() && lonMat.find()) {
                    double lat = Double.parseDouble(latMat.group(1));
                    double lon = Double.parseDouble(lonMat.group(1));

                    double qibla = calculateQibla(lat, lon);
                    horaire.setQiblaDegree(qibla);
                }
            } catch (Exception e) {
                System.err.println("Error parsing intelligent Qibla coordinates: " + e.getMessage());
            }
            
            return horaire;
        }
        return null;
    }

    /**
     * Calcule la direction de la Qibla (en degrés) par rapport au Nord,
     * en utilisant la formule de la trigonométrie sphérique.
     */
    public static double calculateQibla(double lat, double lon) {
        double phiM = Math.toRadians(21.422487); // Mecca Latitude
        double lambdaM = Math.toRadians(39.826206); // Mecca Longitude
        double phi = Math.toRadians(lat);
        double lambda = Math.toRadians(lon);

        double deltaLambda = lambdaM - lambda;
        double y = Math.sin(deltaLambda);
        double x = Math.cos(phi) * Math.tan(phiM) - Math.sin(phi) * Math.cos(deltaLambda);

        double qibla = Math.toDegrees(Math.atan2(y, x));
        return Math.round((qibla + 360) % 360);
    }

    /**
     * Returns [month, day, year] from the API JSON.
     * API returns: "hijri":{"day":"17","month":{"number":10,...},"year":"1447"}
     */
    private int[] parseHijriNumbers(String json) {
        try {
            // Find the hijri block first
            int hijriStart = json.indexOf("\"hijri\"");
            if (hijriStart == -1) return null;
            String hijriBlock = json.substring(hijriStart, Math.min(hijriStart + 600, json.length()));

            // Day
            Pattern dayPat = Pattern.compile("\"day\"\\s*:\\s*\"(\\d+)\"");
            Matcher dayMat = dayPat.matcher(hijriBlock);
            int day = dayMat.find() ? Integer.parseInt(dayMat.group(1)) : -1;

            // Month number
            Pattern monthPat = Pattern.compile("\"number\"\\s*:\\s*(\\d+)");
            Matcher monthMat = monthPat.matcher(hijriBlock);
            int month = monthMat.find() ? Integer.parseInt(monthMat.group(1)) : -1;

            // Year
            Pattern yearPat = Pattern.compile("\"year\"\\s*:\\s*\"(\\d+)\"");
            Matcher yearMat = yearPat.matcher(hijriBlock);
            int year = yearMat.find() ? Integer.parseInt(yearMat.group(1)) : 1447;

            if (day > 0 && month > 0) return new int[]{month, day, year};
        } catch (Exception e) {
            System.err.println("Error parsing Hijri numbers: " + e.getMessage());
        }
        return null;
    }

    /**
     * Extracts the Arabic month name from the month sub-object in the hijri block.
     * The JSON has both weekday.ar and month.ar — we must target month.ar specifically.
     */
    private String parseHijriMonthAr(String json) {
        try {
            // Find the hijri block
            int hijriIdx = json.indexOf("\"hijri\"");
            if (hijriIdx == -1) return null;
            String hijriBlock = json.substring(hijriIdx, Math.min(hijriIdx + 800, json.length()));

            // Find the month sub-object inside hijri
            int monthIdx = hijriBlock.indexOf("\"month\"");
            if (monthIdx == -1) return null;
            String monthBlock = hijriBlock.substring(monthIdx, Math.min(monthIdx + 200, hijriBlock.length()));

            // Now find "ar" inside the month block
            Pattern arPat = Pattern.compile("\"ar\"\\s*:\\s*\"([^\"]+)\"");
            Matcher arMat = arPat.matcher(monthBlock);
            if (arMat.find()) return arMat.group(1);
        } catch (Exception e) {
            System.err.println("Error parsing Hijri month name: " + e.getMessage());
        }
        return null;
    }

    private LocalTime parseTime(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([0-9]{2}:[0-9]{2})\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) return LocalTime.parse(matcher.group(1));
        return null;
    }

    public String hijriMonthFallback(int month) {
        String[] names = {
            "",
            "\u0645\u062d\u0631\u0645",         // 1  محرم
            "\u0635\u0641\u0631",               // 2  صفر
            "\u0631\u0628\u064a\u0639 \u0627\u0644\u0623\u0648\u0644", // 3  ربيع الأول
            "\u0631\u0628\u064a\u0639 \u0627\u0644\u062b\u0627\u0646\u064a", // 4  ربيع الثاني
            "\u062c\u0645\u0627\u062f\u0649 \u0627\u0644\u0623\u0648\u0644\u0649", // 5  جمادى الأولى
            "\u062c\u0645\u0627\u062f\u0649 \u0627\u0644\u062b\u0627\u0646\u064a\u0629", // 6  جمادى الثانية
            "\u0631\u062c\u0628",               // 7  رجب
            "\u0634\u0639\u0628\u0627\u0646",   // 8  شعبان
            "\u0631\u0645\u0636\u0627\u0646",   // 9  رمضان
            "\u0634\u0648\u0627\u0644",         // 10 شوال
            "\u0630\u0648 \u0627\u0644\u0642\u0639\u062f\u0629", // 11 ذو القعدة
            "\u0630\u0648 \u0627\u0644\u062d\u062c\u0629"  // 12 ذو الحجة
        };
        return (month >= 1 && month <= 12) ? names[month] : "";
    }
}
