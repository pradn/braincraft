package braincraft;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author Chris
 * 
 *         Exception object thrown when the network is passed an invalid number
 *         of inputs.
 */
class NetworkInputException extends Exception {
	private static final long serialVersionUID = 1L;

	public NetworkInputException() {
	}

	public NetworkInputException(String msg) {
		super(msg);
	}
}

/**
 * @author Chris
 * 
 *         Highest level encapsulation of a single Neural Network. The user
 *         interacts with Brain objects.
 */
public class Brain implements Comparable<Brain>, Serializable {
	// FIELDS:
	/**
	 * ID of this brain
	 */
	protected int ID;
	protected DNA dna;
	/**
	 * Identifier to represent whether a brain has been evaluated or not
	 */
	protected boolean alive;
	/**
	 * Fitness for this brain. Starts as null.
	 */
	protected Double fitness;
	protected Double adjustedFitness;
	private Species species;

	// CONSTRUCTORS:
	/**
	 * Constructor for a new Brain object.
	 * 
	 * @param spec
	 *            Species this Brain belongs to
	 */
	protected Brain(Species spec) {
		alive = true;
		species = spec;
		ID = spec.getNewBrainID(this);
		initializeDNA();
		spec.getTribe(this);
	}

	protected Brain(Species spec, DNA deoxy) {
		alive = true;
		species = spec;
		ID = spec.getNewBrainID(this);
		dna = deoxy;
	}

	
	// PUBLIC ACCESSOR METHODS:
	/**
	 * Returns the ID of this Brain.
	 * 
	 * @return ID
	 */
	public int getID() {
		return ID;
	}

	/**
	 * Returns the living status of the Brain.
	 * 
	 * @return alive or not
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Returns the current fitness of this Brain.
	 * 
	 * @return fitness
	 */
	public double getFitness() {
		return fitness;
	}
	
	public String printBrain() {
		// First line
		String output = "genomestart ";
		output += Integer.toString(ID) + "\n";

		// Node list
		for (NNode n : dna.getNodes()) {
			String nodestring = "node ";
			nodestring += Integer.toString(n.ID) + " ";
			nodestring += Integer.toString(species.getNode(n.ID).type) + "\n";
			output += nodestring;
		}

		// Gene list
		for (Integer ginno : dna.getInnovations()) {
			Gene g = dna.getGene(ginno);
			int bit = 0;
			if (g.enabled)
				bit = 1;
			String genestring = "gene ";
			genestring += Integer.toString(ginno) + " ";
			genestring += Integer.toString(g.start) + " ";
			genestring += Integer.toString(g.end) + " ";
			genestring += Double.toString(g.weight) + " ";
			genestring += Integer.toString(bit) + "\n";
			output += genestring;
		}

		// Last line
		output += "genomeend" + "\n";

		return output;
	}

	
	// PUBLIC AI INTERACTION METHODS:
	/**
	 * Ends the life of this Brain upon the termination of evaluation.
	 * 
	 * @param fitvalue
	 *            the evaluated fitness value for this Brain
	 */
	// TODO: Make this throw something if brain is already dead
	public void reportFitness(double fitvalue) {
		if (alive) {
			fitness = fitvalue;
			alive = false;
		}
	}

	/**
	 * Determines the outputs for a Neural Network using the given inputs.
	 * Throws NetworkInputException if number of inputs not the same as
	 * originally specified value.
	 * 
	 * @param inputs
	 *            double array representing input values for this neural net.
	 * @return double array representing output values for this neural net.
	 * @throws NetworkInputException
	 */
	public double[] evaluate(double[] inputs) throws NetworkInputException {
		if (inputs.length != species.inputs.length)
			throw new NetworkInputException(
					"Input array did not contain as many elements as previously specified.");

		for (int i = 0; i < inputs.length; i++) {
			species.inputs[i].activation = inputs[i];
		}
		for (NNode n : species.outputs) {
			n.activation = getActivation(n);
		}
		double[] retarray = new double[species.outputs.length];
		for (int i = 0; i < retarray.length; i++) {
			retarray[i] = species.outputs[i].activation;
		}
		for (NNode n : dna.getNodes()) {
			if (n.type != NNode.INPUT) {
				n.activated = false;
				n.activation = null;
			}
		}
		return retarray;
	}

	
	// PUBLIC BRAIN I/O
	/**
	 * Allows the user to save this brain using Java serializable
	 * 
	 * @param file
	 *            output file for this Brain
	 */
	public void saveObject(String file) {
		try {
			ObjectOutput out = new ObjectOutputStream(
					new FileOutputStream(file));
			out.writeObject(this);
			out.close();
		} catch (IOException e) {
			Braincraft.report("Couldn't save object to file.");
		}
	}

