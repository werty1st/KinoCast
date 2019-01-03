package com.ov3rk1ll.kinocast.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseIntArray;

import com.google.android.gms.cast.framework.CastContext;
import com.ov3rk1ll.kinocast.api.Parser;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Utils {
    //public static final String USER_AGENT = "KinoCast v" + BuildConfig.VERSION_NAME;
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36";
    public static boolean DisableSSLCheck = false;

    public static boolean isStringEmpty(String val) {
        if (val == null) return true;
        if (val.isEmpty()) return true;
        if (val.equalsIgnoreCase("null")) return true;
        if (val.equalsIgnoreCase("http:null")) return true;
        if (val.equalsIgnoreCase("https:null")) return true;
        if (val.equalsIgnoreCase("https://null")) return true;
        if (val.equalsIgnoreCase("http://null")) return true;
        return false;
    }

    public static String getUrl(String url) {
        if (url != null && url.startsWith("//")) return "https:" + url;
        return url;
    }

    public static String getRedirectTarget(String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(false)
                .addNetworkInterceptor(new UserAgentInterceptor(USER_AGENT))
                .cookieJar(Parser.injectedCookieJar)
                .build();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            String ret = response.header("Location");
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMultiRedirectTarget(String url) {
        String lastRet = url;
        String lastUrl;
        do {
            lastUrl = lastRet;
            lastRet = getRedirectTarget(lastUrl);
        } while (lastRet != null);
        return lastUrl;
    }


    public static JSONObject readJson(String url) {
        OkHttpClient client = buildOkHttpClient();
        Request request = new Request.Builder().url(url).build();

        Log.i("Utils", "read json from " + url);
        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            return new JSONObject(body);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject readJson(String url, Set<Map.Entry<String, String>> postData) {
        OkHttpClient client = buildOkHttpClient();

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (Map.Entry<String, String> pair : postData) {
            bodyBuilder.addFormDataPart(pair.getKey(), pair.getValue());
        }
        RequestBody requestBody = bodyBuilder.build();

        Request request = new Request.Builder().url(url).post(requestBody).build();

        Log.i("Utils", "read json from " + url);
        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            return new JSONObject(body);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Connection buildJsoup(String url) {
        return Jsoup.connect(url)
                .validateTLSCertificates(!DisableSSLCheck)
                .userAgent(Utils.USER_AGENT)
                .timeout(6000);
    }
    public static OkHttpClient buildOkHttpClient() {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new UserAgentInterceptor(USER_AGENT))
                .cookieJar(Parser.injectedCookieJar)
                .build();
    }

    @SuppressWarnings("deprecation")
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return ((netInfo != null) && netInfo.isConnected());
    }

    public static SparseIntArray getWeightedHostList(Context context) {
        SparseIntArray sparseArray = new SparseIntArray();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int count = preferences.getInt("order_hostlist_count", -1);
        if (count == -1) return null;
        for (int i = 0; i < count; i++) {
            int key = preferences.getInt("order_hostlist_" + i, i);
            sparseArray.put(key, i);
        }
        return sparseArray;
    }

    public static CastContext getCastContext(Context context) {
        try {
            return CastContext.getSharedInstance(context);
        } catch (Exception ex) {}
        return  null;
    }
}

