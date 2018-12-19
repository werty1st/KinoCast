package com.ov3rk1ll.kinocast.api.mirror;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.ov3rk1ll.kinocast.R;
import com.ov3rk1ll.kinocast.ui.DetailActivity;
import com.ov3rk1ll.kinocast.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Direct extends Host {
    private static final String TAG = Direct.class.getSimpleName();
    public static final int HOST_ID = 999;

    @Override
    public int getId() {
        return HOST_ID;
    }

    @Override
    public String getName() {
        return "Direct";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getVideoPath(DetailActivity.QueryPlayTask queryTask) {
        return url;
    }
}
