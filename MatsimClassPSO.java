package org.matsim.routing1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.vehicles.Vehicle;
import pso2.Particle;
import pso2.ParticlesRing;

public class MatsimClassPSO implements LeastCostPathCalculator {

	private final Network network;

	public MatsimClassPSO(Network network, TravelDisutility travelCosts,
			TravelTime travelTimes) {
			this.network = network;

	}
	
	@Override
	public Path calcLeastCostPath(Node fromNode, Node toNode, double starttime,
			Person person, Vehicle vehicle) {
		int populationSize =30;
		Id<Node> source = fromNode.getId();
		Id<Node> destination = toNode.getId();
		Map<Id<Node>, ? extends Node> nodes = network.getNodes();
		ParticlesRing particlesRing = new ParticlesRing(populationSize, nodes, source, destination);
		Particle [] particlePopulation = particlesRing.getRing();
		//initilize particle population positions and velocities
		for(int i=0; i < particlePopulation.length; i++){
			//evaluate fitness of each particle			
			//construct a path from particle position vector
			particlePopulation[i].generatePath();
			//calculate fitness 
			//TODO solve add link issue
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
				//calculate velocity and clamp it
				particlePopulation[k].updateVelocity();;
				// update position of each particle
				particlePopulation[k].updatePositions();
				//update fitness
				particlePopulation[k].updateFitness();
				//update pBest
				particlePopulation[k].updatepBest();
				//update nBest
				particlePopulation[k].updatenBest();
				
			}
			i++;
		}
			
		pso2.Path bestPath = null;
		double bestPathCost =Double.MAX_VALUE;
		for(int j = 0 ; j < particlePopulation.length; j++){
			Particle particleTmp = particlePopulation[j];
			Particle pBestTmp = particleTmp.getpBest();
			pso2.Path path = pBestTmp.getPath();
			if(path.isValid(source, destination)){
				System.out.println("valid path");
				for(int k = 0 ; k < path.getNodesIds().size() ; k++){
					System.out.print(" node : "+path.getNodeId(k)+" ");
				}
				double costPath = pBestTmp.getPath().getPathCost();
				if(costPath < bestPathCost ){
					bestPath = path;
					bestPathCost = costPath;
				}
			}
		}
		
		//convert from pso2.Path to matsim.path
		//retrieve nodes of best path
		List<Node> nodesPath = new ArrayList<Node>();
		for(int l = 0 ; l < bestPath.getNodesIds().size(); l++){
			Node nodeTmp = nodes.get(bestPath.getNodeId(l));
			nodesPath.add(nodeTmp);
		}
		//retrieve links of best path
		List<Link> linksPath = bestPath.getLinks();
		
		//
		//Path path = new Path(nodes, links, travelTime, travelCost);
		
		//generate path from the class to integrate PSO2.Particle.java
		
		// TODO Auto-generated method stub
		return new Path(nodesPath, linksPath, 0.0, bestPath.getPathCost());
	}
	
}
