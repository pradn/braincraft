package braincraft;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Chris Donahue
 * 
 *         thinklib is a loose interpretation/implementation of Ken Stanley's
 *         NEAT. More information on NEAT can be found here:
 *         http://www.cs.ucf.edu/~kstanley/neat.html
 */
public class Braincraft {
	// NEAT PARAMETERS:
	/**
	 * Coefficient of x in sigmoid(x)
	 */
	public static double sigmoidCoefficient;
	/**
	 * Parameter of the function that determines a Brain's population
	 */
	public static double c1;
	/**
	 * Parameter of the function that determines a Brain's population
	 */
	public static double c2;
	/**
	 * Parameter of the function that determines a Brain's population
	 */
	public static double c3;
	/**
	 * Threshold value for determining a Brain's place in a population
	 */
	public static double compThresh;
	/**
	 * The default population size if not specified
	 */
	private static int defaultPopulationSize;
	/**
	 * Percentage of each tribe that does not get to reproduce at each
	 * generation
	 */
	public static double percentageOfTribeToKillBeforeReproduction;
	/**
	 * Chance that an individual weight will be mutated in a network weight
	 * mutation.
	 */
	public static double perWeightMutationRate;
	/**
	 * Chance of a neural network having its weights mutated.
	 */
	public static double weightMutationRate;
	/**
	 * Chance of a neural network having a connection mutation
	 */
	public static double linkMutationRate;
	/**
	 * Chance of a neural network having a node added
	 */
	public static double nodeMutationRate;
	/**
	 * Chance that a gene will be disabled in the offspring given that one of
	 * its parents has that gene disabled.
	 */
	public static double disabledRate;
	/**
	 * Chance that offspring will inherit a given gene from the parent with
	 * higher fitness.
	 */
	public static double inheritFromHigherFitRate;

	// FIELDS:
	private static Random rng;
	private static ArrayList<Species> ecosystem;
	private static ArrayList<String> log;
	private static ArrayList<String> errorLog;

	// CONSTRUCTORS:
	/**
	 * Constuctor for a new thinklib environment.
	 */
	public Braincraft() {
		setParams();
		rng = new Random();
		ecosystem = new ArrayList<Species>();
		log = new ArrayList<String>();
		errorLog = new ArrayList<String>();
		// set parameters. Perhaps use NEAT to set NEAT parameters?
	}

	// PUBLIC METHODS:
	/**
	 * Creates a new Species object with the specified number of inputs and
	 * outputs. Returns the object to the user.
	 * 
	 * @param numInputs
	 *            number of inputs to the neural network
	 * @param numOutputs
	 *            number of outputs to the neural network
	 * @return Species
	 */
	public Species newSpecies(int numInputs, int numOutputs) {
		return newSpecies(defaultPopulationSize, numInputs, numOutputs);
	}

	public Species newSpecies(int maxPopulation, int numInputs, int numOutputs) {
		Species ret = new Species(maxPopulation, numInputs, numOutputs,
				ecosystem.size());
		ecosystem.add(ret);
		return ret;
	}

	/**
	 * Writes the log messages to a specified file
	 * 
	 * @param file
	 *            the file to write to
	 * @return 1 if successful, -1 if unsuccessful
	 */
	public static int writeLog(String file) {
		String output = "";
		for (String s : log) {
			output += s + "\n";
		}

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(output);
			out.close();
		} catch (IOException e) {
			Braincraft.reportError("Could not write log to location " + file + ".");
			return -1;
		}
		return 1;
	}

	// LIBRARY METHODS:
	/**
	 * Bernoulli trial with percentage chance
	 * 
	 * @param chance
	 *            the chance of success for this Bernoulli trial
	 * @return whether or not the trial was a success
	 */
	protected static boolean randomChance(double chance) {
		if (Math.random() < chance)
			return true;
		return false;
	}

	/**
	 * Get a random weight value
	 * 
	 * @return double a weight value between -1 and 1
	 */
	protected static double randomWeight() {
		int sign = (int) (Math.random() * 2);
		double value = Math.random();
		if (sign == 0) {
			return value * -1;
		}
		return value;
	}

	/**
	 * Gets a random integer between 0 (inclusive) and the specified range
	 * (exclusive)
	 * 
	 * @param range
	 *            get a random number greater than or equal to 0 but less than
	 *            range
	 * @return a random integer
	 */
	protected static int randomInteger(int range) {
		return rng.nextInt(range);
	}

	/**
	 * Adds a string to the library log
	 * 
	 * @param message
	 *            message to add to the log
	 */
	protected static void report(String message) {
		log.add(message);
	}

	/**
	 * Adds a string to the library error log
	 * 
	 * @param message error to report
	 */
	protected static void reportError(String message) {
		errorLog.add(message);
	}

	// PRIVATE METHODS:
	/**
	 * Sets default values for parameters.
	 */
	private void setParams() {
		sigmoidCoefficient = -4.9;
		c1 = 1.0;
		c2 = 1.0;
		c3 = 0.4;
		compThresh = 3.0;
		defaultPopulationSize = 150;
		percentageOfTribeToKillBeforeReproduction = 0.5;
		perWeightMutationRate = 0.1;
		weightMutationRate = 0.8;
		linkMutationRate = 0.3;
		nodeMutationRate = 0.18;
		disabledRate = 0.75;
		inheritFromHigherFitRate = 0.8;
	}
}
