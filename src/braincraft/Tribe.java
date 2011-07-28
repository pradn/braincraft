package braincraft;

import java.io.Serializable;
import java.util.ArrayList;

public class Tribe implements Serializable
{
	protected ArrayList<Brain> brains;
	protected int ID;
	protected Species species;
	protected Brain representative;
	protected double fitness;
	protected int numBabies;

	protected Tribe(Species spec, Brain rep)
	{
		ID = spec.getNewTribeID(this);
		species = spec;
		brains = new ArrayList<Brain>();
		representative = rep;
	}

	protected void add(Brain b)
	{
		brains.add(b);
	}
	
	protected Brain getRandomMemberBrain()
	{
		return brains.get(Braincraft.randomInteger(brains.size()));
	}
	
	/**
	 * Version ID for serialization
	 */
	private static final long serialVersionUID = 1L;
}
