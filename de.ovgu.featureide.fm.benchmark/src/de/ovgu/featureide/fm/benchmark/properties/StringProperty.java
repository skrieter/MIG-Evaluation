package de.ovgu.featureide.fm.benchmark.properties;

public class StringProperty extends AProperty<String> {

	public StringProperty(String name) {
		super(name);
	}

	@Override
	protected String getDefaultValue() {
		return "";
	}

	@Override
	protected String cast(String valueString) throws Exception {
		return valueString;
	}

}
