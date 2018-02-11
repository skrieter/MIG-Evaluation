package de.ovgu.featureide.fm.benchmark.properties;

public class BoolProperty extends AProperty<Boolean> {

	public BoolProperty(String name) {
		super(name);
	}

	@Override
	protected Boolean getDefaultValue() {
		return Boolean.FALSE;
	}

	@Override
	protected Boolean cast(String valueString) throws Exception {
		return Boolean.parseBoolean(valueString);
	}

}
