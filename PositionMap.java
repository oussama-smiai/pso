package pso2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Node;

public class PositionMap {

	private  int maxPositionValue = 100;
	
	private Map<Id<Node>, Position> positions;
	
	private int positionMapSize;
	
	private int minPositionValue = -100;
	
	public void updatePosition(Id<Node> nodeId, Position position){		
		positions.put(nodeId, position);
	}
	
	public Map<Id<Node>, Position> getPositions() {
		return positions;
	}

	public void setPositions(Map<Id<Node>, Position> positions) {
		this.positions = positions;
	}

	public int getMinPositionValue() {
		return minPositionValue;
	}

	public void setMinPositionValue(int minPositionValue) {
		this.minPositionValue = minPositionValue;
	}

	public int getMaxPositionValue() {
		return maxPositionValue;
	}

	public void setMaxPositionValue(int maxPositionValue) {
		this.maxPositionValue = maxPositionValue;
	}
	
	public int getPositionMapSize() {
		return positionMapSize;
	}

	public void setPositionMapSize(int positionVectorSize) {
		this.positionMapSize = positionVectorSize;
	}

	public PositionMap(Map<Id<Node>, ? extends Node> nodes, int minPositionValue,int maxPositionValue){	
		setPositionMapSize(nodes.size());
		positions = new HashMap<Id<Node> ,Position>(this.positionMapSize);
		this.minPositionValue = minPositionValue;
		this.maxPositionValue = maxPositionValue;
	}

	public void initPositionMap(Map<Id<Node>, ? extends Node> nodes){
		
		for (Map.Entry<Id<Node>, ? extends Node> entry : nodes.entrySet()) {
			Id<Node> idNode = entry.getKey();
		    int positionValue = ThreadLocalRandom.current().nextInt(minPositionValue,maxPositionValue);
			Position position = new Position(positionValue);
		    positions.put(idNode, position);
		}
		
	}
	
	public Position getPosition(Id<Node> nodeId){
		return positions.get(nodeId);
		
	}
	


}
