package com.ov3rk1ll.kinocast.api.mirror;

import android.text.TextUtils;
import android.util.Log;

import com.ov3rk1ll.kinocast.ui.DetailActivity;
import com.ov3rk1ll.kinocast.utils.Utils;

import org.jsoup.nodes.Document;

public class Vidoza extends Host {
    private static final String TAG = Vidoza.class.getSimpleName();
    public static final int HOST_ID = 80;

    @Override
    public int getId() {
        return HOST_ID;
    }

    @Override
    public String getName() {
        return "Vidoza";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getVideoPath(DetailActivity.QueryPlayTask queryTask) {
        Log.d(TAG, "GET " + url);

        if(TextUtils.isEmpty(url)) return null;
        try {
            queryTask.updateProgress(url);
            Document doc = Utils.buildJsoup(url)
                    .get();

            return doc.select("source[type=video/mp4]").attr("src");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
