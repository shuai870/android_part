package com.example.lbstest;
import android.util.Log;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.List;
public class TrackManager {

    private static final String TAG = "TrackManager";

    // 轨迹点集合
    private final List<LatLng> routePoints = new ArrayList<>();

    // 宽度（默认容忍20米偏离）
    private double ellipseWidth = 20.0;

    // 回调接口
    public interface DeviationListener {
        void onOutOfTrack(LatLng currentPoint);
        void onBackOnTrack(LatLng currentPoint);
    }
    private DeviationListener listener;
    private boolean wasOnTrackLastCheck = true;

    public void setRoute(List<LatLng> points) {
        routePoints.clear();
        routePoints.addAll(points);
    }

    public void setEllipseWidth(double width) {
        this.ellipseWidth = width;
    }

    public void setDeviationListener(DeviationListener listener) {
        this.listener = listener;
    }

    public void checkCurrentPosition(LatLng currentPoint) {
        boolean onTrack = isPointOnTrack(currentPoint);

        if (!onTrack && wasOnTrackLastCheck) {
            Log.w(TAG, "偏离轨迹！");
            if (listener != null) listener.onOutOfTrack(currentPoint);
        } else if (onTrack && !wasOnTrackLastCheck) {
            Log.i(TAG, "已回到轨迹");
            if (listener != null) listener.onBackOnTrack(currentPoint);
        }

        wasOnTrackLastCheck = onTrack;
    }
    private boolean isPointOnTrack(LatLng p) {
        for (int i = 0; i < routePoints.size() - 1; i++) {
            LatLng f1 = routePoints.get(i);
            LatLng f2 = routePoints.get(i + 1);

            double distFoci = DistanceUtil.getDistance(f1, f2);
            double distSum = DistanceUtil.getDistance(p, f1) + DistanceUtil.getDistance(p, f2);
            double a = (distFoci + ellipseWidth) / 2.0;

            if (distSum <= 2 * a) return true;
        }
        return false;
    }
}
