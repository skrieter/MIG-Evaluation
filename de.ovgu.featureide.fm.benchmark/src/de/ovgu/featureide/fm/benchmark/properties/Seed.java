package de.ovgu.featureide.fm.benchmark.properties;

public class Seed extends LongProperty {

	public Seed() {
		super("seed");
	}

	@Override
	protected Long getDefaultValue() {
		return System.currentTimeMillis();
	}

}
