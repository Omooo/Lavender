package com.ehi.plugin.fps_detector;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.FloatRange;

import java.text.DecimalFormat;

/**
 * @author Omooo
 * @version v1.0
 * @Date 2020/03/11 16:42
 * desc :
 */
public class FPSDetector {

    private final static Program PROGRAM = new Program();

    private FPSDetector() {

    }

    public static Program prepare(Application application) {
        return PROGRAM.prepare(application);
    }

    public static class Program implements LifecycleListener.LifecycleCallbackListener {

        private MyFrameCallback mMyFrameCallback;
        private boolean isPlaying = false;

        private Application app;
        private WindowManager wm;
        private View stageView;
        private TextView fpsText;
        private WindowManager.LayoutParams lp;

        private final DecimalFormat decimal = new DecimalFormat("#.0' fps'");

        private Program prepare(Application application) {
            mMyFrameCallback = new MyFrameCallback();
            lp = new WindowManager.LayoutParams();
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            application.registerActivityLifecycleCallbacks(new LifecycleListener(this));

            if (isOverlayApiDeprecated()) {
                lp.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                lp.type = LayoutParams.TYPE_TOAST;
            }
            lp.flags = LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_NOT_FOCUSABLE
                    | LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_TOUCHABLE;
            lp.format = PixelFormat.TRANSLUCENT;
            lp.gravity = Gravity.TOP | Gravity.END;
            lp.x = 10;

            app = application;
            wm = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
            stageView = LayoutInflater.from(app).inflate(R.layout.stage, new RelativeLayout(app));
            fpsText = stageView.findViewById(R.id.tv_fps);

            listener(new Audience() {
                @Override
                public void heartbeat(double fps) {
                    if (fps > 100) {
                        fpsText.setTextColor(Color.RED);
                    } else {
                        fpsText.setTextColor(Color.WHITE);
                    }
                    fpsText.setText(decimal.format(fps));
                }
            });
            return this;
        }

        public Program listener(Audience audience) {
            mMyFrameCallback.addListener(audience);
            return this;
        }

        @Override
        public void onAppForeground() {
            play();
        }

        @Override
        public void onAppBackground() {
            stop();
        }

        private void play() {
            if (!hasOverlayPermission()) {
                startOverlaySettingActivity();
                return;
            }
            mMyFrameCallback.start();
            if (!isPlaying) {
                wm.addView(stageView, lp);
                isPlaying = true;
            }
        }

        private void stop() {
            mMyFrameCallback.stop();

            if (isPlaying) {
                wm.removeView(stageView);
                isPlaying = false;
            }
        }

        public Program color(int color) {
            fpsText.setTextColor(color);
            return this;
        }

        public Program size(float size) {
            fpsText.setTextSize(size);
            return this;
        }

        public Program alpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
            fpsText.setAlpha(alpha);
            return this;
        }

        public Program interval(int ms) {
            mMyFrameCallback.setInterval(ms);
            return this;
        }

        public Program gravity(int gravity) {
            lp.gravity = gravity;
            return this;
        }

        private boolean isOverlayApiDeprecated() {
            return Build.VERSION.SDK_INT > 26;
        }

        private void startOverlaySettingActivity() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                app.startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + app.getPackageName())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }

        private boolean hasOverlayPermission() {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(app);
        }
    }
}
