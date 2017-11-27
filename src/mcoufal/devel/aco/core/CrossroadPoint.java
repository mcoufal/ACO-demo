package mcoufal.devel.aco.core;

import java.awt.Point;

/**
 * Class representing crossroad in Ant Colony Optimization Algorithm.
 * 
 * @author Martin Coufal <xmcoufal@gmail.com>
 * @date Nov 25, 2017
 */
public class CrossroadPoint {

	// ID of the given crossroad
	private int id;
	// crossroad coordinates
	private Point coordinates = null;

	/**
	 * TODO
	 * @param id
	 * @param coordinates
	 */
	public CrossroadPoint(int id, Point coordinates) {
		this.id = id;
		this.coordinates = coordinates;
	}

	/**
	 * Get distance of this crossroad and point p.
	 * 
	 * @param p
	 * @return
	 */
	public double getDistance(Point p) {
		return Math.hypot(this.getCoordinates().getX() - p.getX(), this.getCoordinates().getY() - p.getY());
	}

	/**
	 * Get coordinates of this crossroad.
	 * 
	 * @return
	 */
	public Point getCoordinates() {
		return this.coordinates;
	}

	/**
	 * Returns ID of given crossroad.
	 * @return
	 */
	public int getID () {
		return this.id;
	}
}
