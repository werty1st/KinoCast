package com.ov3rk1ll.kinocast.api.mirror;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.ov3rk1ll.kinocast.R;
import com.ov3rk1ll.kinocast.ui.DetailActivity;
import com.ov3rk1ll.kinocast.utils.Utils;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamCloud extends Host {
    private static final String TAG = StreamCloud.class.getSimpleName();
    public static final int HOST_ID = 30;


    @Override
    public int getId() {
        return HOST_ID;
    }

    @Override
    public String getName() {
        return "Streamcloud";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getVideoPath(DetailActivity.QueryPlayTask queryTask) {
        if(TextUtils.isEmpty(url)) return null;
        queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_getdatafrom, url));
        Pattern pattern = Pattern.compile("http:\\/\\/streamcloud\\.eu\\/(.*)\\/(.*)\\.html");
        Matcher matcher = pattern.matcher(url);
        Log.d(TAG, "resolve " + url);
        if (matcher.find() && matcher.groupCount() == 2) {
            Log.d(TAG, "Request player [id:" + matcher.group(1) + ", fname: " + matcher.group(2) + "]");
            queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_getvideoforid,  matcher.group(1)));
            String[] links = getLink(url, matcher.group(1), matcher.group(2));
            Log.d(TAG, "1st Request. Got " + links[0]);
            queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_1sttry));
            if(links[0] == null) {
                queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_wait, "5"));
                Log.d(TAG, "single request failed. Waiting 10s and retry.");
                for (int i = 5; i >= 0; i--) {
                    queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_wait, String.valueOf(i)));
                    SystemClock.sleep(1000);
                }
                links = getLink(url, matcher.group(1), matcher.group(2));

                queryTask.updateProgress(queryTask.getContext().getString(R.string.host_progress_2ndtry));
                Log.d(TAG, "2nd Request. Got " + links[0]);
            }
            if(links[1] != null){
                try {
                    Document doc = Utils.buildJsoup(links[1])
                            .ignoreContentType(true)
                            .referrer(url)
                           .get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return links[0];
        }
        return null;
    }

    private String[] getLink(String url, String id, String fname){
        String[] data = {null, null} ;
        try {
            // op=download1&usr_login=&id=kp95f217fxwr&fname=Bones.S01E01.DVDRip.XviD-TOPAZ.avi
            // &referer=http%3A%2F%2Fwww.kinox.to%2FStream%2FBones.html
            // &hash=&imhuman=Weiter+zum+Video
            Document doc = Utils.buildJsoup(url)
                    .data("op", "download1")
                    .data("id", id)
                    .data("fname", fname)
                    .data("imhuman", "Weiter zum Video")
                    .data("usr_login", "")
                    .data("referer", "") // http://www.kinox.to
                    .data("hash", "")
                    .cookie("playermode", "html5")
                    .cookie("lang", "german")
                    .post();
            String html = doc.html();
            Pattern p = Pattern.compile("file: \\\"(.*)\\/video\\.mp4\\\",");
            Pattern p1 = Pattern.compile("url: \\\"http:\\/\\/meta(.*)\\/\\\"");
            Matcher m = p.matcher(html);
            if(m.find()){
                data[0] = m.group(1) + "/video.mp4";
                Matcher m1 = p1.matcher(html);
                if(m1.find()){
                    data[1] = "http://meta" + m1.group(1) + "/";
                }
                Log.d(TAG, "URL '" + data[0] + "'");
                Log.d(TAG, "BEFORE '" + data[1] + "'");
            } else {
                Log.d(TAG, "file-pattern not found in '" + html + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;

    }
}
