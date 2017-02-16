package pso1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;

public class Particle {

	
	//nodes initilaze
	Map<Id<Node>, ? extends Node> nodes;
	
	

	//fi speed update
	private final double fi=4.1;
	
	//c1 speed update
	private final double c1=2.05;
	
	//c2 speed update
	private final double c2=2.05;
	
	//x speed update
	private final double x=0.729;

	
	//maximum velocity
	private final int max_velocity = 3000;
	
	//neighbor left
	private Particle leftNeighbor;
	
	//neighbor left
	private Particle rightNeighbor;
	
	// nodeid source
	private int sourceId;
	
	//nodeId destination
	private int destinationId;
	
	//Position represent the priority
	private int [] positionVector;
	
	
	//The partical velocity
	private int []  velocityVector;
	
	// max number of nodes in the network
	private int NMAX=200;
	private ParticlePath path;
	private double fitness;
	private final double penalty=0.0;
	// best position of the particle
	private Particle pBest = null;
	
	// best position of the particle neighborhood
	private Particle nBest = null;
	
	public Map<Id<Node>, ? extends Node> getNodes() {
		return nodes;
	}

	public void setNodes(Map<Id<Node>, ? extends Node> nodes) {
		this.nodes = nodes;
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
	
	public int[] getPositionVector() {
		return positionVector;
	}

	public void setPositionVector(int[] positionVector) {
		this.positionVector = positionVector;
	}
	
	public int getNbnodes(){
		return NMAX;
	}
	
	public void setNodes(int nbNodes){
		NMAX = nbNodes;
	}
	
	public int[] getVelocityVector() {
		return velocityVector;
	}

	public void setVelocityVector(int[] velocityVector) {
		this.velocityVector = velocityVector;
	}

	public void updateVelocity(){
		//v = x[v+c1r1(bid -xid)+ c2r2(bnid-xid)]
		 int[] pBest = this.pBest.getPositionVector();
		 int[] nBest = this.nBest.getPositionVector();
		 int [] xid = this.positionVector;
		for(int i =0; i< velocityVector.length; i++){
			double r1 = ThreadLocalRandom.current().nextDouble(0,1);
			double r2 = ThreadLocalRandom.current().nextDouble(0,1);
			double velocityTmp = x*(velocityVector[i] + c1*r1*(pBest[i]-xid[i]) +c2*r2*(nBest[i]-xid[i]) );
			velocityVector[i]=(int)(velocityTmp);
		}
	}
	
	//tbd velocity clamping
	public void clampVelocity(){
		for(int i=0; i < positionVector.length;i++){
			if(positionVector[i] > max_velocity ){
				positionVector[i]=max_velocity;
			}else if(positionVector[i] < -max_velocity){
				positionVector[i]=-max_velocity;
			}
		}
	}
	
	public ParticlePath generatePath(){
		ParticlePath partialPath = new ParticlePath();
		int k =0;
		int tk= sourceId-1;
		int dest = destinationId-1;
		partialPath.addNodeId(tk+1);
		int [] xk = positionVector;
		xk[tk]=-50000;
		while(k<NMAX && tk!=dest ){
			//connNodesId: nodes id having direct link with node tk
			int [] connNodesIds = connectedNodes(nodes,tk+1);
			// index node with heighestPriority
			int indexHighPriorityNode = 0;
			//select node with highest priority
			int heighestPriority= positionVector[connNodesIds[indexHighPriorityNode]-1];
			for(int i = 1; i< connNodesIds.length; i++){
				if(heighestPriority < positionVector[connNodesIds[i]-1]){
					heighestPriority = positionVector[connNodesIds[i]-1];
					indexHighPriorityNode = i;
				}
			}
			//test if Id of the-to-be-next node - Id of present node > -4
			if ((connNodesIds[indexHighPriorityNode]-1)-tk <= -4){
				// search for another node which satisfy the id-next - id-current > -4
				// copy the connected node ids without the node id with highest priority to newConnectedIds	 
				int j = 0;
				int [] newConnNodesId = new int [connNodesIds.length-1];
				for(int i=0; i < connNodesIds.length; i++)
				{
				   if(i != indexHighPriorityNode){
					   newConnNodesId[j++] = connNodesIds[i];
				   }
				}
				// search the highest priority in the new array 
				for(int i = 0; i< newConnNodesId.length; i++){
					if(heighestPriority < positionVector[connNodesIds[i]-1]){
						heighestPriority = positionVector[connNodesIds[i]-1];
						indexHighPriorityNode = i;
					}
				}
				tk=newConnNodesId[indexHighPriorityNode]-1;
			}else{
				tk=connNodesIds[indexHighPriorityNode]-1;
			}
			//add node id to the partial path
			partialPath.addNodeId(tk+1);
			//decrease the added node id priority
			xk[tk]=-50000;
			//update the path cost
			double cost = linkCost(nodes,tk+1,destinationId);
			double currentCost= partialPath.getCost();
			double newCost= currentCost + cost;
			partialPath.setCost(newCost);
			// increment the iteration process
			k++;
			
		}
		setPath(partialPath);
		return partialPath;
	}
	
	public double linkCost(Map<Id<Node>, ? extends Node> nodes , int source, int destination){
		double cost=0.0;
		Map<Id<Link>, ? extends Link> outLinks = nodes.get(Id.createNodeId(source)).getOutLinks();
		//get the link between source and destination
		for (Entry<Id<Link>, ? extends Link> entry : outLinks.entrySet()) {
			Link link = entry.getValue();
			Node destNode =link.getToNode();
			String destNodeString = destNode.getId().toString();
			int destNodeInt = Integer.valueOf(destNodeString);
			if(destNodeInt==destination){
				cost=link.getLength();
				break;
			}
		}
		return cost;
	}
	
	public boolean isValidPath(ParticlePath path, int destination){
		boolean isValid = false;
		List<Integer> listNodes =  path.getPath();
		int pathSize = listNodes.size();
		if (listNodes.get(pathSize-1)==destination){
			isValid=true;
		}
		return isValid;
		
	}

	public int [] connectedNodes(Map<Id<Node>, ? extends Node> nodes,int nodeId){
		//System.out.println("connectedNodes fct node size : "+nodes.size());
		//System.out.println("connectedNodes fct nodeId : "+nodeId);
		//System.out.println("connectedNodes fct Id.createNodeId(nodeId) "+Id.createNodeId(nodeId));
		Map<Id<Link>, ? extends Link> outLinks = nodes.get(Id.createNodeId(nodeId)).getOutLinks();
		int [] connNodes=null;
		if(outLinks!=null){
			connNodes=new int [outLinks.size()];
			int i =0;
			for (Entry<Id<Link>, ? extends Link> entry : outLinks.entrySet()) {
				Link link = entry.getValue();
				Node node =link.getToNode();
				String idStr = node.getId().toString();
				int idInt= Integer.valueOf(idStr);
				connNodes[i]=idInt;
				i++;
			}
		}
		
		return connNodes;
	}
	
	public void initPositionVector(Map<Id<Node>, ? extends Node> networkNodes){

		//Initialize position vector
		positionVector = new int [NMAX];
		for (Entry<Id<Node>, ? extends Node> entry : networkNodes.entrySet()) {
			int randomNum = ThreadLocalRandom.current().nextInt(-100,100);
			Id<Node> idNode= entry.getKey(); 
			String idNodeString = idNode.toString();
			int idNodeInt = Integer.valueOf(idNodeString);
			positionVector[idNodeInt-1]=randomNum;
		}
		
	}
	
	public void initVelocityVector(Map<Id<Node>, ? extends Node> networkNodes){
		
		
		//Initialize position vector
		velocityVector = new int [NMAX];
		for (Entry<Id<Node>, ? extends Node> entry : networkNodes.entrySet()) {
			int randomNum = ThreadLocalRandom.current().nextInt(-10,10);
			Id<Node> idNode= entry.getKey(); 
			String idNodeString = idNode.toString();
			int idNodeInt = Integer.valueOf(idNodeString);
			velocityVector[idNodeInt-1]=randomNum;
		}
				
	}
	
	
	public void calculateVelocity(){
		
	}
	
	public void velocityClamping(){
		
	}
	
	public void updateParticlePosition(){
		
	}
	
	public void updateFitness(){
		if(!isValidPath(this.path, destinationId)){
			setFitness(0);
		}else{
			setFitness(1/path.getCost());
		}
	}
	
	public void updatepBest(){
		//during initialize
		if(pBest == null){
			pBest=this;
		}else{
			if (fitness > pBest.getFitness()){
				pBest=this;
			}
		}
	}
	
	public void updatenBest(){
		//System.out.println("leftNeighbor.getFitness() " +leftNeighbor.getFitness());
		double leftFitness = leftNeighbor.getFitness();
		double rightFitness = rightNeighbor.getFitness();
		if(leftFitness > rightFitness){
			setnBest(leftNeighbor);
		}else{
			setnBest(rightNeighbor);
		}	
	}
	
	
	public ParticlePath getPath() {
		return path;
	}
	public void setPath(ParticlePath path) {
		this.path = path;
	}
	public double getFitness() {
		return fitness;
	}
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	public double getPenalty() {
		return penalty;
	}
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

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public int getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(int destinationId) {
		this.destinationId = destinationId;
	}

	
}
