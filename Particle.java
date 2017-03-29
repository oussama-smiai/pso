package pso2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;

public class Particle {
	
	private PositionMap positionMap;
	
	private VelocityMap velocityMap;
	
	private Particle pBest = null;
	
	private Particle nBest = null;
	
	private Particle leftNeighbor;
	
	private Particle rightNeighbor;
	
	private double fitness;
	
	private Path path;
	
	private Id<Node> source;
	
	private Id<Node> destination;
	
	private int nmax=500;
	
	private Map<Id<Node>, ? extends Node> nodes;
	
	private int minPositionValue;
	
	private int maxPositionValue;
	
	//fi speed update
	private final double fi=4.1;
	
	//c1 speed update
	private final double c1=2.05;
	
	//c2 speed update
	private final double c2=2.05;
	
	//x speed update
	private final double x=0.729;
	
	//maximum velocity
	private final int MAX_VELOCITY = 3000;
		
	public Particle getpBest() {
		return pBest;
	}

	public void setpBest(Particle pBest) {
		this.pBest = pBest;
	}

	public Particle getnBest() {
		return nBest;
	}

	public void setnBest(Particle nBest) {
		this.nBest = nBest;
	}
	
	public Particle(Map<Id<Node>, ? extends Node> nodes, Id<Node> source, Id<Node> destination){
		this.setNodes(nodes);
		this.setSource(source);
		this.setDestination(destination);
		this.nmax = nodes.size();
	}
	
	public Path generatePath(){
		
		List<Id<Node>> pathNodesId= new ArrayList<Id<Node>>();
		List<Link> pathLinks = new ArrayList<Link>();
		Path partialPath = new Path(pathNodesId,pathLinks);
		try{
			int k =0;
			Id<Node> tk= source;
			partialPath.addNodeId(tk);
			PositionMap xk = positionMap;
			xk.updatePosition(tk, new Position(-50000));
			
			while(k<nmax && tk!=destination ){
				//connNodesId: nodes id having direct link with node tk
				List<Id<Node>> connNodesIds = connectedNodes(nodes,tk);
				if(connNodesIds.size() > 1){									
					// initialize highest priority node
					Id<Node> nodeHighest = connNodesIds.get(0);
					Position positionhighest = positionMap.getPosition(nodeHighest);
					// initialize highest priority
					int highestPriority = positionhighest.getPositionValue();
					for(int i = 1; i < connNodesIds.size(); i++){
						Id<Node> nodeId = connNodesIds.get(i);
						Position position = positionMap.getPosition(nodeId);
						int positionValue = position.getPositionValue();
						if(highestPriority < positionValue){
							highestPriority = positionValue;
							nodeHighest = nodeId;
						}
					}
					BigInteger tkNodeIdValue = new BigInteger(tk.toString());
					BigInteger nodeHighsetIdValue = new BigInteger(nodeHighest.toString());
					BigInteger diff = tkNodeIdValue.subtract(nodeHighsetIdValue);
					if(BigInteger.valueOf(-4).compareTo(diff) >= 0){
						// search for another node which satisfy the id-next - id-current > -4
						//copy the connectedNode without the highest node found previously
						List <Id<Node>> newConnectedNodes= new ArrayList<Id<Node>>(connNodesIds.size()-1); 
						for(int i = 0; i < connNodesIds.size(); i++){
							Id<Node> nodeId = connNodesIds.get(i);
							if(nodeId != nodeHighest){
								newConnectedNodes.add(nodeId);
							}
						}
						// initialize highest priority node
						nodeHighest = newConnectedNodes.get(0);
						positionhighest = positionMap.getPosition(nodeHighest);
						// initialize highest priority
						highestPriority = positionhighest.getPositionValue();
						// search the new highestPriority node
						for(int i = 1; i < newConnectedNodes.size(); i++){
							Id<Node> nodeId = newConnectedNodes.get(i);
							Position position = positionMap.getPosition(nodeId);
							int positionValue = position.getPositionValue();
							if(highestPriority < positionValue){
								highestPriority = positionValue;
								nodeHighest = nodeId;
							}
						}
					}
					//tk = nodeHighest;
					partialPath.addNodeId(nodeHighest);
					positionMap.updatePosition(nodeHighest, new Position(-50000));
					//update the path cost
					Link link = getLinkFromSourceToDestination(nodes,tk,nodeHighest);
					partialPath.addLink(link);
					// update tk to become latest added node
					tk = nodeHighest;
					// update number of iteration
					k++;
				}else if(connNodesIds.size() == 1){
					Id<Node> selectedNode = connNodesIds.get(0);
					partialPath.addNodeId(selectedNode);
					positionMap.updatePosition(selectedNode, new Position(-50000));
					//update the path cost
					Link link = getLinkFromSourceToDestination(nodes,tk,selectedNode);
					partialPath.addLink(link);
					// update tk to become latest added node
					tk = selectedNode;
					// update number of iteration
					k++;
				}
				
			}
			 setPath(partialPath);
			 return partialPath;
		}
		catch(Exception e){
			e.printStackTrace();
			setPath(partialPath);
			return partialPath;
		}
	}
	
