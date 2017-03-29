package pso2test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

import pso2.Particle;
import pso2.ParticlesRing;
import pso2.Path;
import pso2.Position;
import pso2.PositionMap;
import pso2.Velocity;
import pso2.VelocityMap;

public class ParticleTest {
	
	// path for the config file of matsim
	private static final String CONFIG = "C:/Users/oussama.smiai/matsim_workspace/elios/config.xml";
	private static Scenario scenario;
	private static Network network;
	private static Map<Id<Node>, ? extends Node> nodes;
	private static int minPosition = -100;
	private static int maxPosition = 100;
	private static PositionMap positions;
	private static Map<Id<Node>, Position>  initiliazedPositionVector ;
	private static int minVelocity = -10;
	private static int maxVelocity = 10;
	private static VelocityMap velocities;
	private static Map<Id<Node>, Velocity>  initiliazedVelocityMap;
	/**
	 * read the network file of MATSIM
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		Config config = ConfigUtils.loadConfig(CONFIG);
		scenario = ScenarioUtils.loadScenario(config) ;
		network = scenario.getNetwork();
		nodes = network.getNodes();
		// init the posiiton vector
		positions = new PositionMap(nodes,minPosition,maxPosition);
		positions.initPositionMap(nodes);
		initiliazedPositionVector = positions.getPositions();
		//init the velocity vector
		 velocities = new VelocityMap(nodes,minVelocity,maxVelocity);
		velocities.initVelocityMap(nodes);
		initiliazedVelocityMap = velocities.getVelocities();
	}
	
	/**
	 * check if nodes keys are empty
	 */
	@Test
	public final void whenKeysOfNodesMapAreNotNullThenNoExceptionInThrown(){
		for(Map.Entry<Id<Node>, ? extends Node> nodeEntry : nodes.entrySet()){
			Object key =nodeEntry.getKey();
			Assert.assertNotNull(key);
		}
	}
	
	
	/**
	 * check if position values are within the range  
	 */	
	@Test
    public final void whenPositionsAreWithinTheRangeThenNoExceptionIsThrown(){
		for (Map.Entry<Id<Node>, Position> positionMapElement : initiliazedPositionVector.entrySet()) {
			  Position position = positionMapElement.getValue();
			  int positionValue = position.getPositionValue();
			  Id<Node> index = positionMapElement.getKey();
			  Assert.assertTrue("Error, index: "+index +" position: "+ positionValue +" is less than "+minPosition, positionValue >= minPosition);
			  Assert.assertTrue("Error, index: "+index +" position: "+ positionValue +" is greater than "+maxPosition,  positionValue  < maxPosition);
		}
		
	}
	
	/**
	 * check if position Map keys are not null
	 */
	@Test
	public final void whenKeysOfThePositonsMapsAreNotNullThenNoExceptionsIsThrown(){
		int i =0;
		for (Map.Entry<Id<Node>, Position> positionMapElement : initiliazedPositionVector.entrySet()){
			  Id<Node> index = positionMapElement.getKey();
			 Assert.assertNotNull("key at position "+i,index);
			 i++;
		}
	}
	
	/**
	 * check if velocity Map keys are not null
	 */
	@Test
	public final void whenKeysOfTheVelocitysMapsAreNotNullThenNoExceptionsIsThrown(){
		int i =0;
		for (Map.Entry<Id<Node>, Velocity> VelocityMapElement : initiliazedVelocityMap.entrySet()){
			  Id<Node> index = VelocityMapElement.getKey();
			 Assert.assertNotNull("key at position "+i,index);
			 i++;
		}
	}
	
	/**
	 * PositionMap Node Id should be equal to network node ids
	 */
	@Test
    public final void whenPositionMapIdAreEqualToNetWorkNodesIdsThenNoExceptionIsThrown(){	
		for(Map.Entry<Id<Node>, ? extends Node> nodeMapElement : nodes.entrySet()){
			Id<Node> idNode = nodeMapElement.getKey();
			Position position =  initiliazedPositionVector.get(idNode);
			Assert.assertTrue("the node id "+idNode.toString()+" does not exist in the position vector",position != null);
		}
		
	}
	
