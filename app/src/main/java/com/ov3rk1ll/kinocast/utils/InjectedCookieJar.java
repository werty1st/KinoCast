package com.ov3rk1ll.kinocast.utils;

import android.content.Context;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.CookieCache;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;

public class InjectedCookieJar extends PersistentCookieJar {
    private  CookieCache ccache;

    private InjectedCookieJar(CookieCache cache, CookiePersistor persistor) {
        super(cache, persistor);
        ccache = cache;

    }

    public static InjectedCookieJar Build(Context context){
        return new InjectedCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
    }

    public Cookie[] toArray(){
        List<Cookie> list = new ArrayList<>();
        Iterator<Cookie> ic = ccache.iterator();
        while(ic.hasNext()){
            list.add(ic.next());
        }
        return list.toArray(new Cookie[list.size()]);
    }

    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }

    public void addCookie(Cookie cookie){
        ccache.addAll(Arrays.asList(cookie));
    }

}
