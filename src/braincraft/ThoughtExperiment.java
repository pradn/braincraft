package braincraft;

public abstract class ThoughtExperiment
{
	Braincraft lib = new Braincraft();
	Species spec;
	Brain champ;
	
	void createSpecies(int inputs, int outputs)
	{
		spec = lib.newSpecies(inputs, outputs);
	}
	
	void createSpecies(int popSize, int inputs, int outputs)
	{
		spec = lib.newSpecies(popSize, inputs, outputs);
	}
	
	abstract void evaluate(Brain b);
	
	void think(double threshold)
	{
		Brain b;
		do
		{
			b = spec.getBrain();
			evaluate(b);
		}
		while (b.getFitness() <= threshold);
		champ = b;
	}
}