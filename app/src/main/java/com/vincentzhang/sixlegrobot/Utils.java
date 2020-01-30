package com.vincentzhang.sixlegrobot;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Utils {
    static private String base_url = "http://%s:5001";
    static private MulticastReceiver multicastReceiver = new MulticastReceiver();

    public static MulticastReceiver getMulticastReceiver() {
        return multicastReceiver;
    }

    public static String getUrl(String postFix) {
        if (Utils.getMulticastReceiver().getRobotIp().equalsIgnoreCase("unknown")) {
            return null;
        }

        return String.format(base_url, Utils.getMulticastReceiver().getRobotIp()) + postFix;
    }

    public static void sendCommand(final Activity activity, int method, String remoteCommand) {
        sendCommand(activity, method, remoteCommand, false);
    }

    public static Map<Activity, RequestQueue> requestQueueMap = new ConcurrentHashMap();

    public static void sendCommand(final Activity activity, int method, String remoteCommand, final boolean isToastResponse) {
        String url = Utils.getUrl(remoteCommand);
        if (url != null) {
            RequestQueue queue = requestQueueMap.get(activity);
            if (queue == null) {
                queue = Volley.newRequestQueue(activity);
                requestQueueMap.put(activity, queue);
            }
            
            StringRequest stringRequest = new StringRequest(method, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Utils.sendCommand", "Response is: " + response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(activity, "That didn't work!" + error, Toast.LENGTH_LONG).show();
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } else {
            Toast.makeText(activity, "Haven't gotten ip address yet, please be patient!", Toast.LENGTH_LONG).show();
        }
    }
}
