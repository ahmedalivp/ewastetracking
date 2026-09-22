package com.ewaste.app.data.api;

import com.ewaste.app.BuildConfig;
import com.ewaste.app.EWasteApplication;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/**
 * Singleton provider for configured Retrofit ApiService instance.
 */
public class ApiClient {

    private static ApiService apiService;

    public static synchronized ApiService getApiService() {
        if (apiService == null) {
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(new AuthInterceptor(EWasteApplication.getInstance().getSessionManager()));

            // Enable HTTP request/response logging only in debug builds
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                okHttpBuilder.addInterceptor(loggingInterceptor);
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(okHttpBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    public static String getBaseUrl() {
        if (!BuildConfig.DEBUG) {
            return BuildConfig.BASE_URL;
        }

        if (isEmulator()) {
            return "http://10.0.2.2:8080/api/";
        } else {
            // Physical Android device connected via USB with "adb reverse tcp:8080 tcp:8080"
            return "http://127.0.0.1:8080/api/";
        }
    }

    private static boolean isEmulator() {
        return (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
                || android.os.Build.FINGERPRINT.startsWith("generic")
                || android.os.Build.FINGERPRINT.startsWith("unknown")
                || android.os.Build.HARDWARE.contains("goldfish")
                || android.os.Build.HARDWARE.contains("ranchu")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86")
                || android.os.Build.MANUFACTURER.contains("Genymotion")
                || android.os.Build.PRODUCT.contains("sdk_google")
                || android.os.Build.PRODUCT.contains("google_sdk")
                || android.os.Build.PRODUCT.contains("sdk");
    }
}
