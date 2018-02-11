package de.ovgu.featureide.fm.benchmark.simple.selection;

public class FalseStrategy implements ISelectionStrategy {

	@Override
	public byte getNextSelection() {
		return -1;
	}

	@Override
	public String toString() {
		return "False";
	}

}