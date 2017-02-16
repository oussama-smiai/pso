package pso1;

import java.util.ArrayList;
import java.util.List;

public class ParticlePath {
	
	private List<Integer> path = new ArrayList<Integer>();
	private double cost=0;
	
	public boolean addNodeId (int nodeId){
		return path.add(nodeId);
	}
	
	
	public List<Integer> getPath() {
		return path;
	}

	public void setPath(List<Integer> path) {
		this.path = path;
	}


	public double getCost() {
		return cost;
	}


	public void setCost(double cost) {
		this.cost = cost;
	}
	

}
