package de.ovgu.featureide.fm.benchmark.properties;

public class LongProperty extends AProperty<Long> {

	public LongProperty(String name) {
		super(name);
	}

	@Override
	protected Long getDefaultValue() {
		return 0L;
	}

	@Override
	protected Long cast(String valueString) throws Exception {
		return Long.parseLong(valueString);
	}

}
