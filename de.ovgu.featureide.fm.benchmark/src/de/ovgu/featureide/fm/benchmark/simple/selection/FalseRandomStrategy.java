package de.ovgu.featureide.fm.benchmark.simple.selection;

public class FalseRandomStrategy implements ISelectionStrategy {

	private final Long seed;

	public FalseRandomStrategy(Long seed) {
		this.seed = seed;
	}

	@Override
	public byte getNextSelection() {
		return 1;
	}

	@Override
	public String toString() {
		return "False_" + seed;
	}

}