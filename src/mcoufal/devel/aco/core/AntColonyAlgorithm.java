package mcoufal.devel.aco.core;

import java.util.ArrayList;

public interface AntColonyAlgorithm {
	public void step();
	public void run();
	public ArrayList<CrossroadPoint> getBestPath();
	public double getBestPathLength();
	
	
	public double[][] getTau();
	public double[][] getDeltaTau();
	public double[][] getEta();
	public double getAlpha();
	public double getBeta();
	public double getQuantityOfPheromone();
	public double getRo();
}