	public Link getLinkFromSourceToDestination(Map<Id<Node>, ? extends Node> nodes , Id<Node> source, Id<Node> destination){
		Link link=null;
		Map<Id<Link>, ? extends Link> outLinks = nodes.get(source).getOutLinks();
		//get the link between source and destination
		for (Entry<Id<Link>, ? extends Link> entry : outLinks.entrySet()) {
			Link linkTmp = entry.getValue();
			Node destNodeTmp =linkTmp.getToNode();
			if(destNodeTmp.getId() == destination){
				link=linkTmp;
				break;
			}
		}
		return link;
	}
		
	public List<Id<Node>> connectedNodes(Map<Id<Node>, ? extends Node> nodes,Id<Node> nodeId){
		
		Map<Id<Link>, ? extends Link> outLinks = nodes.get(nodeId).getOutLinks();
		List<Id<Node>> connNodes=new ArrayList<Id<Node>>();
		if(outLinks!=null){
			for (Entry<Id<Link>, ? extends Link> entry : outLinks.entrySet()) {
				Link link = entry.getValue();
				Node node =link.getToNode();
				connNodes.add(node.getId());
			}
		}	
		return connNodes;
	}

	public boolean isAValidPath(Path path, Id<Node> source, Id<Node> destination){
		return(path.getSourceNodeId() == source && path.getDestinationNodeId() == destination );
	}
	
	
	
	public Particle getLeftNeighbor() {
		return leftNeighbor;
	}

	public void setLeftNeighbor(Particle leftNeighbor) {
		this.leftNeighbor = leftNeighbor;
	}

	public Particle getRightNeighbor() {
		return rightNeighbor;
	}

	public void setRightNeighbor(Particle rightNeighbor) {
		this.rightNeighbor = rightNeighbor;
	}

	public double getFitness() {
		return fitness;
	}
	
	public void updateFitness() {
		if(isAValidPath(getPath(),getSource(),getDestination())){
			double pathLength= getPathLength();
			double inverseOfLength = 1/pathLength;
			setFitness(inverseOfLength);
		}else{
			setFitness(0);
		}
	}
	
