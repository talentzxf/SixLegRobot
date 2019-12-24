package com.vincentzhang.sixlegrobot;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MulticastReceiver implements Runnable {

    String multicast_address = "224.1.1.1";
    Integer multicast_port = 6666;
    String robotIp = "192.168.17.59";

    Activity activity;

    Pattern p = Pattern.compile("Robot:(.*):");

    void setActivity(Activity activity){
        this.activity = activity;
    }

    String getRobotIp(){
        return robotIp;
    }

    private void setTextViewText(final String text){
        final TextView textView = this.activity.findViewById(R.id.textViewHttpResult);
        this.activity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(text);
                    }
                }
        );
    }

    @Override
    public void run() {
        String tag = "MulticastReceiverThread";
        Log.i(tag, "In the multicast receiver thread");
        MulticastSocket socket = null;
        InetAddress group = null;
        Boolean received = false;

        try {
            socket = new MulticastSocket(this.multicast_port);
            Log.i(tag, "Socket created");
            group = InetAddress.getByName(this.multicast_address);
            socket.joinGroup(group);
            Log.i(tag, "Joined group");

            DatagramPacket packet;

            while (!received) {
                try {
                    byte[] buf = new byte[256];
                    packet = new DatagramPacket(buf, buf.length);
                    setTextViewText("Receiving data");
                    socket.receive(packet);
                    String s = new String(buf, "UTF-8");
                    String strings[] = s.split("\\r?\\n|\\r");
                    setTextViewText("Received" + strings[0]);

                    Matcher matcher = p.matcher(strings[0]);
                    if (matcher.matches()) {
                        this.robotIp = matcher.group(1);
                        Log.i(tag, "ip is:" + this.robotIp);
                        setTextViewText("Ip is:" + this.robotIp + "! Press button to control robot now.");
                        received = true;
                    } else {
                        Log.e(tag, "Protocol mismatch!");
                        setTextViewText("Protocol mismatch!");
                    }
                } catch (Exception e) {
                    Log.e(tag, "Protocol mismatch!");
                    setTextViewText("Protocol mismatch!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    if (group != null) {
                        socket.leaveGroup(group);
                    }
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
