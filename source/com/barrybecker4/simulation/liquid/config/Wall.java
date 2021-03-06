/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.config;

import com.barrybecker4.common.geometry.Location;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *  Walls form a basis for solid objects in the simulation space.
 *  They are straight lines  (either horizontal or vertical) and immovable.
 *  Endpoints of walls must be on cell boundaries
 *
 *  @author Barry Becker
 */
public class Wall {

    /** the 2 endpoints defining the wall */
    private final Line2D.Double segment_;

    /** the thickness of the wall. */
    private final float thickness_;

    /**
     * Constructor
     * The start and stop locations define the endpoints of a horizontal or vertical line.
     * @param startLocation begin point for wall
     * @param stopLocation  endpoint for wall
     */
    public Wall(Location startLocation, Location stopLocation) {
        this(startLocation.getX(), startLocation.getY(), stopLocation.getX(), stopLocation.getY());
    }

    /**
     * Constructor
     */
    public Wall( double x1, double y1, double x2, double y2 ) {
        assert (x1 == x2 || y1 == y2);
        segment_ = new Line2D.Double( x1, y1, x2, y2 );
        thickness_ = 2.0f;
    }

    public Point2D.Double getStartPoint() {
        return (Point2D.Double) segment_.getP1();
    }

    public Point2D.Double getStopPoint() {
        return (Point2D.Double) segment_.getP2();
    }

    public float getThickness() {
        return thickness_;
    }

    public boolean intersects( Rectangle2D.Double rect ) {
        return segment_.intersects( rect );
    }

    /**
     * returns true if the point lies on the wall
     */
    public boolean intersects( double i, double j, double eps ) {
        return segment_.intersects( i, j, eps, eps );
    }

    public boolean isVertical() {
        return (segment_.getX1() == segment_.getX2());
    }

    public boolean isHorizontal() {
        return (segment_.getY1() == segment_.getY2());
    }

}
