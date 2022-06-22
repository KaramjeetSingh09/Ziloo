package com.zeroitsolutions.ziloo.ApiClasses;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiVolleyRequest {

    public ApiVolleyRequest() {

    }

    public static void JsonPostRequest(final Activity context, String url, JSONObject jsonObject, final HashMap<String, String> headers, final InterfaceApiResponse callback) {

        if (jsonObject != null)
            if (!jsonObject.toString().isEmpty())
                Log.d("ok", "\n Params => " + jsonObject);
        if (!headers.isEmpty())
            Log.d("ok", "\n Header => " + headers);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,
                jsonObject, response -> {
            Log.d("ok", " Success => Api " + url + " \nresponse is => " + response.toString());

            if (!response.toString().isEmpty()) {
                if (callback != null)
                    callback.onResponse(response.toString());
            }
        }, error -> {
            Log.d("ok", "error => " + url + " \nresponse is => " + error.toString());
            Toast.makeText(context, "Something went wrong please try again...", Toast.LENGTH_SHORT).show();
            if (callback != null)
            callback.onError(error.toString());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        try {
            if (context != null) {
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 0, 1.0F));
                requestQueue.getCache().clear();
                requestQueue.add(jsonObjectRequest);
            }
        } catch (Exception var9) {
            Log.d("ok", var9.toString());
        }
    }

    public static void JsonGetRequest(Activity context, String url, final InterfaceApiResponse callback) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            Log.d("ok", " Success => Api " + url + " \nresponse is => " + response.toString());
            if (callback != null) {
                callback.onResponse(response.toString());
            }

        }, error -> {
            Log.d("ok", "error => " + url + " \nresponse is => " + error.toString());
            if (callback != null) {
                callback.onError(error.toString());
            }
        });
        try {
            if (context != null) {
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000, 0, 1.0F));
                requestQueue.getCache().clear();
                requestQueue.add(jsonObjReq);
            }
        } catch (Exception var5) {
            Log.d("ok", var5.toString());
        }
    }
}