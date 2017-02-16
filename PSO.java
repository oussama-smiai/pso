package pso1;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.network.NodeImpl;
import org.matsim.core.scenario.ScenarioUtils;

public class PSO {
	
	private static final String CONFIG = "C:/Users/oussama.smiai/matsim_workspace/elios/config.xml";


	
	public static void main(String [] args){
	
		Config config = ConfigUtils.loadConfig(CONFIG);
		Scenario scenario = ScenarioUtils.loadScenario(config) ;
		Network network = scenario.getNetwork();
		
		System.out.println(" number of network links : "+network.getLinks().size());
		System.out.println(" number of network nodes : "+network.getNodes().size());

		
		//Particle p= new Particle();

	
		
		//retrieve network nodes to initilize the vector priority and the vector velocity
		Map<Id<Node>, ? extends Node> nodes = network.getNodes();
		//Integer node = 5;
		//Node n = nodes.get(Id.createNodeId(0));
		//System.out.println("node "+n);
		// set nb nodes of the particle	
		/*p.setNodes(nodes.size()); 
		p.initPositionVector(nodes);
		
		int [] positionVector = p.getPositionVector();
		System.out.println("test entry positions initialize");
		System.out.println("positionVector size "+	positionVector.length);
		// display position vector
		for (int i=0; i <positionVector.length ; i++) {
			System.out.println("position  "+i +" Value positionVector "+ positionVector[i]);
		}
		
		p.initVelocityVector(nodes);
		int [] velocityVector = p.getVelocityVector();
		System.out.println("test entry velocities initialize");
		System.out.println("velocityVector size "+	velocityVector.length);
		
		// display velocity vector
		for (int i=0; i <velocityVector.length ; i++) {
			System.out.println("position  "+i +" Value velocityVector "+ velocityVector[i]);
		}*/
		nodes.get(Id.createNodeId(0));
		// create a ring of 30 particles 
		int populationSize =30;
		PSO pso = new PSO();
		Particle [] particlePopulation = pso.createRing( populationSize);
		int source=1;
		int destination=15;
		//initilize particle population positions and velocities
		for(int i=0; i < particlePopulation.length; i++){
			particlePopulation[i].initPositionVector(nodes);
			particlePopulation[i].initVelocityVector(nodes);
			//set nodes
			particlePopulation[i].setSourceId(source);
			particlePopulation[i].setDestinationId(destination);
			particlePopulation[i].setNodes(nodes);
			//evaluate fitness of each particle			
			//construct a path from particle position vector
			particlePopulation[i].generatePath();
			//calculate fitness 
			particlePopulation[i].updateFitness();
			// update pBest
			particlePopulation[i].updatepBest();
			//update nBest
			particlePopulation[i].updatenBest();
			
			
		}
		
		//PSO main loop
		int max_iteration = 500;
		int i = 0;
		while(i<max_iteration){
			for(int k=0; k <particlePopulation.length;k++){
				//calculate velocity
				particlePopulation[k].calculateVelocity();
				// clamp velocity
				particlePopulation[k].clampVelocity();
				// update position of each particle
				particlePopulation[k].updateParticlePosition();
				//update fitness
				particlePopulation[k].updateFitness();
				//update pBest
				particlePopulation[k].updatepBest();
				//update nBest
				particlePopulation[k].updatenBest();

			}
			i++;
		}
		
		//display nBest
		double maxFitness= particlePopulation[0].getpBest().getFitness();
		Particle bestSolution = particlePopulation[0].getpBest();
		for(int j=1 ;j<particlePopulation.length;j++){
			if(particlePopulation[j].getpBest().getFitness() > maxFitness){
				maxFitness=particlePopulation[j].getpBest().getFitness();
				bestSolution = particlePopulation[j].getpBest();
			}
		}
		
		System.out.println("shortest path " +bestSolution.getPath().getPath());
		System.out.println("min cost " +bestSolution.getPath().getCost());

		
	
	}
	
	public Particle [] createRing(int populationSize){
		Particle[] ring = new Particle[populationSize];

		//initilize particles 
		for(int i=0; i< ring.length; i++){
			ring[i]= new Particle();
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
}
