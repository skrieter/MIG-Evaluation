package de.ovgu.featureide.fm.benchmark.simple.iterator;

import java.util.Iterator;
import java.util.LinkedList;

import de.ovgu.featureide.fm.core.base.IFeature;

public abstract class AIterator implements Iterator<IFeature> {

	protected final LinkedList<IFeature> list = new LinkedList<>();

	public AIterator(IFeature root) {
		list.add(root);
	}

	@Override
	public boolean hasNext() {
		return !list.isEmpty();
	}

}