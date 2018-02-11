package de.ovgu.featureide.fm.benchmark.simple.iterator;

import java.util.Collections;
import java.util.List;

import de.ovgu.featureide.fm.core.base.FeatureUtils;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.functional.Functional;

public class LevelOrderIterator extends AIterator {

	private final boolean reverse;

	public LevelOrderIterator(IFeature root, boolean reverse) {
		super(root);
		this.reverse = reverse;
	}

	@Override
	public IFeature next() {
		final IFeature next = list.removeFirst();
		final List<IFeature> children = Functional.toList(FeatureUtils.getChildren(next));
		if (reverse) {
			Collections.reverse(children);
		}
		list.addAll(children);
		return next;
	}

	@Override
	public void remove() {
	}

}