package com.vincentzhang.sixlegrobot;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

    public static void sendCommand(final Activity activity, int method, String remoteCommand, final boolean isToastResponse) {
        String url = Utils.getUrl(remoteCommand);
        if (url != null) {
            RequestQueue queue = Volley.newRequestQueue(activity);
            StringRequest stringRequest = new StringRequest(method, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            if(isToastResponse){
                                Toast.makeText(activity, "Response is: " + response, Toast.LENGTH_LONG).show();
                            }

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
