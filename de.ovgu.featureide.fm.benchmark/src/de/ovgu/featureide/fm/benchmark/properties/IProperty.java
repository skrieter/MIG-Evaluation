package de.ovgu.featureide.fm.benchmark.properties;

public interface IProperty {

	String getKey();
	
	Object getValue();
	
	boolean setValue(String valueString);

}
