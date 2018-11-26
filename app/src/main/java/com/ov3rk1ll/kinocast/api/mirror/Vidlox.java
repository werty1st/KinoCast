package com.ov3rk1ll.kinocast.api.mirror;

import android.text.TextUtils;
import android.util.Log;

import com.ov3rk1ll.kinocast.ui.DetailActivity;
import com.ov3rk1ll.kinocast.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vidlox extends Host {
    private static final String TAG = Vidlox.class.getSimpleName();
    public static final int HOST_ID = 74;

    @Override
    public int getId() {
        return HOST_ID;
    }

    @Override
    public String getName() {
        return "Vidlox.tv";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private static final Pattern regexMp4 = Pattern.compile("http[s]*:\\/\\/([0-9a-z.]*)\\/([0-9a-z.]*?)\\/v\\.mp4");

    @Override
    public String getVideoPath(DetailActivity.QueryPlayTask queryTask) {
        Log.d(TAG, "GET " + url);

        if(TextUtils.isEmpty(url)) return null;
        try {
            queryTask.updateProgress(url);
            Document doc = Jsoup.connect(url)
                    .userAgent(Utils.USER_AGENT)
                    .timeout(3000)
                    .get();

            Matcher m = regexMp4.matcher(doc.html());

            // if an occurrence if a pattern was found in a given string...
            if (m.find()) {
                // ...then you can use group() methods.
                return m.group(0); // whole matched expression
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
