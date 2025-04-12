package com.example.lbstest;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
public class RotatedEllipseUtil {
    /**
     * 构造旋转椭圆上的点集合
     * @param f1 第一个焦点（起点）
     * @param f2 第二个焦点（终点）
     * @param width 椭圆总宽度（通过控制椭圆“胖瘦”程度）
     * @param numPoints 构造点的数量（越多越圆滑）
     * @return List<LatLng> 组成椭圆轮廓的点
     */
    public static List<LatLng> buildRotatedEllipse(LatLng f1, LatLng f2, double width, int numPoints) {
        List<LatLng> points = new ArrayList<>();

        // 中心点
        double cx = (f1.longitude + f2.longitude) / 2.0;
        double cy = (f1.latitude + f2.latitude) / 2.0;

        // 焦距c = 两点距离/2

        double c = DistanceUtil.getDistance(f1, f2) / 2.0;

        // 半长轴a = 焦距 + 额外宽度/2
        double a = c + width / 2.0;
        double b = Math.sqrt(a * a - c * c);

        // 旋转角度
        double dx = f2.longitude - f1.longitude;
        double dy = f2.latitude - f1.latitude;
        double theta = Math.atan2(dy, dx);


        // 经纬度转换系数
        double metersPerLat = 111000.0; // 每纬度约 111km
        double metersPerLng = 111000.0 * Math.cos(Math.toRadians(cy)); // 每经度约为纬度的 cos 倍


        for (int i = 0; i < numPoints; i++) {
            double t = 2 * Math.PI * i / numPoints;
            double x = a * Math.cos(t);
            double y = b * Math.sin(t);

            // 旋转变换
            double xRot = x * Math.cos(theta) - y * Math.sin(theta);
            double yRot = x * Math.sin(theta) + y * Math.cos(theta);

            // 转换成经纬度偏移
            double latOffset = yRot / metersPerLat;
            double lngOffset = xRot / metersPerLng;
            points.add(new LatLng(cy + latOffset, cx + lngOffset));
        }

        return points;
    }

    /**
     * 构建椭圆多边形 Overlay（用于百度地图）
     * @param f1 焦点1
     * @param f2 焦点2
     * @param width 椭圆“宽度”
     * @return PolygonOptions 用于 addOverlay()
     */
    public static PolygonOptions getEllipseOverlay(LatLng f1, LatLng f2, double width) {
        List<LatLng> ellipsePoints = buildRotatedEllipse(f1, f2, width, 100);
        return new PolygonOptions()
                .points(ellipsePoints)
                .stroke(new Stroke(3, Color.BLUE))
                .fillColor(0x220000FF);
    }
}
