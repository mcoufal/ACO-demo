package mcoufal.devel.aco.core;

import java.util.ArrayList;

public interface AntColonyAlgorithm {
	// TODO comments
	// TODO define all needed functions
	public void step();
	public void run();
	public ArrayList<CrossroadPoint> getBestPath();
	public double getBestPathLength();
	
	
	public double[][] getTau();
	public double[][] getDeltaTau();
	public double[][] getEta();
	public int getAlpha();
	public int getBeta();
	public int getQuantityOfPheromone();
	public double getRo();
}