	public double getPathLength(){
		List<Link> links = path.getLinks();
		double pathLength= 0;
		for(int i = 0 ; i <links.size() ; i++){
			pathLength += links.get(i).getLength(); 
		}
		return pathLength;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public Id<Node> getSource() {
		return source;
	}

	public void setSource(Id<Node> source) {
		this.source = source;
	}

	public Id<Node> getDestination() {
		return destination;
	}

	public void setDestination(Id<Node> destination) {
		this.destination = destination;
	}

	
	public void updatepBest(){
		if(pBest == null){
			setpBest(this);
		}else{
			if (fitness > pBest.getFitness()){
				setpBest(this);
			}
		}
	}
	
	public void updatenBest(){
		double leftFitness = leftNeighbor.getFitness();
		double rightFitness = rightNeighbor.getFitness();
		if(leftFitness > rightFitness){
			setnBest(leftNeighbor);
		}else{
			setnBest(rightNeighbor);
		}	
	}
	
	/**
	 * Update the velocity Map
	 */
	public void updateVelocity(){
		//v = x[v+c1r1(bid -xid)+ c2r2(bnid-xid)]
		PositionMap pBestPositions = pBest.getPositionMap();
		PositionMap nBestPositions = nBest.getPositionMap();
		PositionMap xid = positionMap;
		double r1 = ThreadLocalRandom.current().nextDouble(0,1);
		double r2 = ThreadLocalRandom.current().nextDouble(0,1);
		Map <Id<Node>, Velocity> velocities = velocityMap.getVelocities();
		for (Map.Entry<Id<Node>, Velocity> velocityMapElement : velocities.entrySet()){
			int velocity = velocityMapElement.getValue().getVelocityValue();
			Id<Node> nodeId = velocityMapElement.getKey(); 
			int pBestValue = pBestPositions.getPosition(nodeId).getPositionValue();
			int xidValue = xid.getPosition(nodeId).getPositionValue();
			int nBestValue = nBestPositions.getPosition(nodeId).getPositionValue();
			double newVelocity = x*(velocity + c1*r1*(pBestValue-xidValue) +c2*r2*(nBestValue-xidValue) );			
			//clamp velocity
			int clampedVelocity = clampVelocity((int)(newVelocity));
			velocities.put(nodeId, new Velocity(clampedVelocity));
		}
	}
	
	/**
	 * Update the velocity Map
	 */
	public void updateVelocityJnuit(){
		//v = x[v+c1r1(bid -xid)+ c2r2(bnid-xid)]
		PositionMap pBestPositions = pBest.getPositionMap();
		PositionMap nBestPositions = nBest.getPositionMap();
		PositionMap xid = positionMap;
		double r1 = 0.3;
		double r2 = 0.6;
		Map <Id<Node>, Velocity> velocities = velocityMap.getVelocities();
		for (Map.Entry<Id<Node>, Velocity> velocityMapElement : velocities.entrySet()){
			int velocity = velocityMapElement.getValue().getVelocityValue();
			Id<Node> nodeId = velocityMapElement.getKey(); 
			int pBestValue = pBestPositions.getPosition(nodeId).getPositionValue();
			int xidValue = xid.getPosition(nodeId).getPositionValue();
			int nBestValue = nBestPositions.getPosition(nodeId).getPositionValue();
			double newVelocity = x*(velocity + c1*r1*(pBestValue-xidValue) +c2*r2*(nBestValue-xidValue) );			
			//clamp velocity
			int clampedVelocity = clampVelocity((int)(newVelocity));
			velocities.put(nodeId, new Velocity(clampedVelocity));
		}
	}
	
	/**
	 * Update the Position Map using eq 2
	 */
	public void updatePositions(){
		
		//xid = xid + vid
		Map<Id<Node>, Position> positions =  positionMap.getPositions();
		Map<Id<Node>, Velocity> velocities =  velocityMap.getVelocities();
		for(Map.Entry<Id<Node>, Position> positionMapElement : positions.entrySet()){
			Id<Node> nodeId = positionMapElement.getKey();
			Position position = positionMapElement.getValue();
			Velocity velocity = velocities.get(nodeId);
			int newPosition = position.getPositionValue() + velocity.getVelocityValue();
			positions.put(nodeId, new Position(newPosition));
		}
		
	}
	
	//tbd velocity clamping
	public int clampVelocity(int velocity){
		int clampedVelocity = velocity;
		if(velocity > MAX_VELOCITY){
			clampedVelocity = MAX_VELOCITY;
		}else if(velocity < -MAX_VELOCITY){
			clampedVelocity = -MAX_VELOCITY;
		}
		return clampedVelocity;
	}
	
	public void calculatenBest(){
		
	}
	
	public void calculteVelocity(){
		
	}
	
	public PositionMap getPositionMap() {
		return positionMap;
	}

	public void setPositionMap(PositionMap positionMap) {
		this.positionMap = positionMap;
	}

	public VelocityMap getVelocityMap() {
		return velocityMap;
	}

	public void setVelocityMap(VelocityMap velocityMap) {
		this.velocityMap = velocityMap;
	}

	public int getNmax() {
		return nmax;
	}

	public void setNmax(int nmax) {
		this.nmax = nmax;
	}

	public Map<Id<Node>, ? extends Node> getNodes() {
		return nodes;
	}

	public void setNodes(Map<Id<Node>, ? extends Node> nodes) {
		this.nodes = nodes;
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

	
}
