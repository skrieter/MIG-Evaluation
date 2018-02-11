package de.ovgu.featureide.fm.benchmark.properties;

public class Timeout extends LongProperty {

	public Timeout() {
		super("timeout");
	}

	@Override
	protected Long getDefaultValue() {
		return 0L;
	}

}
