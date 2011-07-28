package braincraft;

import java.io.Serializable;

public class Gene implements Serializable
{
	protected int start;
	protected int end;
	protected double weight;
	protected int innovation;
	protected boolean enabled;

	protected Gene(int startNode, int endNode, double weightValue, Species spec)
	{
		start = startNode;
		end = endNode;
		weight = weightValue;
		enabled = true;
		innovation = spec.getInnovation(this);
	}

	protected Gene(Gene g)
	{
		start = g.start;
		end = g.end;
		weight = g.weight;
		innovation = g.innovation;
		enabled = g.enabled;
	}

	protected Gene()
	{
		enabled = true;
	}

	protected boolean equals(Gene g)
	{
		if (g.start == start && g.end == end)
		{
			return true;
		}
		return false;
	}

	/**
	 * Version ID for serialization
	 */
	private static final long serialVersionUID = 1L;
}
