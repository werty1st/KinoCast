package com.ov3rk1ll.kinocast.api.mirror;

import com.ov3rk1ll.kinocast.ui.DetailActivity;

public class Direct extends Host {
    private static final String TAG = Direct.class.getSimpleName();
    public static final int HOST_ID = 999;

    private String mName = "Direct";
    @Override
    public int getId() {
        return HOST_ID;
    }

    @Override
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
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
