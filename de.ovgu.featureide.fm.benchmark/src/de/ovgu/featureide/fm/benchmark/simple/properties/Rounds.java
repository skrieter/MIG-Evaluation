package de.ovgu.featureide.fm.benchmark.simple.properties;

import de.ovgu.featureide.fm.benchmark.properties.IntProperty;

public class Rounds extends IntProperty {

	public Rounds(String name) {
		super(name);
	}

	@Override
	protected Integer getDefaultValue() {
		return 0;
	}

}
