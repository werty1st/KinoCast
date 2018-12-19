package com.ov3rk1ll.kinocast.api.mirror;

import com.ov3rk1ll.kinocast.ui.DetailActivity;

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
