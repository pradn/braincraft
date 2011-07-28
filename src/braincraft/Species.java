package braincraft;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

/**
 * @author Chris
 * 
 *         A Species object encapsulates all of the neural networks that are
 *         being evolved for a specific task. All members of the same Species
 *         have the same number of inputs and outputs and can breed within
 *         themselves.
 */
public class Species implements Serializable {
	// FIELDS:
	/**
	 * ID of this species
	 */
	protected int ID;
	/**
	 * Number of members in the Species
	 */
	protected int populationSize;
	/**
	 * The number of completed generations of this Species
	 */
	protected int numGenerations;
	/**
	 * Keeps track of the Tribes in this Species
	 */
	private ArrayList<Tribe> tribes;
	/**
	 * Keeps track of the Brains in this Species
	 */
	private ArrayList<Brain> brains;
	/**
	 * Keeps track of the Genes in this Species
	 */
	private ArrayList<Gene> innovations;
	/**
	 * Keeps track of the Nodes in this species
	 */
	private ArrayList<NNode> nodes;
	protected NNode[] inputs;
	protected NNode[] outputs;
	/**
	 * A stack of unevaluated brains to return to the user
	 */
	private Stack<Brain> unevaluated;

	// CONSTRUCTORS:
	/**
	 * Constructor for a new Species object.
	 * 
	 * @param numInputs
	 *            the number of inputs for a species
	 * @param numOutputs
	 *            the number of outputs for a species
	 * @param speciesID
	 *            the ID of
	 */
	protected Species(int popSize, int numInputs, int numOutputs, int id) {
		inputs = new NNode[numInputs];
		outputs = new NNode[numOutputs];

		tribes = new ArrayList<Tribe>();
		innovations = new ArrayList<Gene>();
		nodes = new ArrayList<NNode>();
		brains = new ArrayList<Brain>();
		unevaluated = new Stack<Brain>();

		for (int i = 0; i < numInputs; i++) {
			inputs[i] = new NNode(NNode.INPUT, this);
		}
		for (int j = 0; j < numOutputs; j++) {
			outputs[j] = new NNode(NNode.OUTPUT, this);
		}

		for (int i = 0; i < popSize; i++) {
			unevaluated.push(new Brain(this));
		}

		ID = id;
		populationSize = popSize;
		numGenerations = 0;

		Braincraft.report("Species " + ID + " has been created with "
				+ numInputs + " inputs, " + numOutputs
				+ " outputs, and a population size of " + popSize + ".");
	}

	// PUBLIC METHODS:
	/**
	 * User will call this method on a Species to retrieve a new Brain for
	 * evaluation.
	 * 
	 * @return Brain
	 */
	public Brain getBrain() {
		if (unevaluated.size() > 0) {
			return unevaluated.pop();
		} else
			newGeneration();
		return unevaluated.pop();
	}

	/**
	 * Get the ID for this Species object.
	 * 
	 * @return int
	 */
	public int getID() {
		return ID;
	}

	// LIBRARY METHODS:
	/**
	 * Called by Gene's constructor.
	 * 
	 * @param g
	 *            gene to get innovation for
	 * @return innovation number
	 */
	protected int getInnovation(Gene g) {
		for (Gene inno : innovations) {
			if (g.equals(inno)) {
				return inno.innovation;
			}
		}
		int ret = innovations.size();
		innovations.add(g);
		Braincraft.report("SPECIES " + ID + ": Innovation " + ret + " was just made!");
		return ret;
	}

	/**
	 * Called by NNode's constructor.
	 * 
	 * @param n
	 *            new node
	 * @return int
	 */
	protected int getNewNodeID(NNode n) {
		int ret = nodes.size();
		nodes.add(n);
		Braincraft.report("SPECIES " + ID + ": Node " + ret + " was just made!");
		return ret;
	}

	/**
	 * Called by Brain's constructor.
	 * 
	 * @param b
	 *            new brain
	 * @return int
	 */
	protected int getNewBrainID(Brain b) {
		int ret = brains.size();
		brains.add(b);
		Braincraft.report("SPECIES " + ID + ": Brain " + ret + " was just made!");
		return ret;
	}

	/**
	 * Called by Tribe's constructor.
	 * 
	 * @param t
	 *            new tribe
	 * @return int
	 */
	protected int getNewTribeID(Tribe t) {
		int ret = tribes.size();
		tribes.add(t);
		Braincraft.report("SPECIES " + ID + ": Tribe " + ret + " was just made!");
		return ret;
	}

