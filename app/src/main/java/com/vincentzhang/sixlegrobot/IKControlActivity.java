package com.vincentzhang.sixlegrobot;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class IKControlActivity extends Activity {

    private IKControlView view;

    // TODO: Make a REST call to get the legHeight
    private float legHeight = -1.5f;
    private float maxLegHeight = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ik_control);


        SeekBar legHeightSeekBar = findViewById(R.id.legHeightSeekBar);
        legHeightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float newHeight = (float) progress / 100.0f * (maxLegHeight - legHeight) + legHeight;
                sendCommand(Request.Method.PUT, "/robot/legs/0/height?height=" + newHeight);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }



    void sendCommand(int method, String remoteCommand) {
        Utils.sendCommand(this, method, remoteCommand);
    }
}

class LegRect {
    private final float RECTSIZE = 50.f;
    private Point scrLegPoint = new Point();
    private boolean selected = false;

    public LegRect(int scrX, int scrY) {
        scrLegPoint.x = scrX;
        scrLegPoint.y = scrY;
    }

    public void draw(Canvas canvas) {
        Paint pen = new Paint();
        pen.setStyle(Paint.Style.FILL_AND_STROKE);
        if (selected) {
            pen.setColor(Color.parseColor("#ff0000"));
        } else {
            pen.setColor(Color.parseColor("#0000ff"));
        }

        canvas.drawRect(new RectF(
                scrLegPoint.x - RECTSIZE / 2.f,
                scrLegPoint.y - RECTSIZE / 2.f,
                scrLegPoint.x + RECTSIZE / 2.f,
                scrLegPoint.y + RECTSIZE / 2.f
        ), pen);
    }

    public boolean contains(int x, int y) {
        RectF rectF = new RectF(
                scrLegPoint.x - RECTSIZE / 2.f,
                scrLegPoint.y - RECTSIZE / 2.f,
                scrLegPoint.x + RECTSIZE / 2.f,
                scrLegPoint.y + RECTSIZE / 2.f
        );
        return rectF.contains(x, y);
    }

    public void setSelect(boolean isSelected) {
        this.selected = isSelected;
    }

    public void setPos(int x, int y) {
        this.scrLegPoint.x = x;
        this.scrLegPoint.y = y;
    }
}

class IKControlView extends View implements View.OnTouchListener {
    private Activity activity;
    private final float BODY_WIDTH = 8.f;
    private final float BODY_LENGTH = 16.f;
    private final float SCALE = 20.f;
    private final float STRETCH = 100.0f;

    private boolean inited = false;
    private Map<Integer, LegRect> legRectMap = new HashMap();
    private LegRect curSelectedLeg = null;

    public IKControlView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnTouchListener(this);
        activity = (Activity) context;
    }

    private void initLegRects(int scrWidth, int scrHeight) {
        legRectMap.clear();

        int centerX = scrWidth / 2;
        int centerY = scrHeight / 2;

        // Add leg rects
        legRectMap.put(0, new LegRect((int) (centerX - BODY_WIDTH / 2.0f * SCALE), (int) (centerY - BODY_LENGTH / 2.0f * SCALE))); // Left up
        legRectMap.put(1, new LegRect((int) (centerX + BODY_WIDTH / 2.0f * SCALE), (int) (centerY - BODY_LENGTH / 2.0f * SCALE))); // Right up
        legRectMap.put(2, new LegRect((int) (centerX + BODY_WIDTH / 2.0f * SCALE), (int) (centerY))); // Right center
        legRectMap.put(3, new LegRect((int) (centerX + BODY_WIDTH / 2.0f * SCALE), (int) (centerY + BODY_LENGTH / 2.0f * SCALE))); // Right down
        legRectMap.put(4, new LegRect((int) (centerX - BODY_WIDTH / 2.0f * SCALE), (int) (centerY + BODY_LENGTH / 2.0f * SCALE))); // Left down
        legRectMap.put(5, new LegRect((int) (centerX - BODY_WIDTH / 2.0f * SCALE), (int) (centerY))); // Left center

        inited = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw a rectange in the center of the canvas
        int screenWidth = getWidth();
        int screenHeight = getHeight();

        if (!inited) {
            initLegRects(screenWidth, screenHeight);
        }

        Paint pen = new Paint();
        pen.setStyle(Paint.Style.FILL_AND_STROKE);
        pen.setColor(Color.parseColor("#00ffff"));

        RectF bodyRect = new RectF(
                screenWidth / 2.f - BODY_WIDTH * SCALE / 2.f,
                screenHeight / 2.f - BODY_LENGTH * SCALE / 2.f,
                screenWidth / 2.f + BODY_WIDTH * SCALE / 2.f,
                screenHeight / 2.f + BODY_LENGTH * SCALE / 2.f);
        canvas.drawRect(bodyRect, pen);

        for (LegRect legRect : legRectMap.values()) {
            legRect.draw(canvas);
        }
    }

    private void hideHeightSeekBar() {
        SeekBar heightSeekBar = activity.findViewById(R.id.legHeightSeekBar);
        if (heightSeekBar != null) {
            heightSeekBar.setVisibility(INVISIBLE);
        }
    }

    private void showHeightSeekBar() {
        SeekBar heightSeekBar = activity.findViewById(R.id.legHeightSeekBar);
        if (heightSeekBar != null) {
            heightSeekBar.setVisibility(VISIBLE);
            Log.i(IKControlView.class.getName(), "heightSeekBar shown now!");
        } else {
            Log.e(IKControlView.class.getName(), "heightSeekBar can't be shown!");
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int eventAction = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        Log.i("IKControlView", "touch event x:" + x + ",y:" + y + ", event:" + eventAction);

        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:
                boolean clickInLegRect = false;
                for (LegRect legRect : legRectMap.values()) {
                    if (legRect.contains(x, y)) {
                        legRect.setSelect(true);
                        curSelectedLeg = legRect;
                        clickInLegRect = true;

                        showHeightSeekBar();
                    } else {
                        legRect.setSelect(false);
                    }
                }

                if (!clickInLegRect) {
                    for (LegRect legRect : legRectMap.values()) {
                        legRect.setSelect(false);
                    }
                    curSelectedLeg = null;
                    hideHeightSeekBar();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (curSelectedLeg != null) {
                    curSelectedLeg.setPos(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;

        }
        invalidate();
        return true;
    }
}
