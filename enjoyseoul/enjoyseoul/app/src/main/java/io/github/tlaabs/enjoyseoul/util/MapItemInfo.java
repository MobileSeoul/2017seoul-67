package io.github.tlaabs.enjoyseoul.util;

import java.io.Serializable;

/*===========================================================
              @ Since : 2017-08-25
              @ Author : tlaabs@naver.com(심세용)
 ===========================================================*/

public class MapItemInfo implements Serializable{
    String placeName;
    double x;
    double y;

    public MapItemInfo(){
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getPlaceName() {
        return placeName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
