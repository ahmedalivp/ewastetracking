package com.ewaste.app.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.ewaste.app.data.model.DropOffPointResponse;
import java.util.List;

public final class MapHelper {

    private MapHelper() {}

    public interface OnMarkerClickListener {
        void onMarkerClicked(long dropOffPointId);
    }

    private static class MapBridge {
        private final OnMarkerClickListener listener;

        MapBridge(OnMarkerClickListener listener) {
            this.listener = listener;
        }

        @JavascriptInterface
        public void onMarkerClicked(long id) {
            if (listener != null) {
                listener.onMarkerClicked(id);
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public static void renderMultiPointMap(WebView webView, List<DropOffPointResponse> points, OnMarkerClickListener listener) {
        if (webView == null) return;

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUserAgentString("EWasteTrackerApp/1.0 (Android; Mobile)");
        webView.setWebViewClient(new WebViewClient());

        if (listener != null) {
            webView.addJavascriptInterface(new MapBridge(listener), "AndroidBridge");
        }

        StringBuilder markersJs = new StringBuilder();
        double centerLat = 37.7749;
        double centerLng = -122.4194;
        boolean hasPoints = points != null && !points.isEmpty();

        if (hasPoints) {
            centerLat = points.get(0).getLat() != null ? points.get(0).getLat() : 37.7749;
            centerLng = points.get(0).getLng() != null ? points.get(0).getLng() : -122.4194;

            for (DropOffPointResponse pt : points) {
                if (pt.getLat() == null || pt.getLng() == null) continue;
                String safeTitle = pt.getLabel() != null ? pt.getLabel().replace("'", "\\'") : "Drop-Off Hub";
                String safeFacility = pt.getFacilityName() != null ? pt.getFacilityName().replace("'", "\\'") : "Recycling Hub";
                String safeAddress = pt.getAddress() != null ? pt.getAddress().replace("'", "\\'") : "";
                String safeHours = pt.getOperatingHours() != null ? pt.getOperatingHours().replace("'", "\\'") : "Open for drop-off";
                long id = pt.getId() != null ? pt.getId() : 0;

                markersJs.append("addHubMarker(")
                        .append(pt.getLat()).append(", ")
                        .append(pt.getLng()).append(", ")
                        .append("'").append(safeTitle).append("', ")
                        .append("'").append(safeFacility).append("', ")
                        .append("'").append(safeAddress).append("', ")
                        .append("'").append(safeHours).append("', ")
                        .append(id).append(");\n");
            }
        }

        int nightModeFlags = webView.getContext().getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        boolean isDark = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;

        String bgMap = isDark ? "#121214" : "#FAF8F5";
        String tileFilter = isDark ? ".leaflet-tile-pane { filter: invert(100%) hue-rotate(180deg) brightness(85%) contrast(90%); } " : "";
        String popupWrapperStyle = isDark
                ? "background: #1A1A1E; color: #F4F4F5; border: 1px solid #2A2A32; box-shadow: 0 8px 24px rgba(0,0,0,0.5);"
                : "background: #FFFFFF; color: #18181B; border: 1px solid #E5E0D8; box-shadow: 0 8px 24px rgba(0,0,0,0.08);";
        String popupTipBg = isDark ? "#1A1A1E" : "#FFFFFF";
        String hubTitleColor = isDark ? "#86EFAC" : "#1F3D2B";
        String hubSubColor = isDark ? "#BBF7D0" : "#3B6B4C";
        String hubAddrColor = isDark ? "#A1A1AA" : "#71717A";
        String hubHoursBg = isDark ? "#163824" : "#E8EFEA";
        String hubHoursColor = isDark ? "#BBF7D0" : "#1F3D2B";
        String markerStrokeColor = isDark ? "#163824" : "#14291D";
        String markerFillColor = isDark ? "#86EFAC" : "#1F3D2B";

        String html = "<!DOCTYPE html><html><head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no' />" +
                "<link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'/>" +
                "<script src='https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'></script>" +
                "<style>" +
                "html, body, #map { height: 100%; margin: 0; padding: 0; background: " + bgMap + "; } " +
                tileFilter +
                ".leaflet-popup-content-wrapper { border-radius: 14px; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; font-size: 13px; " + popupWrapperStyle + " } " +
                ".leaflet-popup-tip { background: " + popupTipBg + "; }" +
                ".hub-popup { padding: 4px 2px; }" +
                ".hub-title { font-weight: 700; color: " + hubTitleColor + "; font-size: 14px; margin-bottom: 2px; }" +
                ".hub-sub { color: " + hubSubColor + "; font-size: 11px; font-weight: 600; margin-bottom: 4px; }" +
                ".hub-addr { color: " + hubAddrColor + "; font-size: 11px; line-height: 1.3; margin-bottom: 4px; }" +
                ".hub-hours { display: inline-block; background: " + hubHoursBg + "; color: " + hubHoursColor + "; border-radius: 6px; padding: 2px 6px; font-size: 10px; font-weight: 600; }" +
                "</style>" +
                "</head><body><div id='map'></div><script>" +
                "var map = L.map('map', {zoomControl: false}).setView([" + centerLat + ", " + centerLng + "], 13);" +
                "L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', { maxZoom: 19 }).addTo(map);" +
                "var markers = {};" +
                "var group = L.featureGroup();" +
                "function addHubMarker(lat, lng, title, facility, addr, hours, id) {" +
                "  var marker = L.circleMarker([lat, lng], {" +
                "    radius: 11," +
                "    color: '" + markerStrokeColor + "'," +
                "    weight: 2.5," +
                "    fillColor: '" + markerFillColor + "'," +
                "    fillOpacity: 0.95" +
                "  }).addTo(map);" +
                "  var popupHtml = \"<div class='hub-popup'><div class='hub-title'>\" + title + \"</div><div class='hub-sub'>\" + facility + \"</div><div class='hub-addr'>\" + addr + \"</div><div class='hub-hours'>\" + hours + \"</div></div>\";" +
                "  marker.bindPopup(popupHtml);" +
                "  marker.on('click', function() {" +
                "    if (window.AndroidBridge && window.AndroidBridge.onMarkerClicked) {" +
                "      window.AndroidBridge.onMarkerClicked(id);" +
                "    }" +
                "  });" +
                "  markers[id] = marker;" +
                "  group.addLayer(marker);" +
                "}" +
                markersJs.toString() +
                (hasPoints ? "if (group.getLayers().length > 1) { map.fitBounds(group.getBounds().pad(0.15)); }" : "") +
                "window.centerOn = function(lat, lng, zoom, id) {" +
                "  map.setView([lat, lng], zoom || 15, {animate: true});" +
                "  if (id && markers[id]) { markers[id].openPopup(); }" +
                "};" +
                "window.fitAll = function() {" +
                "  if (group.getLayers().length > 0) { map.fitBounds(group.getBounds().pad(0.15)); }" +
                "};" +
                "</script></body></html>";

        webView.loadDataWithBaseURL("https://openstreetmap.org", html, "text/html", "UTF-8", null);
    }

    public static void centerMapOn(WebView webView, double lat, double lng, int zoom, long id) {
        if (webView == null) return;
        webView.evaluateJavascript("if (window.centerOn) { window.centerOn(" + lat + ", " + lng + ", " + zoom + ", " + id + "); }", null);
    }

    public static void fitAllMarkers(WebView webView) {
        if (webView == null) return;
        webView.evaluateJavascript("if (window.fitAll) { window.fitAll(); }", null);
    }

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
                "L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', { maxZoom: 19, subdomains: 'abcd' }).addTo(map);" +
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
            // 1. Google Maps turn-by-turn navigation intent
            Uri navUri = Uri.parse("google.navigation:q=" + lat + "," + lng + "&mode=d");
            Intent navIntent = new Intent(Intent.ACTION_VIEW, navUri);
            navIntent.setPackage("com.google.android.apps.maps");
            if (navIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(navIntent);
                return;
            }

            // 2. Generic geo intent with label
            Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(" + Uri.encode(label) + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
                return;
            }

            // 3. Web fallback to official Google Maps directions URL
            Uri webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + lat + "," + lng);
            context.startActivity(new Intent(Intent.ACTION_VIEW, webUri));
        } catch (Exception e) {
            Toast.makeText(context, "Unable to launch Google Maps", Toast.LENGTH_SHORT).show();
        }
    }
}
