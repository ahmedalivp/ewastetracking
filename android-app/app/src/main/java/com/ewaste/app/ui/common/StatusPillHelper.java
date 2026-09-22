package com.ewaste.app.ui.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.ewaste.app.R;

public final class StatusPillHelper {

    private StatusPillHelper() {}

    public static void applyStatus(TextView textView, String status) {
        if (textView == null) return;
        Context context = textView.getContext();
        String normalized = status != null ? status.trim().toUpperCase() : "SUBMITTED";

        int bgRes;
        int textRes;

        switch (normalized) {
            case "RECEIVED":
                bgRes = R.color.status_received_bg;
                textRes = R.color.status_received_text;
                break;
            case "CATEGORIZED":
                bgRes = R.color.status_categorized_bg;
                textRes = R.color.status_categorized_text;
                break;
            case "PROCESSED":
                bgRes = R.color.status_processed_bg;
                textRes = R.color.status_processed_text;
                break;
            case "COMPLETED":
            case "FULFILLED":
            case "AVAILABLE":
                bgRes = R.color.status_completed_bg;
                textRes = R.color.status_completed_text;
                break;
            case "APPROVED":
                bgRes = R.color.status_submitted_bg;
                textRes = R.color.status_submitted_text;
                break;
            case "PENDING":
                bgRes = R.color.status_pending_bg;
                textRes = R.color.status_pending_text;
                break;
            case "HAZARDOUS":
            case "REJECTED":
                bgRes = R.color.status_hazardous_bg;
                textRes = R.color.status_hazardous_text;
                break;
            case "SUBMITTED":
            default:
                bgRes = R.color.status_submitted_bg;
                textRes = R.color.status_submitted_text;
                break;
        }

        textView.setBackgroundResource(R.drawable.bg_status_pill);
        textView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, bgRes)));
        textView.setTextColor(ContextCompat.getColor(context, textRes));
    }

    public static void applyCondition(TextView textView, String condition) {
        if (textView == null) return;
        Context context = textView.getContext();
        String normalized = condition != null ? condition.trim().toUpperCase() : "FAIR";

        int bgRes;
        int textRes;

        switch (normalized) {
            case "GOOD":
                bgRes = R.color.status_good_bg;
                textRes = R.color.status_good_text;
                break;
            case "POOR":
                bgRes = R.color.status_poor_bg;
                textRes = R.color.status_poor_text;
                break;
            case "FAIR":
            default:
                bgRes = R.color.status_fair_bg;
                textRes = R.color.status_fair_text;
                break;
        }

        textView.setBackgroundResource(R.drawable.bg_status_pill);
        textView.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, bgRes)));
        textView.setTextColor(ContextCompat.getColor(context, textRes));
    }
}
