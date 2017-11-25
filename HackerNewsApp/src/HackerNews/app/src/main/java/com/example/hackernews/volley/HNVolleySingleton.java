package com.example.hackernews.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by aman.kush on 2/28/2017.
 */
public class HNVolleySingleton {
    private static final String TAG = "BVS";
    private static HNVolleySingleton instance;
    private Context context;
    private RequestQueue requestQueue;


    private HNVolleySingleton(Context context) {
        this.context = context;
    }

    public static synchronized HNVolleySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new HNVolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
