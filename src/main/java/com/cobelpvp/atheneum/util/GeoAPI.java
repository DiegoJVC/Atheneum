package com.cobelpvp.atheneum.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Utility class for resolving geographical information about players.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GeoAPI {

    /**
     * The cached responses per IP addresses
     */
    private static final StrictMap<String, GeoResponse> cache = new StrictMap<>();

    /**
     * Returns a {@link GeoResponse} with geographic data for the given IP address
     *
     * @param ip
     * @return
     */
    public static GeoResponse getCountry(InetAddress ip) {
        GeoResponse response = new GeoResponse("", "", "", "");

        if (ip == null)
            return response;

        if (ip.getHostName().equals("127.0.0.1") || ip.getHostName().equals("0.0.0.0"))
            return new GeoResponse("local", "-", "local", "-");

        if (cache.contains(ip.toString()))
            return cache.get(ip.toString());

        try {
            final URL url = new URL("http://ip-api.com/json/" + ip.getHostName());
            final URLConnection con = url.openConnection();
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);

            try (final BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String page = "";
                String input;

                while ((input = r.readLine()) != null)
                    page += input;

                response = new GeoResponse(getJson(page, "country"), getJson(page, "countryCode"), getJson(page, "regionName"), getJson(page, "isp"));
                cache.put(ip.toString(), response);
            }

        } catch (final NoRouteToHostException ex) {
            // Firewall or internet access denied

        } catch (final SocketTimeoutException ex) { // hide
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return response;
    }

    private static String getJson(String page, String element) {
        return page.contains("\"" + element + "\":\"") ? page.split("\"" + element + "\":\"")[1].split("\",")[0] : "";
    }

    /**
     * The response we get from an external server, cached since the country does not change for the IP does it? :)
     */
    @RequiredArgsConstructor
    @Getter
    public static final class GeoResponse {
        private final String countryName, countryCode, regionName, isp;
    }
}