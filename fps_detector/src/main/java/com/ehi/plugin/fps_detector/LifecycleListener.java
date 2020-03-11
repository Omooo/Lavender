package com.ehi.plugin.fps_detector;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Omooo
 * @version v1.0
 * @Date 2020/03/11 16:41
 * desc :
 */
public class LifecycleListener implements Application.ActivityLifecycleCallbacks {

    private int startedActivityCounter = 0;
    private LifecycleCallbackListener mListener;

    LifecycleListener(LifecycleCallbackListener listener) {
        mListener = listener;
    }

    public interface LifecycleCallbackListener {
        void onAppForeground();

        void onAppBackground();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        synchronized (this) {
            startedActivityCounter++;
            if (startedActivityCounter == 1 && mListener != null) {
                mListener.onAppForeground();
            }
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        synchronized (this) {
            startedActivityCounter--;
            if (startedActivityCounter == 0 && mListener != null) {
                mListener.onAppBackground();
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
