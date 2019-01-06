package com.ov3rk1ll.kinocast;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ov3rk1ll.kinocast.api.KinoxParser;
import com.ov3rk1ll.kinocast.api.Parser;
import com.ov3rk1ll.kinocast.utils.Utils;


public class CastApp extends Application {


    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }


    @Override
    public void onCreate() {
        sApplication = this;

        //TODO Select Parser depending on settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String parser = preferences.getString("parser", Integer.toString(KinoxParser.PARSER_ID));
        Utils.DisableSSLCheck = preferences.getBoolean("allow_invalid_ssl",false);
        Parser.selectParser(this, Integer.parseInt(parser));
        Log.i("selectParser", "ID is " + Parser.getInstance().getParserId());

        String flurry_key = getString(R.string.FLURRY_API_KEY);
        if(!Utils.isStringEmpty(flurry_key)) {
            new com.flurry.android.FlurryAgent.Builder()
                    .withLogEnabled(true)
                    .withCaptureUncaughtExceptions(true)
                    .build(this, getString(R.string.FLURRY_API_KEY));
        }
        //com.google.android.gms.ads.MobileAds.initialize(this, "ca-app-pub-2728479259954125~72137");
        super.onCreate();
    }

    public static Context GetCheckedContext(Context context){
        if(context != null) return context;
        return getContext();
    }
}
