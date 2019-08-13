package org.forwater.backend.dto;

import com.vividsolutions.jts.geom.Point;

public class PointsDTO {

    private Point point;

    private String springCode;

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public String getSpringCode() {
        return springCode;
    }

    public void setSpringCode(String springCode) {
        this.springCode = springCode;
    }
}
