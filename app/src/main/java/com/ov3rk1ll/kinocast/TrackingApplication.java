package com.ov3rk1ll.kinocast;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ov3rk1ll.kinocast.api.KinoxParser;
import com.ov3rk1ll.kinocast.api.Parser;
import com.ov3rk1ll.kinocast.api.mirror.Host;
import com.ov3rk1ll.kinocast.utils.Utils;


public class TrackingApplication extends Application {

    @Override
    public void onCreate() {
        //TODO Select Parser depending on settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String parser = preferences.getString("parser", Integer.toString(KinoxParser.PARSER_ID));
        Host.DisableSSLCheck = preferences.getBoolean("allow_invalid_ssl",false);
        Parser.selectParser(this, Integer.parseInt(parser));
        Log.i("selectParser", "ID is " + Parser.getInstance().getParserId());

        String flurry_key = getString(R.string.FLURRY_API_KEY);
        if(!Utils.isStringEmpty(flurry_key)) {
            new com.flurry.android.FlurryAgent.Builder()
                    .withLogEnabled(true)
                    .withCaptureUncaughtExceptions(true)
                    .build(this, getString(R.string.FLURRY_API_KEY));
        }
        super.onCreate();
    }
}
