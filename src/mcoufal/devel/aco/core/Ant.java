package mcoufal.devel.aco.core;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import mcoufal.devel.aco.ui.MainClass;

/**
 * Class representing an ant in Ant Colony Optimisation algorithm.
 * 
 * @author Martin Coufal <xmcoufal@gmail.com>
 * @date Nov 25, 2017
 */
public class Ant {// TODO: check if arraylist is OK with my cause(order of items in list)

	// logger
	private static final Logger LOG = Logger.getLogger(Ant.class.getName());

	// lists of all crossroads
	private ArrayList<CrossroadPoint> allCrossroads;
	// lists of visited crossroads
	private ArrayList<CrossroadPoint> visitedCrossroads = new ArrayList<CrossroadPoint>();
	// number of available crossroads(places to visit)
	private int numberOfCrossroads;
	// pheromone levels of trails
	private double[] trailsProbability;
	// current crossroad (where the ant is)
	private CrossroadPoint currentCrossroad = null;
	// pheromone levels of trails
	private double[][] tau;
	// increment of pheromone levels
	private double[][] deltaTau;
	// visibility between two crossroads
	private double[][] eta;
	// impact parameter of pheromone
	private double alpha;
	// impact parameter of visibility
	private double beta;
	// current path length
	private double pathLength = 0;
	// pheromone quantity given by each ant per cycle
	private int quantityOfPheromone = 100;

	public Ant(AntColonyAlgorithm algorithm, ArrayList<CrossroadPoint> allCrossroads) {
		this.numberOfCrossroads = allCrossroads.size();
		this.allCrossroads = allCrossroads;
		this.currentCrossroad = this.allCrossroads.get(new Random().nextInt(numberOfCrossroads));
		visitedCrossroads.add(currentCrossroad);
		this.alpha = algorithm.getAlpha();
		this.beta = algorithm.getBeta();
		this.tau = algorithm.getTau();
		this.deltaTau = algorithm.getDeltaTau();
		this.eta = algorithm.getEta();
		this.quantityOfPheromone = algorithm.getQuantityOfPheromone();
		trailsProbability = new double[numberOfCrossroads];
	}

	/**
	 * Compute probability of an ant going from current point to other points that
	 * are defined.
	 */
	public void computeNewProbabilities() {
		for (int j = 0; j < trailsProbability.length; j++) {
			// can't go to current point
			if (j == currentCrossroad.getID()) {
				trailsProbability[j] = 0.000;
				continue;
			}

			// can't go to already visited location
			if (visitedCrossroads.contains(allCrossroads.get(j))) {
				trailsProbability[j] = 0.0;
				continue;
			}
			double numerator = (Math.pow(tau[currentCrossroad.getID()][j], alpha)
					* Math.pow(eta[currentCrossroad.getID()][j], beta));
			double denominator = 0;
			for (int k = 0; k < numberOfCrossroads; k++) {
				if (!visited(allCrossroads.get(k))) {
					denominator += Math.pow(tau[currentCrossroad.getID()][k], alpha)
							* Math.pow(eta[currentCrossroad.getID()][k], beta);
				}
			}
			trailsProbability[j] = numerator / denominator;
		}
	}

	/**
	 * Return probability of going from current point to point given by
	 * {@code destination} parameter.
	 * 
	 * @param destination
	 * @return
	 */
	public double getProbability(CrossroadPoint destination) {
		return trailsProbability[destination.getID()];
	}

	/**
	 * Check whether ant already visited crossroad given by {@code p}. Returns true
	 * if yes, false otherwise.
	 * 
	 * @param p
	 * @return
	 */
	public Boolean visited(CrossroadPoint p) {
		if (visitedCrossroads.contains(p))
			return true;
		else
			return false;
	}

	/**
	 * Moves ant stochastically to the next crossroad based on pheromone trails and
	 * crossroad visibility.
	 */
	public void goToNextCrossroad() {
		double rand = new Random().nextDouble();
		double sum = 0;
		for (int i = 0; i < numberOfCrossroads; i++) {
			// can't go there, skip
			if (trailsProbability[i] == 0.000) {
				continue;
			}
			sum += trailsProbability[i];
			if (rand <= sum) {
				pathLength += currentCrossroad.getDistance(allCrossroads.get(i).getCoordinates());
				currentCrossroad = allCrossroads.get(i);
				visitedCrossroads.add(currentCrossroad);
				return;
			}
		}
	}

	/**
	 * @return current length of the ant's path.
	 */
	public double getPathLength() {
		return pathLength;
	}

	/**
	 * @return list of visited crossroads.
	 */
	public ArrayList<CrossroadPoint> getPath() {
		return visitedCrossroads;
	}

	public void computeIncreaseOfPheromone() {
		CrossroadPoint prev = null;
		for (CrossroadPoint crossroad : visitedCrossroads) {
			if (prev != null) {
				deltaTau[prev.getID()][crossroad.getID()] += quantityOfPheromone / getPathLength();
			}
			prev = crossroad;
		}

	}
}
