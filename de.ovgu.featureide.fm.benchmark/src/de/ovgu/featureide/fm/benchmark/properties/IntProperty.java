package de.ovgu.featureide.fm.benchmark.properties;

public class IntProperty extends AProperty<Integer> {

	public IntProperty(String name) {
		super(name);
	}

	@Override
	protected Integer getDefaultValue() {
		return 0;
	}

	@Override
	protected Integer cast(String valueString) throws Exception {
		return Integer.parseInt(valueString);
	}

}