	/**
	 * Get an NNode from its ID
	 * 
	 * @param id
	 * @return NNode with given id
	 */
	protected NNode getNode(int id) {
		return nodes.get(id);
	}

	/**
	 * Brain object tells its Species that it is done and ready to reproduce or
	 * be killed.
	 * 
	 * @param b
	 *            Brain that was evaluated
	 */
	protected void evaluated(Brain b) {
		if (!b.alive) {
			unevaluated.removeElement(b);
		}
	}

	/**
	 * Takes a Brain object and finds and returns a suitable Tribe. Same
	 * algorithm as the Sorting Hat from Harry Potter.
	 * 
	 * @param b
	 *            the Brain to find a Tribe for
	 * @return Tribe the tribe that this brain belongs to
	 */
	protected Tribe getTribe(Brain b) {
		Collections.shuffle(tribes);
		for (Tribe tri : tribes) {
			if (Brain.areCompatible(b, tri.representative)) {
				tri.add(b);
				return tri;
			}
		}
		Tribe ret = new Tribe(this, b);
		ret.add(b);
		return ret;
	}

	// NEAT EPOCH AND HELPER METHODS:
	/**
	 * This method controls major elements of the evolutionary process including
	 * reproduction, mutation and tribe management. It can almost be considered
	 * the "main" method of the library.
	 */
	private void newGeneration() {
		numGenerations++;
		Braincraft.report("Species " + ID + " has grown for " + numGenerations + " generations.");
		double totalFitness = 0.0;
		Tribe champTribe = tribes.get(0);
		// Tribe loop to determine adjusted fitnesses
		for (Tribe t : tribes) {
			double tribeFitness = 0.0;
			int tribeSize = t.brains.size();
			if (tribeSize == 0)
				continue;

			// Calculate adjusted fitness for each Brain
			for (int i = 0; i < tribeSize; i++) {
				Brain b = t.brains.get(i);
				int denominator = 0;
				for (int j = 0; j < tribeSize; j++) {
					if (i == j)
						continue;
					if (!Brain.areCompatible(b, t.brains.get(j)))
						denominator++;
				}
				b.adjustedFitness = b.fitness / denominator;
				tribeFitness += b.adjustedFitness;
				totalFitness += b.adjustedFitness;
			}
			// Sort by adjusted fitness
			Collections.sort(t.brains);
			// Assign champ by adj fitness
			t.representative = t.brains.get(tribeSize - 1);
			t.fitness = tribeFitness;
			// Assign superchamp tribe
			if (champTribe.representative.fitness < t.representative.fitness) {
				champTribe = t;
			}
		}

		// Determine reproduction rights, remove poor-performing members
		int numBabiesDealt = 0;
		for (Tribe t : tribes) {
			// Number of Babies a tribe gets is equal to its share of the total
			// fitness multiplied by the population size.
			int designatedBabies = (int) ((t.fitness / totalFitness) * populationSize);
			numBabiesDealt += designatedBabies;
			t.numBabies = designatedBabies;
			// Remove part of the tribe
			int numUnfit = (int) (Braincraft.percentageOfTribeToKillBeforeReproduction * t.brains
					.size());
			for (int i = t.brains.size() - numUnfit; i < t.brains.size(); i++) {
				t.brains.remove(i);
			}
		}
		// Assign champTribe the rounded-off babies
		if (populationSize > numBabiesDealt) {
			champTribe.numBabies += (populationSize - numBabiesDealt);
		}

		// Reproduce designated number of babies
		for (Tribe t : tribes) {
			ArrayList<Brain> newBrains = new ArrayList<Brain>();
			// Create offspring
			for (int i = 0; i < t.numBabies; i++) {
				Brain mother = t.getRandomMemberBrain();
				Brain father = t.getRandomMemberBrain();
				Brain child = Brain.crossBrains(mother, father);
				newBrains.add(child);
			}
			// Clear up some Tribe fields
			t.brains.clear();
			t.brains.addAll(newBrains);
			t.fitness = 0;
			t.numBabies = 0;
			unevaluated.addAll(newBrains);
		}

		// Perform mutations
		for (Brain b : unevaluated) {
			if (Braincraft.randomChance(Braincraft.weightMutationRate))
				b.mutateWeights();
			if (Braincraft.randomChance(Braincraft.linkMutationRate))
				b.mutateAddLink();
			if (Braincraft.randomChance(Braincraft.nodeMutationRate))
				b.mutateAddNode();
		}
	}

	/**
	 * Version ID for serialization
	 */
	private static final long serialVersionUID = 1L;
}
