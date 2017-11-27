package mcoufal.devel.aco.ui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import mcoufal.devel.aco.core.AntColonyAlgorithm;
import mcoufal.devel.aco.core.AntCycleAlgorithm;
import mcoufal.devel.aco.core.CrossroadPoint;

public class MainClass {
	// logger
	private static final Logger LOG = Logger.getLogger(MainClass.class.getName());

	// algorithm variables

	// maximum number of algorithm iterations
	private static int maximumIterations = 1000;
	// total number of ants
	private static int numberOfAnts = 30;
	// total number of crossroads
	private static int numberOfCrossroads = 6;
	// initial pheromone levels
	private static int a = 1;
	// impact parameter of pheromone
	private static int alpha = 1;
	// impact parameter of visibility
	private static int beta = 1;
	// pheromone quantity given by each ant per cycle
	private static int quantityOfPheromone = 100;
	// pheromone evaporation parameter
	private static double ro = 0.1;

	public static void main(String[] args) {
		Point A = new Point(0, 0);
		Point B = new Point(1, 1);
		Point C = new Point(2, 0);
		Point D = new Point(3, 1);
		Point E = new Point(4, 0);
		Point F = new Point(5, 1);
		// list of all crossroads
		ArrayList<CrossroadPoint> allCrossroads = new ArrayList<CrossroadPoint>();
		allCrossroads.add(new CrossroadPoint(0, A));
		allCrossroads.add(new CrossroadPoint(1, B));
		allCrossroads.add(new CrossroadPoint(2, C));
		allCrossroads.add(new CrossroadPoint(3, D));
		allCrossroads.add(new CrossroadPoint(4, E));
		allCrossroads.add(new CrossroadPoint(5, F));
		LOG.log(Level.FINE, "Created crossroads...");

		AntColonyAlgorithm alg = new AntCycleAlgorithm(maximumIterations, numberOfAnts, numberOfCrossroads,
				allCrossroads, a, alpha, beta, quantityOfPheromone, ro);
		LOG.log(Level.FINE, "Created algorithm...");

		for (int i = 1; i <= 20; i++) {
			alg.step();
			String path = "";
			for (CrossroadPoint p : alg.getBestPath()) {
				path += String.format("[%d]", p.getID());
			}
			System.out.println(String.format("[%d] Best solution: %s with length of: %f", i, path,
					alg.getBestPathLength()));
		}
	}

}
