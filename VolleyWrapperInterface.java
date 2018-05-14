package me.itsof.volleywrapper;

import android.graphics.Bitmap;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by jonathan on 5/14/18.
 */

public interface VolleyWrapperInterface {
    void OnResponse(Object object);
    void OnError(Exception e);
}
