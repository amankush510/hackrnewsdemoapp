package com.example.hackernews.volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by aman.kush on 9/1/2017.
 */
public class HNJSONRequest extends JsonObjectRequest {
    private static final String TAG = "BJR";

    private Context context;

    public HNJSONRequest(Context context, int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.context = context;
        this.setRetryPolicy(new DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Log.d(TAG, "url==" + url + "||time==" + new Date());
        try {
            Log.d(TAG, "||data==" + (jsonRequest == null ? "NULL" : (jsonRequest.length() == 0 ? "0" : jsonRequest.toString())));
        } catch (Exception ex) {
            Log.e(TAG, "Data print exception ", ex);
        }

    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data);
            JSONObject res = new JSONObject();
            try{
                res.put("data", new JSONArray(jsonString));
            } catch (JSONException e){
                res.put("data", new JSONObject(jsonString));
            }
            return Response.success(res,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