	/**
	 * check if particle.updateVelocity change the velocity value
	 */
	@Test
	public final void whenVelocityIsChangedNoExceptionIsThrown(){
		//init positions
		PositionMap positions = new PositionMap(nodes,minPosition,maxPosition);
		positions.initPositionMap(nodes);
		//init velocity
		VelocityMap velocities = new VelocityMap(nodes, minVelocity, maxVelocity);
		velocities.initVelocityMap(nodes);
		// make a copy of velocities
		Map<Id<Node>, Velocity> velocitiesCopy = new HashMap<Id<Node>, Velocity>();
		Map<Id<Node>, Velocity> velocityOriginal = velocities.getVelocities();
		for(Map.Entry<Id<Node>, Velocity> velocityMapElement : velocityOriginal.entrySet()){
			Velocity v = velocityMapElement.getValue();
			velocitiesCopy.put(velocityMapElement.getKey(), new Velocity(v.getVelocityValue()));
		}
		// init particle
		Id<Node> source = Id.createNodeId(1);
		Id<Node> destination = Id.createNodeId(13);
		Particle particle = new Particle(nodes,source,destination);
		particle.setPositionMap(positions);
		particle.setVelocityMap(velocities);
		//TBD init velocity of pbest and nBest init pbest and nbest
		//nBest position map
		PositionMap positionsNBest = new PositionMap(nodes,minPosition,maxPosition);
		positionsNBest.initPositionMap(nodes);
		//nBest velocity map
		VelocityMap velocitiesNBest = new VelocityMap(nodes, minVelocity, maxVelocity);
		velocitiesNBest.initVelocityMap(nodes);
		//init particle nBest
		Particle nBest = new Particle(nodes,source,destination);
		nBest.setPositionMap(positionsNBest);
		nBest.setVelocityMap(velocitiesNBest);
		//pBest position map
		PositionMap positionsPBest = new PositionMap(nodes,minPosition,maxPosition);
		positionsPBest.initPositionMap(nodes);
		//pBest velocity map
		VelocityMap velocitiesPBest = new VelocityMap(nodes, minVelocity, maxVelocity);
		velocitiesPBest.initVelocityMap(nodes);
		//init particle pBest
		Particle pBest = new Particle(nodes,source,destination);
		pBest.setPositionMap(positionsNBest);
		pBest.setVelocityMap(velocitiesNBest);
		
		//set particle nbest and pbest
		particle.setpBest(pBest);
		particle.setnBest(nBest);
		particle.updateVelocityJnuit();
		VelocityMap updatedVelocity = particle.getVelocityMap();
		Map<Id<Node>,Velocity > velocitiesUpdated = updatedVelocity.getVelocities();
		//compare old velocities and updated ones
		for(Map.Entry<Id<Node>, Velocity> velocityMapElement : velocitiesUpdated.entrySet()){
			Id<Node> nodeId = velocityMapElement.getKey();	
			Velocity oldVelocityObject = velocitiesCopy.get(nodeId);
			int velocity = oldVelocityObject.getVelocityValue();
			PositionMap pBestPositions = pBest.getPositionMap();
			int pBestValue = pBestPositions.getPosition(nodeId).getPositionValue();
			PositionMap xid = positions;
			int xidValue = xid.getPosition(nodeId).getPositionValue();
			PositionMap nBestPositions = nBest.getPositionMap();
			int nBestValue = nBestPositions.getPosition(nodeId).getPositionValue();
			double x=0.729;
			double r1 = 0.3;
			double r2 = 0.6;
			double c1=2.05;
			double c2=2.05;
			double calculatedVelocity = x*(velocity + c1*r1*(pBestValue-xidValue) +c2*r2*(nBestValue-xidValue) );			 			
			int updatedVelocityValue = velocityMapElement.getValue().getVelocityValue();
			Assert.assertTrue("error updatedVelocityValue: "+updatedVelocityValue +" != calculatedVelocity: "+calculatedVelocity,updatedVelocityValue == (int)calculatedVelocity);
		}
		
	}
	
	
	/**
	 * Check if  velocities value are within the range 
	 */
	@Test
    public final void whenVelocitiesAreWhithinTheRangeThenNoExceptionIsThrown(){
		
		
		for (Map.Entry<Id<Node>, Velocity> velocityMapElement : initiliazedVelocityMap.entrySet()) {
			  Velocity velocity = velocityMapElement.getValue();
			  int velocityValue = velocity.getVelocityValue();
			  Assert.assertTrue("Error, velocity "+ velocityValue +"is less than "+minVelocity, velocityValue >= minVelocity);
			  Assert.assertTrue("Error, velocity "+ velocityValue +"is greater than "+maxVelocity,  velocityValue  < maxVelocity);
		}
	}
	
