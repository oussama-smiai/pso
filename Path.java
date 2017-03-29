package pso2;

import java.util.List;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;

public class Path {

	private List<Id<Node>> nodesIds ;
	
	private List<Link> links ;
	

	public Path(List<Id<Node>> nodesIds, List<Link> links) {
		this.nodesIds = nodesIds;
		this.links = links;
	}
	
	public Id<Node> getSourceNodeId(){
		return nodesIds.get(0);
	}
	
	public Id<Node> getDestinationNodeId(){
		return nodesIds.get(nodesIds.size()-1);
	}
	
	
	public List<Id<Node>> getNodesIds() {
		return nodesIds;
	}

	public void setNodesIds(List<Id<Node>> nodesIds) {
		this.nodesIds = nodesIds;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	public boolean addNodeId(Id<Node> idNode){
		return nodesIds.add(idNode);
	}
	
	public boolean addLink(Link link){
		return links.add(link);
	}
	
	public double getPathCost(){
		double cost=0.0;
		for(Link link: links){
			cost += link.getLength();
		}
		return cost;
	}
	
	public Id<Node> getNodeId(int index){
		return nodesIds.get(index);
		
	}
	
	public boolean isValid(Id<Node> source, Id<Node> destination){
		return (source == getSourceNodeId() && destination == getDestinationNodeId());
	}
	
}
