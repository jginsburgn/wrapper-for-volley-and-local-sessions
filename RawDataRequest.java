package me.itsof.volleywrapper;

import android.graphics.Bitmap;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * Created by jonathan on 5/14/18.
 */

public class RawDataRequest extends Request<byte[]> {

    private VolleyWrapperInterface vwInterface;
    private String contentType;
    private String body;

    public RawDataRequest (String url, int method, String body, String contentType, VolleyWrapperInterface vwInterface, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.vwInterface = vwInterface;
        this.contentType = contentType;
        this.body = body;
    }

    protected void deliverResponse(byte[] response) {
        vwInterface.OnResponse(response);
    }

    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data,
                    HttpHeaderParser.parseCacheHeaders(response));
    }

    public String getBodyContentType() {
        if (contentType == "") {
            return "application/json";
        }
        else return contentType;
    }

    public byte[] getBody() {
        return body.getBytes();
    }
}
