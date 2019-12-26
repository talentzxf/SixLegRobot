package com.vincentzhang.sixlegrobot;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class MainActivity extends AppCompatActivity {

    MulticastReceiver multicastReceiver = new MulticastReceiver();
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        seekBar = findViewById(R.id.heightSeekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                final TextView textView = findViewById(R.id.textViewHttpResult);
                textView.setText("Progress:" + progress);
                // Map from [0,10] to [-1.0, -2.0]

                float heightValue = -(float)progress/10.0f - 1.0f;
                sendCommand(Request.Method.PUT, "/robot/height?height="+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Create another thread to listen to multicast
        multicastReceiver.setActivity(this);
        Thread t = new Thread(multicastReceiver);
        t.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String base_url = "http://%s:5001";

    private String getUrl(String postFix) {
        if (this.multicastReceiver.getRobotIp().equalsIgnoreCase("unknown")) {
            return null;
        }

        return String.format(this.base_url, this.multicastReceiver.getRobotIp()) + postFix;
    }

    // Default is GET
    void sendCommand(String remoteCommand) {
        sendCommand(Request.Method.GET, remoteCommand);
    }

    void sendCommand(int method, String remoteCommand) {
        final TextView textView = findViewById(R.id.textViewHttpResult);

        String stop_url = getUrl(remoteCommand);
        if (stop_url != null) {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(method, stop_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            textView.setText("Response is: " + response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    textView.setText("That didn't work!" + error);
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } else {
            textView.setText("Haven't gotten ip address");
        }
    }

    public void onStop(View view) {
        this.sendCommand("/robot/move/stop");
    }

    public void onForward(View view) {
        this.sendCommand("/robot/move/go");
    }

    public void onBack(View view) {
        this.sendCommand("/robot/move/back");
    }

    public void onLeft(View view) {
        this.sendCommand("/robot/move/left");
    }

    public void onRight(View view) {
        this.sendCommand("/robot/move/right");
    }

    public void onAdjustPos(View view) {
        // TODO: Switch to adjust device pos activity
        Intent intent = new Intent(this, DeviceRotationActivity.class);
        startActivity(intent);
    }
}
