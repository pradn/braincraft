package braincraft;

public class XOR {

	Braincraft lib = new Braincraft();
	Species spec;
	Brain champ;

	public void evaluate(Brain b) {
		boolean[] xarr = { false, true };
		boolean[] yarr = { false, true };
		int fitness = 0;
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				double[] arr = new double[2];
				arr[0] = x;
				arr[1] = y;
				double[] output = null;
				try {
					output = b.evaluate(arr);
				} catch (NetworkInputException e) {
					System.out.println("oops");
				}
				boolean result = true;
				if (output[0] < 0.5) {
					result = false;
				}
				if (result == (xarr[x] ^ yarr[y])) {
					fitness++;
				}
			}
		}
		System.out.println(fitness);
		b.reportFitness(fitness);
	}

	public XOR() {
		createSpecies(10, 2, 1);
		/*
		Brain created = Brain.loadObject("testbrain.ser");
		double [] arr = {0, 1};
		try
		{
			System.out.println(created.evaluate(arr)[0]);
		}
		catch (NetworkInputException e)
		{
			System.out.println("Oops");
		}
		System.exit(-1);
		*/
		think(4);
	}

	public static void main(String[] args) {
		new XOR();
	}

	void createSpecies(int inputs, int outputs) {
		spec = lib.newSpecies(inputs, outputs);
	}

	void createSpecies(int popSize, int inputs, int outputs) {
		spec = lib.newSpecies(popSize, inputs, outputs);
	}

	void think(double threshold) {
		Brain b;
		do {
			b = spec.getBrain();
			evaluate(b);
		} while (b.getFitness() <= threshold);
		champ = b;
	}
}