	/**
	 * VelocityMap Node Id should be equal to network node ids
	 */
	@Test
    public final void whenVelocityMapIdAreEqualToNetWorkNodesIdsThenNoExceptionIsThrown(){

		for(Map.Entry<Id<Node>, ? extends Node> nodeMapElement : nodes.entrySet()){
			Id<Node> idNode = nodeMapElement.getKey();
			Velocity velocity =  initiliazedVelocityMap.get(idNode);
			Assert.assertTrue("the node id "+idNode.toString()+" does not exist in the position vector",velocity != null);
		}
		
	}
	
	/**
	 * Path should add node with addnode method
	 */
	@Test
	public final void whenPathAddNodeNoExceptionIsThrown(){
		List <Id<Node>> nodesIds = new ArrayList<Id<Node>>();
		List <Link> links = new ArrayList<Link>();
		Path path = new Path(nodesIds, links);
		Id<Node> idNode = Id.createNodeId(10);
		path.addNodeId(idNode);
		Id<Node> addedIdNode =path.getDestinationNodeId();
		Assert.assertTrue(idNode == addedIdNode);
	}
	
	/**
	 * Path should add source and destination with addnode method
	 */
	@Test
	public final void whenPathAddSourceAndDestinationNoExceptionIsThrown(){
		List <Id<Node>> nodesIds = new ArrayList<Id<Node>>();
		List <Link> links = new ArrayList<Link>();
		Path path = new Path(nodesIds, links);
		Id<Node> source = Id.createNodeId(1);
		path.addNodeId(source);
		Id<Node> node = Id.createNodeId(5);
		path.addNodeId(node);
		Id<Node> destination = Id.createNodeId(10);
		path.addNodeId(destination);
		Id<Node> sourceFromPath =path.getSourceNodeId();
		Assert.assertTrue(source == sourceFromPath);
		Id<Node> destinationFromPath= path.getDestinationNodeId();
		Assert.assertTrue(destination == destinationFromPath);
		
	}
	
	/**
	 * Path should add nodes in order
	 */
	@Test
	public final void whenNodesAreAddedInOrderInThePathNoExceptionIsThrown(){
		// add the nodes to the path
		List <Id<Node>> nodesIds = new ArrayList<Id<Node>>();
		List <Link> links = new ArrayList<Link>();
		Path path = new Path(nodesIds, links);
		for(int i=0; i< 15; i++ ){
			path.addNodeId(Id.createNodeId(i));
		}
		//read the nodes from the path
		for(int i = 0 ; i< 15 ; i++){
			Id<Node> id = path.getNodeId(i);
			Assert.assertTrue("addded id different of found id",id == Id.createNodeId(i));
		}
	}
	
