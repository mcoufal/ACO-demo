package mcoufal.devel.aco.core;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class representing ant colony.
 * 
 * This class provides simple abstraction to work with Ant Colony Optimisation
 * algorithm.
 * 
 * @author Martin Coufal <xmcoufal@gmail.com>
 * @date Nov 25, 2017
 */
public class AntCycleAlgorithm implements AntColonyAlgorithm {

	// logger
	private static final Logger LOG = Logger.getLogger(AntCycleAlgorithm.class.getName());
	// list of all ants
	private ArrayList<Ant> antColony = new ArrayList<Ant>();

	// best path
	private ArrayList<CrossroadPoint> bestPath = new ArrayList<CrossroadPoint>();
	// best path length
	private double bestPathLength = Double.MAX_VALUE;

	// algorithm variables

	// maximum number of algorithm iterations
	private int maximumIterations;
	// total number of ants
	private int numberOfAnts;
	// total number of crossroads
	private int numberOfCrossroads;
	// list of all crossroads
	private ArrayList<CrossroadPoint> allCrossroads;
	// pheromone levels of trails
	private double[][] tau;
	// increment of pheromone levels
	private double[][] deltaTau;
	// visibility between two crossroads
	private double[][] eta;
	// impact parameter of pheromone
	private int alpha;
	// impact parameter of visibility
	private int beta;
	// pheromone quantity given by each ant per cycle
	private int quantityOfPheromone = 100;
	// pheromone evaporation parameter
	private double ro = 0.1;

	/**
	 * TODO: ALGORITHM VARIABLES SHOULD BE HERE! Initialise colony.
	 * 
	 * @param numberOfAnts
	 */
	public AntCycleAlgorithm(int maximumIterations, int numberOfAnts, int numberOfCrossroads,
			ArrayList<CrossroadPoint> allCrossroads, int a, int alpha, int beta, int quantityOfPheromone, double ro) {

		// initialise algorithm variables
		this.maximumIterations = maximumIterations;
		this.numberOfAnts = numberOfAnts;
		this.allCrossroads = allCrossroads;
		this.numberOfCrossroads = numberOfCrossroads;
		this.alpha = alpha;
		this.beta = beta;
		this.quantityOfPheromone = quantityOfPheromone;
		this.ro = ro;

		// initialise tables
		tau = new double[numberOfCrossroads][numberOfCrossroads];
		deltaTau = new double[numberOfCrossroads][numberOfCrossroads];
		eta = new double[numberOfCrossroads][numberOfCrossroads];

		// initialise pheromone levels
		for (int i = 0; i < numberOfCrossroads; i++) {
			for (int j = 0; j < numberOfCrossroads; j++) {
				tau[i][j] = a;
			}
		}

		// compute visibility values
		for (int i = 0; i < numberOfCrossroads; i++) {
			for (int j = 0; j < numberOfCrossroads; j++) {
				if (i == j)
					eta[i][j] = 0.0;
				else {
					eta[i][j] = 1 / allCrossroads.get(i).getDistance(allCrossroads.get(j).getCoordinates());
				}
			}
		}
	}

	/**
	 * Initialise algorithm variables before iteration.
	 * 
	 * This involves renewing maximum number of iterations(in case user decides to
	 * change it), reseting all ants and crossroads and finally, reset delta tau
	 * values.
	 * 
	 * @param numberOfAnts
	 * @param numberOfCrossroads
	 * @param maximumIterations
	 */
	public void initialiseBeforeIteration(int numberOfAnts, int numberOfCrossroads, int maximumIterations) {

		// maximum number of cycles of Ant Cycle algorithm
		this.maximumIterations = maximumIterations;

		// create ants
		this.numberOfAnts = numberOfAnts;
		for (int i = 1; i <= numberOfAnts; i++) {
			antColony.add(new Ant(this, allCrossroads));
		}

		// initialise increments of pheromones
		for (int i = 0; i < numberOfCrossroads; i++) {
			for (int j = 0; j < numberOfCrossroads; j++) {
				deltaTau[i][j] = 0;
			}
		}
	}

	/**
	 * Does one iteration in Ant Cycle algorithm.
	 */
	public void step() {
		initialiseBeforeIteration(numberOfAnts, numberOfCrossroads, maximumIterations);
		// go through all crossroads
		for (int ik = 1; ik <= numberOfCrossroads - 1; ik++) {
			for (Ant ant : antColony) {
				ant.computeNewProbabilities();
				ant.goToNextCrossroad();
			}
		}

		// find best path
		for (Ant ant : antColony) {
			if (ant.getPathLength() < bestPathLength) {
				// save best path
				bestPathLength = ant.getPathLength();
				bestPath = new ArrayList<CrossroadPoint>(ant.getPath().size());
				for (CrossroadPoint crossroad : ant.getPath())
					bestPath.add(crossroad);
			}
		}

		// increase of pheromones (delta tau)
		for (Ant ant : antColony) {
			ant.computeIncreaseOfPheromone();
		}
		// renew pheromone levels on given paths
		// go through only right side of diagonal (path [i][j] is the same as path
		// [j][i])
		for (int i = 0; i < numberOfCrossroads; i++) {
			for (int j = i + 1; j < numberOfCrossroads; j++) {
				tau[i][j] = (1 - ro) * tau[i][j] + (deltaTau[i][j] + deltaTau[j][i]);
				tau[j][i] = tau[i][j];
			}
		}
	}

	/**
	 * TODO Not implemented yet!
	 * 
	 * @return
	 */
	public Boolean isOver() {
		return false;
	}

	/**
	 * TODO Not implemented yet!
	 * 
	 * @return
	 */
	public void run() {
		for (int i = 1; i <= this.maximumIterations; i++) {
			step();
			if (isOver())
				break;
		}
	}

	public ArrayList<CrossroadPoint> getBestPath() {
		return bestPath;
	}

	public double getBestPathLength() {
		return bestPathLength;
	}

	/**
	 * @return Alpha - impact parameter of pheromone.
	 */
	public int getAlpha() {
		return alpha;
	}

	/**
	 * @return Beta - impact parameter of visibility.
	 */
	public int getBeta() {
		return beta;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public double[][] getTau() {
		return tau;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public double[][] getDeltaTau() {
		return deltaTau;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public double[][] getEta() {
		return eta;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public int getQuantityOfPheromone() {
		return quantityOfPheromone;
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	public double getRo() {
		return ro;
	}
}
