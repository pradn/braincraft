package braincraft;

import java.io.Serializable;

public class NNode implements Serializable
{
	protected static final int INPUT = 1;
	protected static final int OUTPUT = 2;
	protected static final int HIDDEN = 3;
	protected int ID;
	protected int type;
	protected Double activation;
	protected boolean activated;

	protected NNode(int typeNum, Species spec)
	{
		type = typeNum;
		ID = spec.getNewNodeID(this);
		activated = false;
		if (typeNum == NNode.INPUT)
		{
			activated = true;
		}
	}

	protected boolean equals(NNode n)
	{
		if (n.ID == this.ID)
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
