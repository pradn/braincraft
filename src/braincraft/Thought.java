package braincraft;

public interface Thought
{
	Braincraft lib = new Braincraft();

	// Create a new species
	void createSpecies(int inputs, int outputs);

	// Evaluate a single given brain. User will pass inputs to the brain
	// and then call reportFitness to complete their evaluation
	void evaluate(Brain b);

	// Main think loop. User will createSpecies, set up a while loop
	// to call evaluate until a certain condition has been met.
	void think();
}
