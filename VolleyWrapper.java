package me.itsof.volleywrapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by jonathan on 5/14/18.
 */

public class VolleyWrapper implements VolleyWrapperInterface {
    private RequestQueue queue;
    private ResourceType resourceType;

    public enum ResourceType {
        RJSONObject,
        RJSONArray,
        RString,
        RBitmap,
        RRawData
    }

    public void OnError(Exception e) {
        vwInterface.OnError(e);
    }

    @Override
    public void OnResponse(Object data) {
        byte[] rawData = (byte[]) data;
        String raw = new String(rawData);
        try {
            switch (this.resourceType) {
                case RJSONArray:
                    JSONArray ja = new JSONArray(raw);
                    vwInterface.OnResponse(ja);
                    break;
                case RJSONObject:
                    JSONObject jo = new JSONObject(raw);
                    vwInterface.OnResponse(jo);
                    break;
                case RRawData:
                    vwInterface.OnResponse(data);
                    break;
            }
        } catch (Exception e) {
            vwInterface.OnError(e);
        }
    }

    private VolleyWrapperInterface vwInterface;

    VolleyWrapper(VolleyWrapperInterface vwInterface, Context context) {
        super();
        this.vwInterface = vwInterface;
        // Instantiate the RequestQueue.
        this.queue = Volley.newRequestQueue(context);
    }

    public void execute(String url, int method, String body, String contentType, ResourceType type) {
        this.resourceType = type;
        switch (this.resourceType) {
            case RJSONArray:
                RawDataRequest jsonArrayRequest = new RawDataRequest(
                        url,
                        method,
                        body,
                        contentType,
                        this,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                vwInterface.OnError(error);
                            }
                        }
                );
                queue.add(jsonArrayRequest);
                break;
            case RJSONObject:
                RawDataRequest jsonObjectRequest = new RawDataRequest(
                        url,
                        method,
                        body,
                        contentType,
                        this,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                vwInterface.OnError(error);
                            }
                        }
                );
                queue.add(jsonObjectRequest);
                break;
            case RRawData:
                // Request a string response from the provided URL.
                RawDataRequest rawDataRequest = new RawDataRequest(
                        url,
                        method,
                        body,
                        contentType,
                        this,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                vwInterface.OnError(error);
                            }
                        }
                );
                queue.add(rawDataRequest);
                break;
            case RString:
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(method, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                vwInterface.OnResponse(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        vwInterface.OnError(error);
                    }
                });
                queue.add(stringRequest);
                break;
            case RBitmap:
                ImageRequest imageRequest = new ImageRequest(
                    url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            vwInterface.OnResponse(response);
                        }
                    },
                    0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        vwInterface.OnError(error);
                    }
                });
                queue.add(imageRequest);
                break;
        }
    }
}
