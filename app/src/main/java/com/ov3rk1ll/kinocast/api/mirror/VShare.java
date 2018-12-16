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

public class VShare extends Host {
    private static final String TAG = VShare.class.getSimpleName();
    public static final int HOST_ID = 78;

    @Override
    public int getId() {
        return HOST_ID;
    }

    @Override
    public String getName() {
        return "VShare.eu";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public String getVideoPath(DetailActivity.QueryPlayTask queryTask) {
        if(TextUtils.isEmpty(url)) return null;
        url = url.replace("http://","https://");
        queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_getdatafrom, url));
        Pattern pattern = Pattern.compile("vshare\\.eu\\/(.*)\\.htm");
        Matcher matcher = pattern.matcher(url);
        Log.d(TAG, "resolve " + url);
        if (matcher.find() && matcher.groupCount() >= 1) {
            Log.d(TAG, "Request player [id:" + matcher.group(1) + "]");
            queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_getvideoforid,  matcher.group(1)));

            queryTask.updateProgress(url);
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent(Utils.USER_AGENT)
                        .timeout(3000)
                        .validateTLSCertificates(false)
                        .get();
                String fname = doc.select("input[name=fname]").attr("value");
                Log.d(TAG, "FName " + fname);

            for(int i = 5; i >= 0; i--){
                queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_wait, String.valueOf(i)));
                SystemClock.sleep(1000);
            }
            String link = getLink(url, matcher.group(1), fname);
            Log.d(TAG, "1st Request. Got " + link);

            queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_1sttry));
            if(link != null) return link;

            queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_wait, "5"));
            Log.d(TAG, "single request failed. Waiting 10s and retry.");
            for(int i = 5; i >= 0; i--){
                queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_wait, String.valueOf(i)));
                SystemClock.sleep(1000);
            }
            link = getLink(url, matcher.group(1), fname);
            queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_2ndtry));
            Log.d(TAG, "2nd Request. Got " + link);
            return link;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private String getLink(String url, String id, String fname){
        try {
            // op=download1&usr_login=&id=kp95f217fxwr&fname=Bones.S01E01.DVDRip.XviD-TOPAZ.avi
            // &referer=http%3A%2F%2Fwww.kinox.to%2FStream%2FBones.html
            // &hash=&imhuman=Weiter+zum+Video
            Document doc = Jsoup.connect(url)
                    .data("op", "download1")
                    .data("id", id)
                    .data("fname", fname)
                    .data("method_free", "Proceed to video")
                    .data("usr_login", "")
                    .data("referer", "") // http://www.kinox.to
                    .data("hash", "")
                    .userAgent(Utils.USER_AGENT)
                    .timeout(3000)
                    .validateTLSCertificates(false)
                    .post();

            return doc.select("source[type=video/mp4]").attr("src");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
