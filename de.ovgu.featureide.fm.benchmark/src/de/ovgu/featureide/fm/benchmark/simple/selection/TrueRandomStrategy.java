package de.ovgu.featureide.fm.benchmark.simple.selection;

public class TrueRandomStrategy implements ISelectionStrategy {

	private final Long seed;

	public TrueRandomStrategy(Long seed) {
		this.seed = seed;
	}

	@Override
	public byte getNextSelection() {
		return 1;
	}

	@Override
	public String toString() {
		return "True_" + seed;
	}

}