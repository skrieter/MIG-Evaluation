package de.ovgu.featureide.fm.benchmark.simple.selection;

import java.util.Random;

public class RandomStrategy implements ISelectionStrategy {

	private final Random rand;
	private final Long seed;

	public RandomStrategy(Long seed) {
		this.rand = new Random(seed);
		this.seed = seed;
	}

	@Override
	public byte getNextSelection() {
		return (byte) (rand.nextInt(100) < 50 ? -1 : 1);
	}

	@Override
	public String toString() {
		return "Random_" + seed;
	}

}