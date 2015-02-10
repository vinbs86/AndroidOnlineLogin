package com.example.androidonlinelogin;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

import java.util.List;

public class CookieStoreHelper {

    //replace website.com with your server
    static final String DOMAIN = "http://website.com";
    static CookieStore cookieStore;
    static List sessionCookie;

    public CookieStoreHelper() {
    }

    static {
        BasicCookieStore basiccookiestore = new BasicCookieStore();
        cookieStore = basiccookiestore;
    }
}
