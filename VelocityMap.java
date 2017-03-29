package pso2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Node;

public class VelocityMap {
	
	private  int maxVelocityValue = 100;
	
	private Map<Id<Node>, Velocity> velocities;
	
	private int velocityMapSize;
	
	private int minVelocityValue = -100;
	
	public Map<Id<Node>, Velocity> getVelocities() {
		return velocities;
	}

	public void setVelocityMap(Map<Id<Node>, Velocity> velocityMap) {
		this.velocities = velocityMap;
	}

	public int getMinVelocityValue() {
		return minVelocityValue;
	}

	public void setMinVelocityValue(int minPositionValue) {
		this.minVelocityValue = minPositionValue;
	}

	public int getMaxVelocityValue() {
		return maxVelocityValue;
	}

	public void setMaxVelocityValue(int maxPositionValue) {
		this.maxVelocityValue = maxPositionValue;
	}
	
	public int getVelocityMapSize() {
		return velocityMapSize;
	}

	public void setVelocityMapSize(int velocityMapSize) {
		this.velocityMapSize = velocityMapSize;
	}

	public VelocityMap(Map<Id<Node>, ? extends Node> nodes, int minPositionValue,int maxPositionValue){	
		setVelocityMapSize(nodes.size());
		velocities = new HashMap<Id<Node> ,Velocity>(this.velocityMapSize);
		this.minVelocityValue = minPositionValue;
		this.maxVelocityValue = maxPositionValue;
	}

	public void initVelocityMap(Map<Id<Node>, ? extends Node> nodes){
		
		for (Map.Entry<Id<Node>, ? extends Node> entry : nodes.entrySet()) {
			Id<Node> idNode = entry.getKey();
		    int velocityValue = ThreadLocalRandom.current().nextInt(minVelocityValue,maxVelocityValue);
			Velocity velocity = new Velocity(velocityValue);
		    velocities.put(idNode, velocity);
		}
		
	}
}
