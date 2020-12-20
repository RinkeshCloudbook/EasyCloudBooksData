package com.easycloudbooks.easycloudbooks.util;

import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.easycloudbooks.easycloudbooks.app.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CustomRequest extends JsonObjectRequest {

    private Listener<JSONObject> listener;
    private ErrorListener errorListener;
    private JSONObject params;
    private  Map<String, String> headers;
    private  boolean isDestroyed ;

    public void destroy()
    {
        this.isDestroyed = true;
       // Log.w("CustomRequest", "destroy: " );
        this.listener = null;
        this.errorListener = null;

    }
    public CustomRequest(String url, JSONObject params, Map<String, String> headers,
                         Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(Method.POST, url, params, reponseListener, errorListener);
        this.listener = reponseListener;
        this.errorListener = errorListener;
        this.params = params;
        this.headers = headers;
        this.isDestroyed = false;
    }


    public CustomRequest(int method, String url, JSONObject params, Map<String, String> headers,
                         Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(method, url, params,reponseListener,errorListener);
        this.listener = reponseListener;
        this.errorListener = errorListener;
        this.params = params;
        this.headers = headers;
        this.isDestroyed = false;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    public Map<String, String> getHeaders()  {
        if(this.headers == null)
            this.headers = new HashMap<String, String>();
        //Log.w("CustomRequest", "getHeaders: " +  this.headers );
        return this.headers;
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        if(listener != null && this.isDestroyed != true)
        listener.onResponse(response);
    }
}