	/**
	 * Update position in positionMap
	 */
	@Test 
	public final void whenaPositionIsUpdatedNoExceptionIsThrown(){		
		Id<Node> idNode = Id.createNodeId(10);
		Position position = positions.getPosition(idNode);
		Assert.assertTrue(position.getPositionValue() >= -100);
		Assert.assertTrue(position.getPositionValue() < 100);
	}
	
	
	/**
	 * Particle should return the path between source and destination 
	 */
	@Test
	public final void whenThereIsAPathBetweenSourceAndDestinationNoExceptionIsThrown(){
		//init positions
		PositionMap positions = new PositionMap(nodes,minPosition,maxPosition);
		positions.initPositionMap(nodes);
		//init velocity
		VelocityMap velocities = new VelocityMap(nodes, minVelocity, maxVelocity);
		velocities.initVelocityMap(nodes);
		// init particle
		Id<Node> source = Id.createNodeId(1);
		Id<Node> destination = Id.createNodeId(13);
		Particle particle = new Particle(nodes,source,destination);
		particle.setPositionMap(positions);
		particle.setVelocityMap(velocities);
		Path path = particle.generatePath();
		Id<Node> sourceFromResultPath = path.getSourceNodeId();
		Assert.assertTrue(sourceFromResultPath == source);
		Id<Node> destinationFromPath = path.getDestinationNodeId();
		Assert.assertTrue(destinationFromPath == destination);
	}
	
	/**
	 * check compare of Id<Node> class nodeId1(5) == nodeId(5)
	 */
	@Test
	public final void whenComparaisonOfTwoNodeIdsWithTheSameStringReturnTrueNoExceptionIsThrown(){
		Id<Node> node1 = Id.createNodeId(5);
		Id<Node> node2= Id.createNodeId(5);
		Assert.assertTrue(node1 == node2);
	}
	
	/**
	 * check compare of Id<Node> class nodeId1(5) == nodeId(10)
	 */
	@Test
	public final void whenComparaisonOfTwoNodeIdsWithDifferentStringReturnFalseNoExceptionIsThrown(){
		Id<Node> node1 = Id.createNodeId(5);
		Id<Node> node2= Id.createNodeId(10);
		Assert.assertTrue(node1 != node2);
	}
	
	/**
	 * check if the method isValid of the class path works properly
	 */
	@Test
	public final void whenSourceAndDestinationOfAPathAreValidReturnTrue(){
		List<Id<Node>> nodesIds = new ArrayList<Id<Node>>();
		List<Link> links = new ArrayList<Link>();
		Id<Node> source = Id.createNodeId(10);
		Id<Node> destination = Id.createNodeId(7);
		Id<Node> middle = Id.createNodeId(7);
		nodesIds.add(source);
		nodesIds.add(middle);
		nodesIds.add(destination);
		Path path = new Path(nodesIds, links);
		Assert.assertTrue(source == path.getSourceNodeId());
		Assert.assertTrue(destination == path.getDestinationNodeId());
		Assert.assertTrue(path.isValid(source, destination));
		
	}
	
	
	
	
	/**
	 * check the connected nodes of the method particle.connectedNodes  are correct
	 */
	@Test
	public final void whenConnectedNodesAreCorrectThanNoExceptionIsThrown(){
		Id<Node> source = Id.createNodeId(1);
		Id<Node> destination = Id.createNodeId(10);
		Particle particle = new Particle(nodes, source, destination);
		Id<Node> nodeId = Id.createNodeId(1);
		List<Id<Node>> connNodesIds = particle.connectedNodes(nodes,nodeId);
		Assert.assertTrue(connNodesIds.size() == 1);
		Id<Node> nodeId1 = connNodesIds.get(0);
		int nodeId1Int = Integer.parseInt(nodeId1.toString());
		Assert.assertTrue(nodeId1Int == 2);
		// get the nodes connected to node id 2
		Id<Node> nodeId2 = Id.createNodeId(2);
		List<Id<Node>> connNodesIds1 = particle.connectedNodes(nodes,nodeId2);
		Assert.assertTrue(connNodesIds1.size() == 9);
	}
	
	
	/**
	 * check updatePosition assign the value -50000
	 */
	@Test
	public final void whenPositionIsUpdatedWithHighNegativeValueThenNoExceptionIsThrown(){
		// init the posiiton vector
		PositionMap positions = new PositionMap(nodes,minPosition,maxPosition);
		positions.initPositionMap(nodes);
		Id<Node> nodeId = Id.createNodeId(11);
		Position position = positions.getPosition(nodeId);	
		Assert.assertTrue("Error, position value not less than maxPosition", position.getPositionValue() < maxPosition);
		Assert.assertTrue("Error, position value not greater or equal to minPosition", position.getPositionValue() >= minPosition);
		int negativeValue = -50000;
		Position newPosition = new Position(negativeValue);
		positions.updatePosition(nodeId, newPosition);
		Position updatedPosition = positions.getPosition(nodeId);
		Assert.assertTrue(updatedPosition.getPositionValue() == -50000);

	}
	
