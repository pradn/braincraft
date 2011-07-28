package braincraft;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * DNA stores all of the genetic information for a given Brain in various data
 * structures.
 * 
 * @author Chris
 */
public class DNA implements Serializable
{
	// FIELDS:
	// TODO: Make genes a HashMap probably
	/**
	 * A HashMap mapping innovation numbers to a gene object
	 */
	private HashMap<Integer, Gene> genes;
	/**
	 * Highest innovation number in this DNA.
	 */
	private int highest;
	/**
	 * A HashMap mapping node IDs to ArrayList's of their incoming genes
	 */
	private HashMap<Integer, ArrayList<Gene>> connections;
	/**
	 * The nodes in this DNA.
	 */
	private ArrayList<NNode> nodes;

	// CONSTRUCTOR:
	/**
	 * Constructor for a new DNA object.
	 */
	protected DNA()
	{
		highest = 0;
		genes = new HashMap<Integer, Gene>();
		connections = new HashMap<Integer, ArrayList<Gene>>();
		nodes = new ArrayList<NNode>();
	}

	// GENE METHODS:
	/**
	 * Returns the number of genes in this DNA
	 * 
	 * @return number of genes in this DNA
	 */
	protected int numGenes()
	{
		return genes.size();
	}

	/**
	 * Returns a random gene innovation number from within this DNA
	 * 
	 * @return a
	 */
	// TODO: Make this faster...
	protected int getRandomGeneInnovation()
	{
		ArrayList<Integer> intarr = new ArrayList<Integer>();
		for (Integer i : genes.keySet())
		{
			intarr.add(i);
		}
		int index = Braincraft.randomInteger(intarr.size());
		return intarr.get(index);
	}

	/**
	 * Returns true if this DNA has a particular gene
	 * 
	 * @param innovation
	 *            gene innovation to test
	 * @return whether or not this DNA has this gene
	 */
	protected boolean has(int innovation)
	{
		return genes.containsKey(innovation);
	}

	/**
	 * Gets the gene at index innovation in this DNA
	 * 
	 * @param innovation
	 *            the innovation number to look for
	 * @return the gene at index innovation or null if it is not in this DNA
	 */
	protected Gene getGene(int innovation)
	{
		return genes.get(innovation);
	}

	/**
	 * Get the highest innovation number of any gene in this DNA
	 * 
	 * @return highest innovation number in this DNA
	 */
	protected int getHighestInnovation()
	{
		return highest;
	}

	/**
	 * Get a set of all the gene innovation numbers in this DNA
	 * 
	 * @return set of innovation numbers in this DNA
	 */
	protected Set<Integer> getInnovations()
	{
		return genes.keySet();
	}
	
	// CONNECTIONS METHODS:
	/**
	 * Get an ArrayList of all the Genes terminating at a particular node
	 * 
	 * @param nodeID
	 *            the terminating node
	 * @return an ArrayList with the genes terminating at this node
	 */
	protected ArrayList<Gene> getIncomingGenes(int nodeID)
	{
		if (connections.containsKey(nodeID))
		{
			return connections.get(nodeID);
		}
		return new ArrayList<Gene>();
	}

	/**
	 * Returns true if this DNA has the specified gene
	 * 
	 * @param start starting node ID
	 * @param end ending node ID
	 * @return whether or not this DNA has this gene
	 */
	protected boolean hasConnection(int start, int end)
	{
		if (!connections.containsKey(end))
		{
			return false;
		}
		ArrayList<Gene> check = connections.get(end);
		for (Gene g : check)
		{
			if (g.start == start)
				return true;
		}
		return false;
	}

	/**
	 * Adds a new gene to the various data structures in this DNA
	 * 
	 * @param element the gene to submit
	 */
	protected void submitNewConnection(Gene element)
	{
		genes.put(element.innovation, element);

		if (element.innovation > highest)
			highest = element.innovation;

		if (!connections.containsKey(element.end))
		{
			connections.put(element.end, new ArrayList<Gene>());
		}
		ArrayList<Gene> addto = connections.get(element.end);
		addto.add(element);
	}

	// NODE METHODS:
	/**
	 * Get the number of nodes in this DNA
	 * 
	 * @return number of nodes in this DNA
	 */
	protected int numNodes()
	{
		return nodes.size();
	}

	/**
	 * Get a random node ID from this DNA
	 * 
	 * @return random node ID
	 */
	protected int getRandomNodeID()
	{
		return nodes.get(Braincraft.randomInteger(nodes.size())).ID;
	}

	/**
	 * Safely submit a new node
	 * 
	 * @param node
	 *            the new node for the DNA
	 */
	protected void submitNewNode(NNode node)
	{
		nodes.add(node);
	}

	/**
	 * Returns true if this DNA has the node specified
	 * 
	 * @param ID
	 *            node to check
	 * @return true if DNA has node
	 */
	protected boolean hasNodeID(int ID)
	{
		for (NNode node : nodes)
		{
			if (ID == node.ID)
				return true;
		}
		return false;
	}

	/**
	 * Get a collection of all the nodes in this DNA
	 * 
	 * @return collection of nodes
	 */
	protected Collection<NNode> getNodes()
	{
		return nodes;
	}
	
	// INTERFACE HELPERS:
	/**
	 * Version ID for serialization
	 */
	private static final long serialVersionUID = 1L;
}
