package com.ewaste.app.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public final class MapHelper {

    private MapHelper() {}

    @SuppressLint("SetJavaScriptEnabled")
    public static void renderMap(WebView webView, double lat, double lng, String title, String subtitle) {
        if (webView == null) return;

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        String safeTitle = title != null ? title.replace("'", "\\'") : "Drop-Off Station";
        String safeSubtitle = subtitle != null ? subtitle.replace("'", "\\'") : "Certified E-Waste Hub";

        String html = "<!DOCTYPE html><html><head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no' />" +
                "<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'/>" +
                "<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>" +
                "<style>" +
                "html, body, #map { height: 100%; margin: 0; padding: 0; background: #EEF4F0; } " +
                ".leaflet-popup-content-wrapper { border-radius: 12px; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; font-size: 13px; color: #0F172A; box-shadow: 0 4px 12px rgba(0,0,0,0.15); } " +
                ".leaflet-popup-tip { background: white; }" +
                ".hub-title { font-weight: bold; color: #0D5C3A; font-size: 14px; margin-bottom: 2px; }" +
                ".hub-sub { color: #64748B; font-size: 12px; }" +
                "</style>" +
                "</head><body><div id='map'></div><script>" +
                "var map = L.map('map', {zoomControl: false}).setView([" + lat + ", " + lng + "], 15);" +
                "L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 19 }).addTo(map);" +
                "var circle = L.circleMarker([" + lat + ", " + lng + "], {" +
                "  radius: 12," +
                "  color: '#0D5C3A'," +
                "  weight: 3," +
                "  fillColor: '#10B981'," +
                "  fillOpacity: 0.9" +
                "}).addTo(map);" +
                "circle.bindPopup(\"<div class='hub-title'>" + safeTitle + "</div><div class='hub-sub'>" + safeSubtitle + "</div>\").openPopup();" +
                "</script></body></html>";

        webView.loadDataWithBaseURL("https://openstreetmap.org", html, "text/html", "UTF-8", null);
    }

    public static void launchNavigation(Context context, double lat, double lng, String label) {
        if (context == null) return;
        try {
            Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(" + Uri.encode(label) + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
                return;
            }

            // Generic geo intent
            Intent genericIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            if (genericIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(genericIntent);
                return;
            }

            // Web fallback
            Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng);
            context.startActivity(new Intent(Intent.ACTION_VIEW, webUri));
        } catch (Exception e) {
            Toast.makeText(context, "Unable to launch maps", Toast.LENGTH_SHORT).show();
        }
    }
}
