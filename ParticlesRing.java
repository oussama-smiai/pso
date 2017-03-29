package pso2;

import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Node;

import pso2.Particle;

public class ParticlesRing {
 
   
	private Particle [] ring;
	private static int minPosition = -100;
	private static int maxPosition = 100;
	private static int minVelocity = -10;
	private static int maxVelocity = 10;
	
	public ParticlesRing(int populationSize,Map<Id<Node>, ? extends Node> nodes, Id<Node> source, Id<Node> destination ){
		this.ring = createRing(populationSize,nodes, source, destination);
	}
	
	public Particle [] createRing(int populationSize, Map<Id<Node>, ? extends Node> nodes, Id<Node> source, Id<Node> destination){
		Particle[] ring = new Particle[populationSize];

		//initilize particles 
		for(int i=0; i< ring.length; i++){
			//init positions
			PositionMap positions = new PositionMap(nodes,minPosition,maxPosition);
			positions.initPositionMap(nodes);
			//init velocity
			VelocityMap velocities = new VelocityMap(nodes, minVelocity, maxVelocity);
			velocities.initVelocityMap(nodes);
			Particle particle = new Particle(nodes, source, destination);;
			particle.setPositionMap(positions);
			particle.setVelocityMap(velocities);
			ring[i]= particle;
		}
		
		//create the links
		for(int i=0; i< ring.length; i++){
		if(i==0){
				ring[i].setLeftNeighbor(ring[populationSize-1]);
				ring[i].setRightNeighbor(ring[i+1]);
			}else if(i==(ring.length)-1){
				ring[i].setLeftNeighbor(ring[i-1]);
				ring[i].setRightNeighbor(ring[0]);
			}else{
				ring[i].setLeftNeighbor(ring[i-1]);
				ring[i].setRightNeighbor(ring[i+1]);
			}
			
		}
		
		return ring;
	}

	public Particle [] getRing() {
		return ring;
	}


	public void setRing(Particle [] ring) {
		this.ring = ring;
	}
	
  
}
