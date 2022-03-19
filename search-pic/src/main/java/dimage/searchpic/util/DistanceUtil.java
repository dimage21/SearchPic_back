package dimage.searchpic.util;

public class DistanceUtil {
    public static int getHaversineDistance(double startX, double startY, double endX, double endY){
        double radius = 6371e3; // 지구 반지름(m)
        double deltaLongitude = Math.toRadians(endX - startX);

        startY = Math.toRadians(startY);
        endY = Math.toRadians(endY);
        double distance = Math.acos(
                        Math.sin(startY) * Math.sin(endY)
                        + Math.cos(startY) * Math.cos(endY) * Math.cos(deltaLongitude)
                        ) * radius;
        return (int) distance;
    }
}