	/**
	 * Loads an object using Java serialization
	 * 
	 * @param file
	 *            location of serializable file
	 * @return loaded Brain object
	 */
	public static Brain loadObject(String file) {
		try {
			// Deserialize from a file
			File brain = new File(file);
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					brain));
			// Deserialize the object
			Brain loaded = (Brain) in.readObject();
			in.close();
			return loaded;
		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
			Braincraft.report("Could not load specified Brain file.");
		}
		return null;
	}

	/**
	 * Allows the user to save this brain as raw text data
	 * 
	 * @param file
	 *            output file for this Brain.
	 */
	public void saveText(String file) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(printBrain());
			out.close();
		} catch (IOException e) {
			Braincraft.reportError("Could not write Brain " + ID + " to location " + file + ".");
		}
	}

	/**
	 * Parses a saved Brain file and returns a Brain object.
	 * 
	 * @param file
	 *            the genome file
	 * @return a Brain object
	 */
	public static Brain loadText(String file) {
		return null;
	}

	
	// MUTATION METHODS:
	/**
	 * Mutate this DNA to add a node
	 */
	protected void mutateAddNode() {
		// Select the gene to be split
		int mutategene = dna.getRandomGeneInnovation();

		// Create the new genes
		Gene mutated = dna.getGene(mutategene);
		NNode addition = new NNode(NNode.HIDDEN, species);
		Gene early = new Gene(mutated.start, addition.ID, 1, species);
		Gene late = new Gene(addition.ID, mutated.end, mutated.weight, species);

		// Disable old gene
		mutated.enabled = false;

		// Submit new node
		dna.submitNewNode(addition);

		// Submit new genes
		dna.submitNewConnection(early);
		dna.submitNewConnection(late);
	}

	/**
	 * Mutate this DNA to add a link
	 */
	protected void mutateAddLink() {
		// Make sure network is not fully connected

		// Sum number of connections
		int totalconnections = dna.numGenes();

		// Find number of each type of node
		int numIn = species.inputs.length;
		int numOut = species.outputs.length;
		int numHid = dna.numNodes() - (numIn + numOut);

		// Find the number of possible connections.
		// Each input node can go to each output and each hidden. (I * (O + H))
		// Each hidden node can go to each output and each hidden. (H * (O + H))
		// Each output node can go to each output and each hidden. (O * (O + H))
		// Rearrange with algebra (I * (O + H)) + (H * (O + H)) + (O * (O + H))
		// == (I + H + O) * (O + H)
		int fullyconnected = 0;
		fullyconnected = (numIn + numHid + numOut) * (numOut + numHid);

		if (totalconnections == fullyconnected)
			return;

		// Pick 2 nodes for a new connection and submit it

		NNode randomstart;
		NNode randomend;
		do {
			randomstart = species.getNode(dna.getRandomNodeID());
			randomend = species.getNode(dna.getRandomNodeID());
		} while (randomend.type == NNode.INPUT || randomstart.equals(randomend)
				|| dna.hasConnection(randomstart.ID, randomend.ID));

		Gene newgene = new Gene(randomstart.ID, randomend.ID, Braincraft
				.randomWeight(), species);
		dna.submitNewConnection(newgene);
	}

	/**
	 * Mutate the weights of this DNA
	 */
	protected void mutateWeights() {
		for (int i = 0; i <= dna.getHighestInnovation(); i++) {
			if (dna.has(i)
					&& Braincraft.randomChance(Braincraft.perWeightMutationRate)) {
				dna.getGene(i).weight = Braincraft.randomWeight();
			}
		}
	}

	
	// LIBRARY METHODS:
	/**
	 * Crosses two parent Brains and returns an offspring.
	 * 
	 * @param b1
	 *            mother Brain
	 * @param b2
	 *            father Brain
	 * @return A new Brain representing the offspring of the mother and father
	 *         brains
	 */
	protected static Brain crossBrains(Brain b1, Brain b2) {
		DNA hifit;
		DNA lofit;

		if (b1.fitness == null || b2.fitness == null)
			return null;
		if (b1.species != b2.species)
			return null;

		DNA ret = new DNA();

		// Choose the genome with the higher fitness
		if (b1.fitness > b2.fitness) {
			hifit = b1.dna;
			lofit = b2.dna;
		} else {
			hifit = b2.dna;
			lofit = b1.dna;
		}

		// Populate gene list of ret
		for (Integer i : hifit.getInnovations()) {
			if (!lofit.has(i)) {
				ret.submitNewConnection(hifit.getGene(i));
			} else {
				Gene newgene;
				if (Braincraft.randomChance(Braincraft.inheritFromHigherFitRate))
					newgene = new Gene(hifit.getGene(i));
				else
					newgene = new Gene(lofit.getGene(i));
				if (!hifit.getGene(i).enabled
						|| !lofit.getGene(i).enabled) {
					if (Braincraft.randomChance(Braincraft.disabledRate))
						newgene.enabled = false;
					else
						newgene.enabled = true;
				}
				ret.submitNewConnection(newgene);
			}
		}

		// Populate node list of ret
		for (Integer i : ret.getInnovations()) {
			Gene g = ret.getGene(i);
			if (!ret.hasNodeID(g.start)) {
				ret.submitNewNode(b1.species.getNode(g.start));
			}
			if (!ret.hasNodeID(g.end)) {
				ret.submitNewNode(b1.species.getNode(g.end));
			}
		}
		return new Brain(b1.species, ret);
	}

	/**
	 * Returns the number of disjoint genes, the number of excess genes, and the
	 * average weight difference for two brains in indexes 0, 1, 2 respectively.
	 * 
	 * @param b1
	 *            one brain to compare
	 * @param b2
	 *            other brain to compare
	 * @return double[] a double array with 3 elements
	 */
	private static double[] getDisjointExcessWeightCount(Brain b1, Brain b2) {
		DNA d1 = b1.dna;
		DNA d2 = b2.dna;
		int highest1 = d1.getHighestInnovation();
		int highest2 = d2.getHighestInnovation();
		int excess = 0;
		int disjoint = 0;
		double weightdif = 0;
		int matching = 0;
		DNA larger;
		int lowest = Math.min(highest1, highest2);
		int highest = Math.max(highest1, highest2);

		// Calculate which genome has a higher innovation
		if (highest == highest1)
			larger = d1;
		else
			larger = d2;

		// Calculate excess
		for (int i = lowest + 1; i <= highest; i++) {
			if (larger.has(i)) {
				excess++;
			}
		}

		// Calculate disjoint
		for (int i = 0; i <= lowest; i++) {
			boolean d1has = d1.has(i);
			boolean d2has = d2.has(i);
			if ((d1has || d2has) && !(d1has && d2has)) {
				disjoint++;
			}
			if (d1has && d2has) {
				weightdif += Math.abs(d1.getGene(i).weight
						- d2.getGene(i).weight);
				matching++;
			}
		}
		double[] ret = new double[3];
		ret[0] = disjoint;
		ret[1] = excess;
		ret[2] = weightdif / matching;
		return ret;
	}

	/**
	 * Returns true if two Brains pass the compatibility threshold test
	 * 
	 * @param b1
	 *            test Brain one
	 * @param b2
	 *            test Brain two
	 * @return true if the Brains are compatible
	 */
	protected static boolean areCompatible(Brain b1, Brain b2) {
		double[] disex = getDisjointExcessWeightCount(b1, b2);
		int n = Math.max(b1.dna.numGenes(), b2.dna.numGenes());
		if (n < 20) {
			n = 1;
		}
		double comindex = ((Braincraft.c1 * disex[1]) / n)
				+ ((Braincraft.c2 * disex[0]) / n) + Braincraft.c3 * disex[2];
		System.out.println(comindex);
		if (comindex >= Braincraft.compThresh) {
			return true;
		}
		return false;
	}

	
	// EVALUATION HELPER METHODS:
	private double getActivation(NNode node) {
		if (node.activated)
			return node.activation;
		for (Gene g : dna.getIncomingGenes(node.ID)) {
			NNode start = species.getNode(g.start);
			if (start.activation == null) {
				start.activation = getActivation(start);
			}
		}
		node.activated = true;
		return calculateActivation(node);
	}

	private double calculateActivation(NNode node) {
		Double sum = 0.0;
		for (Gene g : dna.getIncomingGenes(node.ID)) {
			sum += g.weight * species.getNode(g.start).activation;
		}
		return sigmoidFunction(sum);
	}

	private double sigmoidFunction(Double sum) {
		return 1 / (1 + Math.pow(Math.E, sum * Braincraft.sigmoidCoefficient));
	}

	
	// OTHER METHODS:
	/**
	 * Sets up a fully connected DNA with only inputs, outputs and random
	 * weights.
	 * 
	 * @param init
	 *            set up a new fully connected Genome or not
	 */
	private void initializeDNA() {
		dna = new DNA();

		int numInputs = species.inputs.length;
		int numOutputs = species.outputs.length;
		for (NNode node : species.inputs) {
			dna.submitNewNode(node);
		}
		for (NNode node : species.outputs) {
			dna.submitNewNode(node);
		}
		if (numInputs > numOutputs) {
			for (int i = 0; i < numInputs; i++) {
				Gene g = new Gene(species.inputs[i].ID, species.outputs[i
						% numOutputs].ID, Braincraft.randomWeight(), species);
				dna.submitNewConnection(g);
			}
		} else {
			for (int i = 0; i < numOutputs; i++) {
				Gene g = new Gene(species.inputs[i % numInputs].ID,
						species.outputs[i].ID, Braincraft.randomWeight(), species);
				dna.submitNewConnection(g);
			}
		}
	}

	// TODO: Make this check the Sanity of itself, its DNA, etc.
	protected boolean sanityCheck() {
		return false;
	}

	
	// JAVA INTERFACE HELPERS:
	public int compareTo(Brain b) {
		if (adjustedFitness == null || b.adjustedFitness == null)
			return 0;
		if (b.adjustedFitness < adjustedFitness)
			return -1;
		else if (b.adjustedFitness == adjustedFitness)
			return 0;
		else
			return 1;
	}
	
	/**
	 * Version ID for serialization
	 */
	private static final long serialVersionUID = 1L;
}
