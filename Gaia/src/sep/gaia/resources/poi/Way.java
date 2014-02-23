package sep.gaia.resources.poi;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import sep.gaia.util.FloatVector3D;

/**
 * Describes a way having certain attributes and being composed of multiple nodes.
 * @author Matthias Fisch (specification), Matthias Fisch (implementation)
 *
 */
public class Way {

	/**
	 * Set of the IDs of the nodes forming this way.
	 */
	private Set<String> nodeReferences;
	
	/**
	 * The positions of the nodes forming this way.
	 */
	private Collection<FloatVector3D> nodePositions = new LinkedList<>();
	
	/**
	 * Attributes of this way as key-value-pairs.
	 */
	private Map<String, String> attributes;
	
	/**
	 * Initializes the way with its attributes and node-IDs.
	 * @param nodeReferences The IDs of the nodes forming this node.
	 * @param attributes Attributes of this way as key-value-pairs.
	 */
	public Way(Collection<String> nodeReferences, Map<String, String> attributes) {
		this.nodeReferences = new HashSet<>(nodeReferences);
		this.attributes = new HashMap<>(attributes);
	}

	/**
	 * Returns the IDs of the nodes forming this node.
	 * @return The IDs of the nodes forming this node.
	 */
	public Set<String> getNodeReferences() {
		return nodeReferences;
	}

	/**
	 * Sets the IDs of the nodes forming this node.
	 * @param nodeReferences The IDs of the nodes forming this node.
	 */
	public void setNodeReferences(Set<String> nodeReferences) {
		this.nodeReferences = nodeReferences;
	}

	/**
	 * Returns the attributes of this way as key-value-pairs.
	 * @return The attributes of this way as key-value-pairs.
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * Sets the attributes of this way as key-value-pairs.
	 * @param attributes The attributes of this way as key-value-pairs.
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * Returns if the node with the ID <code>id</code> is part of this way.
	 * @param id The ID of the node to check.
	 * @return <code>true</code> if the node is part of the way.
	 */
	public boolean containsNode(String id) {
		return nodeReferences.contains(id);
	}
	
	/**
	 * Adds an nodes position to the nodes forming this way.
	 * @param position The position to add.
	 */
	public void addNode(FloatVector3D position) {
		nodePositions.add(position);
	}

	/**
	 * Returns the positions of the nodes forming this way.
	 * @return The positions of the nodes forming this way.
	 */
	public Collection<FloatVector3D> getNodePositions() {
		return nodePositions;
	}
}
