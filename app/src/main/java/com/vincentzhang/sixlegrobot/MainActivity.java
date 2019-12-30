package com.vincentzhang.sixlegrobot;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.URI;
import java.net.URISyntaxException;


public class MainActivity extends AppCompatActivity {

    SeekBar heightSeekBar;
    SeekBar stretchSeekBar;
    SeekBar cameraPitchSeekBar;
    SeekBar cameraYawSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSeekBars();

        // Create another thread to listen to multicast
        Utils.getMulticastReceiver().setActivity(this);
        Thread t = new Thread(Utils.getMulticastReceiver());
        t.start();
    }

    private void initSeekBars() {
        heightSeekBar = findViewById(R.id.heightSeekBar);

        heightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Map from [0,10] to [-1.0, -2.0]
                float heightValue = -(float) progress / 10.0f - 1.0f;
                sendCommand(Request.Method.PUT, "/robot/height?height=" + heightValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        stretchSeekBar = findViewById(R.id.stretchSeekBar);
        stretchSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Map from [0,10] to [0.5, 3.5]
                float stretchValue = (float) progress / 10.0f * 3.0f + 0.5f;
                sendCommand(Request.Method.PUT, "/robot/stretch?stretch=" + stretchValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        cameraPitchSeekBar = findViewById(R.id.cameraPitchSeekBar);
        cameraPitchSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float pitchValue = (float) progress / 10.0f * 120.0f - 60.0f;
                sendCommand(Request.Method.PUT, "/camera/pitch?degree=" + pitchValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        cameraYawSeekBar = findViewById(R.id.cameraYawSeekBar);
        cameraYawSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float yawValue = (float) progress / 10.0f * 80.0f - 20.0f;
                sendCommand(Request.Method.PUT, "/camera/yaw?degree=" + yawValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void startView(final String ip) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView webView = findViewById(R.id.webView);
                String videoAddress = "http://" + ip + ":8081";
                webView.getSettings().setUseWideViewPort(true);
                webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.loadUrl(videoAddress);
            }
        });
    }


    // Default is GET
    void sendCommand(String remoteCommand) {
        sendCommand(Request.Method.GET, remoteCommand);
    }

    void sendCommand(int method, String remoteCommand) {
        Utils.sendCommand(this, method, remoteCommand);
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
        Intent intent = new Intent(this, DeviceRotationActivity.class);
        startActivity(intent);
    }


    public void onIKControl(View view) {
        Intent intent = new Intent(this, IKControlActivity.class);
        startActivity(intent);
    }
}
