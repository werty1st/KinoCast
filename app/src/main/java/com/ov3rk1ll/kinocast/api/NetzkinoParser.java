package com.ov3rk1ll.kinocast.api;

import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.ov3rk1ll.kinocast.R;
import com.ov3rk1ll.kinocast.api.mirror.Direct;
import com.ov3rk1ll.kinocast.api.mirror.DivxStage;
import com.ov3rk1ll.kinocast.api.mirror.Host;
import com.ov3rk1ll.kinocast.api.mirror.SharedSx;
import com.ov3rk1ll.kinocast.api.mirror.Sockshare;
import com.ov3rk1ll.kinocast.api.mirror.StreamCloud;
import com.ov3rk1ll.kinocast.data.ViewModel;
import com.ov3rk1ll.kinocast.ui.DetailActivity;
import com.ov3rk1ll.kinocast.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class NetzkinoParser extends Parser {
    public static final int PARSER_ID = 8;
    public static final String TAG = "NetzkinoParser";
    public static final String URL_DEFAULT = "http://api.netzkino.de.simplecache.net/";

    private final List<ViewModel> lastModels = new ArrayList<>();

    private static final SparseIntArray languageResMap = new SparseIntArray();
    private static final SparseArray<String> languageKeyMap = new SparseArray<>();

    static {
        languageResMap.put(1, R.drawable.lang_de);
        languageKeyMap.put(1, "de");
        languageResMap.put(2, R.drawable.lang_en);
        languageKeyMap.put(2, "en");
        languageResMap.put(4, R.drawable.lang_zh);
        languageKeyMap.put(4, "zh");
        languageResMap.put(5, R.drawable.lang_es);
        languageKeyMap.put(5, "es");
        languageResMap.put(6, R.drawable.lang_fr);
        languageKeyMap.put(6, "fr");
        languageResMap.put(7, R.drawable.lang_tr);
        languageKeyMap.put(7, "tr");
        languageResMap.put(8, R.drawable.lang_jp);
        languageKeyMap.put(8, "jp");
        languageResMap.put(9, R.drawable.lang_ar);
        languageKeyMap.put(9, "ar");
        languageResMap.put(11, R.drawable.lang_it);
        languageKeyMap.put(11, "it");
        languageResMap.put(12, R.drawable.lang_hr);
        languageKeyMap.put(12, "hr");
        languageResMap.put(13, R.drawable.lang_sr);
        languageKeyMap.put(13, "sr");
        languageResMap.put(14, R.drawable.lang_bs);
        languageKeyMap.put(14, "bs");
        languageResMap.put(15, R.drawable.lang_de_en);
        languageKeyMap.put(15, "de");
        languageResMap.put(16, R.drawable.lang_nl);
        languageKeyMap.put(16, "nl");
        languageResMap.put(17, R.drawable.lang_ko);
        languageKeyMap.put(17, "ko");
        languageResMap.put(24, R.drawable.lang_el);
        languageKeyMap.put(24, "el");
        languageResMap.put(25, R.drawable.lang_ru);
        languageKeyMap.put(25, "ru");
        languageResMap.put(26, R.drawable.lang_hi);
        languageKeyMap.put(26, "hi");
    }

    @Override
    public String getDefaultUrl() {
        return URL_DEFAULT;
    }

    @Override
    public String getParserName() {
        return "Netzkino.de";
    }

    @Override
    public int getParserId() {
        return PARSER_ID;
    }

    @Override
    public List<ViewModel> parseList(String url) {
        Log.i("Parser", "parseList: " + url);

        JSONObject doc = Utils.readJson(url);
        return parseList(doc);
    }

    private List<ViewModel> parseList(JSONObject doc) {
        List<ViewModel> list = new ArrayList<>();
        try {
            JSONArray entries = doc.getJSONArray("posts");
            for (int i = 0; i < entries.length(); i++) {
                JSONObject item = entries.getJSONObject(i);

                try {
                    JSONObject cf = item.getJSONObject("custom_fields");
                    ViewModel model = new ViewModel();
                    model.setSummary(item.getString("content"));
                    model.setSlug(item.getString("slug"));
                    model.setTitle(item.getString("title"));

                    String imdburl = cf.getJSONArray("IMDb-Link").getString(0);
                    String imdb = imdburl.substring(imdburl.indexOf("/tt") + 1);
                    imdb = imdb.replaceAll("/", "");
                    model.setImdbId(imdb);
                    model.setType(ViewModel.Type.MOVIE);

                    model.setLanguageResId(R.drawable.lang_de);

                    List<Host> hostlist = new ArrayList<>();
                    Host h = new Direct();
                    h.setMirror(1);
                    String url = "http://pmd.netzkino-seite.netzkino.de/" + cf.getJSONArray("Streaming").getString(0) + ".mp4";
                    h.setSlug(url);
                    h.setUrl(url);
                    if (h.isEnabled()) {
                        hostlist.add(h);
                    }
                    model.setMirrors(hostlist.toArray(new Host[hostlist.size()]));
                    list.add(model);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing " + doc.toString(), e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing " + doc.toString(), e);
        }
        AddModels(list);
        return list;
    }

    private void AddModels(List<ViewModel> models){
        List<String> slugs = new ArrayList<>();
        for ( ViewModel m: lastModels) {
            slugs.add(m.getSlug());
        }
        for ( ViewModel m: models) {
            if(!slugs.contains(m.getSlug())) lastModels.add(m);
        }
    }


    @Override
    public ViewModel loadDetail(ViewModel item) {
        if(lastModels == null) return item;
        for ( ViewModel m: lastModels) {
            if(item.getSlug().equalsIgnoreCase(m.getSlug())) return m;
        }
        return item;
    }

    @Override
    public ViewModel loadDetail(String url) {
        url = parseSlug(url);
        if(url == null || lastModels == null) return null;

        for ( ViewModel m: lastModels) {
            if(url.equalsIgnoreCase(m.getSlug())) return m;
        }
        return null;
    }


    private String parseSlug(String url) {
        if(url == null || lastModels == null) return null;
        url = url.substring(url.indexOf("filme/") + 6);
        int i = url.indexOf("/");
        if(i < 1) i = url.indexOf("#");
        if(i > 0) url = url.substring(0, i);
        return url;
    }

    @Override
    public List<Host> getHosterList(ViewModel item, int season, String episode) {
        item = loadDetail(item);
        List<Host> hostlist = new ArrayList<>();
        for (Host host : item.getMirrors()) {
            hostlist.add(host);
        }
        return hostlist;
    }


    @Override
    public String getMirrorLink(DetailActivity.QueryPlayTask queryTask, ViewModel item, Host host) {
        return host.getUrl();
    }

    @Override
    public String getMirrorLink(DetailActivity.QueryPlayTask queryTask, ViewModel item, Host host, int season, String episode) {
        return host.getUrl();
    }

    @SuppressWarnings("deprecation")
    @Override
    public String[] getSearchSuggestions(String query) {
        String url = KinoxParser.URL_DEFAULT + "aGET/Suggestions/?q=" + URLEncoder.encode(query) + "&limit=10&timestamp=" + SystemClock.elapsedRealtime();
        String data = getBody(url);
        /*try {
            byte ptext[] = data.getBytes("ISO-8859-1");
            data = new String(ptext, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
        String suggestions[] = data != null ? data.split("\n") : new String[0];
        if (suggestions[0].trim().equals("")) return null;
        // Remove duplicates
        return new HashSet<>(Arrays.asList(suggestions)).toArray(new String[new HashSet<>(Arrays.asList(suggestions)).size()]);
    }

    @Override
    public String getPageLink(ViewModel item) {
        return "http://www.netzkino.de/filme/" + item.getSlug();
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getSearchPage(String query) {
        return URL_BASE + "capi-2.0a/search?q=" + URLEncoder.encode(query) + "&d=www&l=de-DE&v=1.2.1";
    }

    @Override
    public String getCineMovies() {
        return URL_BASE + "capi-2.0a/categories/1.json?d=www&l=de-DE&v=1.2.1";
    }

    @Override
    public String getPopularMovies() {
        return URL_BASE + "capi-2.0a/categories/1.json?d=www&l=de-DE&v=1.2.1";
    }

    @Override
    public String getLatestMovies() {
        return URL_BASE + "capi-2.0a/categories/1.json?d=www&l=de-DE&v=1.2.1";
    }

    @Override
    public String getPopularSeries() {
        return URL_BASE + "capi-2.0a/categories/1.json?d=www&l=de-DE&v=1.2.1";
    }

    @Override
    public String getLatestSeries() {
        return URL_BASE + "capi-2.0a/categories/1.json?d=www&l=de-DE&v=1.2.1";
    }

    @Override
    public String PreSaveParserUrl(String newUrl){

        return newUrl.endsWith("/") ? newUrl : newUrl + "/";
    }
}
