package pso2;

import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

public class PSOAlgo {
	private static final String CONFIG = "C:/Users/oussama.smiai/matsim_workspace/elios/config.xml";

	public static void main(String [] args){
		Config config = ConfigUtils.loadConfig(CONFIG);
		Scenario scenario = ScenarioUtils.loadScenario(config) ;
		Network network = scenario.getNetwork();
		Map<Id<Node>, ? extends Node> nodes = network.getNodes();
		// create a ring of 30 particles 
		int populationSize =30;
		Id<Node> source = Id.createNodeId(1014134898);
		Id<Node> destination = Id.createNodeId(387275586);
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
		//print the paths of all particles
		Path pathParticle0 = particlePopulation[0].getPath();
		int nbPathNodes= pathParticle0.getNodesIds().size();
		System.out.println("nbPathNodes : "+nbPathNodes);
		for(int k = 0 ; k < nbPathNodes; k++){
			System.out.print(" node : "+pathParticle0.getNodeId(k));
		}
		
		
		/*Path bestPath = null;
		double bestPathCost =Double.MAX_VALUE;
		for(int j = 0 ; j < particlePopulation.length; j++){
			Particle particleTmp = particlePopulation[j];
			Particle pBestTmp = particleTmp.getpBest();
			Path path = pBestTmp.getPath();
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
		System.out.println("optimal path nodes : ");
		if(bestPath != null){
			for(int k = 0 ; k < bestPath.getNodesIds().size() ; k++){
				System.out.print("node : "+bestPath.getNodeId(k)+" ");
			}
		}else{
			System.out.println("bestPath is null");
		}
		System.out.println();
		//System.out.println("optimal path cost :"+ bestPath.getPathCost());*/
	}
}
