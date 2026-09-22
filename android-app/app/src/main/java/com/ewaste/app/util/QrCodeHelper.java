package com.ewaste.app.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.annotation.ColorInt;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

public final class QrCodeHelper {

    private static final int DEFAULT_FOREGROUND = 0xFF0D5C3A; // Forest Emerald
    private static final int DEFAULT_BACKGROUND = 0xFFFFFFFF; // White

    private QrCodeHelper() {}

    public static Bitmap generateQrCode(String content, int size) {
        return generateQrCode(content, size, DEFAULT_FOREGROUND, DEFAULT_BACKGROUND);
    }

    public static Bitmap generateQrCode(String content, int size, @ColorInt int foregroundColor, @ColorInt int backgroundColor) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    size,
                    size,
                    hints
            );

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];

            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? foregroundColor : backgroundColor;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatPassportToken(Long submissionId) {
        if (submissionId == null) return "EWASTE-0000";
        return String.format("EWASTE-%04d", submissionId);
    }

    public static Long parsePassportToken(String token) {
        if (token == null || token.trim().isEmpty()) return null;
        String clean = token.trim();
        if (clean.toUpperCase().startsWith("EWASTE-")) {
            clean = clean.substring(7).trim();
        }
        try {
            return Long.parseLong(clean);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