	/**
	 * check if clampVelocity method reduce the velocity
	 */
	@Test 
	public final void whenVelocityIsClampedThanNoExceptionIsThrown(){
		Id<Node> source = Id.createNodeId(1);
		Id<Node> destination = Id.createNodeId(10);
		Particle particle = new Particle(nodes,source,destination);
		int velocity1 = 8000;
		int velocity2 = -9000;
		int clampedVelocity1 = particle.clampVelocity(velocity1);
		Assert.assertTrue(clampedVelocity1 == 3000);
		int clampedVelocity2 = particle.clampVelocity(velocity2);
		Assert.assertTrue(clampedVelocity2 == -3000);
	}
	
	/**
	 * check if particle.updatePosition(x(i)) make x(i) = x(i) + v(i)
	 */
	@Test
	public final void whenPositionIsUpdatedThanNoExceptionIsThrown(){
		//init positions
		PositionMap positions = new PositionMap(nodes,minPosition,maxPosition);
		positions.initPositionMap(nodes);
		//clone position 
		Map<Id<Node>, Position> positionsCopy = new HashMap<Id<Node>, Position>();
		Map<Id<Node>, Position> positionsOriginal = positions.getPositions();
		for(Map.Entry<Id<Node>, Position> positionMapElement : positionsOriginal.entrySet()){
			Position position = new Position(positionMapElement.getValue().getPositionValue());
			positionsCopy.put(positionMapElement.getKey(), position);
		}
		//init velocity
		VelocityMap velocities = new VelocityMap(nodes, minVelocity, maxVelocity);
		velocities.initVelocityMap(nodes);
		Map<Id<Node>, Velocity> velocitiesMap = velocities.getVelocities();
		Id<Node> source = Id.createNodeId(1);
		Id<Node> destination = Id.createNodeId(10);
		Particle particle = new Particle(nodes,source,destination);
		particle.setPositionMap(positions);
		particle.setVelocityMap(velocities);
		particle.updatePositions();
		for(Map.Entry<Id<Node>, Position> positionMapElement : positionsOriginal.entrySet()){
			int updatedPosition = positionMapElement.getValue().getPositionValue();
			Id<Node> idNode = positionMapElement.getKey();			
			Velocity ithVelocity = velocitiesMap.get(idNode);
			int velocityValue = ithVelocity.getVelocityValue();
			Position oldParticle = positionsCopy.get(idNode);
			int oldPositionValue = oldParticle.getPositionValue();
			Assert.assertTrue(updatedPosition == velocityValue + oldPositionValue );
		}	
	}
	
	/**
	 * check if all particles of the nodes ring and their neighbors are not null
	 */
	public final void whenTheParticlesRingAndTheirNeighborsAreNotNullThanNoExceptionIsThrown(){
		int populationSize =30;
		Id<Node> source = Id.createNodeId(1);
		Id<Node> destination = Id.createNodeId(10);
		ParticlesRing particlesRing = new ParticlesRing(populationSize, nodes, source, destination);
		Particle [] particles = particlesRing.getRing();
		for(int i = 0 ; i < particles.length; i++){
			Assert.assertNotNull(particles[i] );
			Assert.assertNotNull(particles[i].getLeftNeighbor());
			Assert.assertNotNull(particles[i].getRightNeighbor());
		}
	}
}
