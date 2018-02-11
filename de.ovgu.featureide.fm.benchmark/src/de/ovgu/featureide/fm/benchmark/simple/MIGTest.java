package de.ovgu.featureide.fm.benchmark.simple;
/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2015  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.prop4j.Node;
import org.prop4j.analyses.AConditionallyCoreDeadAnalysis;
import org.prop4j.analyses.AdjList;
import org.prop4j.analyses.AdjMatrix;
import org.prop4j.analyses.ConditionallyCoreDeadAnalysisFGImproved2;
import org.prop4j.analyses.ConditionallyCoreDeadAnalysisFGImproved4;
import org.prop4j.analyses.ConditionallyCoreDeadAnalysisFGNaive2;
import org.prop4j.analyses.ConditionallyCoreDeadAnalysisFGNaive4;
import org.prop4j.analyses.ConditionallyCoreDeadAnalysisSatImproved;
import org.prop4j.analyses.ConditionallyCoreDeadAnalysisSatNaive;
import org.prop4j.analyses.ConditionallyCoreDeadAnalysisSplar;
import org.prop4j.solver.BasicSolver;
import org.prop4j.solver.SatInstance;
import org.sat4j.specs.ContradictionException;

import de.ovgu.featureide.fm.benchmark.ABenchmark;
import de.ovgu.featureide.fm.benchmark.CSVWriter;
import de.ovgu.featureide.fm.benchmark.properties.BoolProperty;
import de.ovgu.featureide.fm.benchmark.properties.IntProperty;
import de.ovgu.featureide.fm.benchmark.simple.iterator.AIterator;
import de.ovgu.featureide.fm.benchmark.simple.iterator.LevelOrderIterator;
import de.ovgu.featureide.fm.benchmark.simple.properties.Rounds;
import de.ovgu.featureide.fm.benchmark.simple.selection.FalseRandomStrategy;
import de.ovgu.featureide.fm.benchmark.simple.selection.FalseStrategy;
import de.ovgu.featureide.fm.benchmark.simple.selection.ISelectionStrategy;
import de.ovgu.featureide.fm.benchmark.simple.selection.RandomStrategy;
import de.ovgu.featureide.fm.benchmark.simple.selection.TrueRandomStrategy;
import de.ovgu.featureide.fm.benchmark.simple.selection.TrueStrategy;
import de.ovgu.featureide.fm.core.base.FeatureUtils;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.conf.IFeatureGraph2;
import de.ovgu.featureide.fm.core.editing.AdvancedNodeCreator;
import de.ovgu.featureide.fm.core.editing.AdvancedNodeCreator.CNFType;
import de.ovgu.featureide.fm.core.job.LongRunningWrapper;

/**
 * @author Sebastian Krieter
 */
public class MIGTest extends ABenchmark {

	private static final int FLAG_FG_IMPROVED = 0b0000_0001, FLAG_SAT_IMPROVED = 0b0000_0010, FLAG_SPLAR = 0b0000_0100,
			FLAG_FG_NAIVE = 0b0000_1000, FLAG_SAT_NAIVE = 0b0001_0000, FLAG_CFG_IMPROVED = 0b0010_0000,
			FLAG_CFG_NAIVE = 0b0100_0000;

	private final List<AConditionallyCoreDeadAnalysis> analysesList = new ArrayList<>();

	private static final BoolProperty useFalse = new BoolProperty("False");
	private static final BoolProperty useTrue = new BoolProperty("True");
	private static final IntProperty algorithms = new IntProperty("algorithms");
	private static final Rounds initRounds = new Rounds("init_rounds");
	private static final Rounds randomRounds = new Rounds("random_rounds");
	private static final Rounds trueRounds = new Rounds("true_rounds");
	private static final Rounds falseRounds = new Rounds("false_rounds");

	private static final int dummyRounds = 1;

	private int[] selectionOrder;
	private int[] coreDead;

	private SatInstance satInstance;
	private IFeatureModel fm;
	private String modelName;

	private CSVWriter onlineTimeCSVWriter;
	private CSVWriter offlineTimeCSVWriter;
	private CSVWriter statisticCSVWriter;

	public static void main(String[] args) {
		try {
			new MIGTest().run();
		} catch (Throwable e) {
			printErr(e.getMessage());
			e.printStackTrace();
		}
	}

	public void run() {
		final long currentSeed = getNextSeed();

		offlineTimeCSVWriter = new CSVWriter();
		offlineTimeCSVWriter.setOutputPath(rootOutPath);
		offlineTimeCSVWriter.setFileName("initTimes.csv");

		statisticCSVWriter = new CSVWriter();
		statisticCSVWriter.setOutputPath(rootOutPath);
		statisticCSVWriter.setFileName("fmStatistic.csv");

		for (int i = 0; i < modelNames.size(); i++) {
			evaluateFeatureModel(currentSeed, i);
		}
		printOut("--- Done! ---");
	}

	private void evaluateFeatureModel(final long currentSeed, int i) {
		modelName = modelNames.get(i);
		printOut(modelName);

		long time = System.nanoTime();
		fm = init(modelName);
		modelName = modelName.replace(",", "");
		printOut("Load Feature Model:          " + ((System.nanoTime() - time) / 1_000_000) / 1000.0, 1);

		time = System.nanoTime();
		final List<String> orderList = getOrderList(fm);
		initSatInstance(fm, orderList);
		printOut("Set Up SatInstance           " + ((System.nanoTime() - time) / 1_000_000) / 1000.0, 1);
		
		time = System.nanoTime();
		final IFeatureGraph2 fg_strong_complete = AdjList.build(AdjMatrix.build(satInstance, true));
		printOut("Init StrongComplete:         " + ((System.nanoTime() - time) / 1_000_000) / 1000.0, 1);

		time = System.nanoTime();
		final IFeatureGraph2 fg_non_complete = AdjList.build(AdjMatrix.build(satInstance, false));
		printOut("Init NonComplete:            " + ((System.nanoTime() - time) / 1_000_000) / 1000.0, 1);

		time = System.nanoTime();
		coreDead = LongRunningWrapper.runMethod(new ConditionallyCoreDeadAnalysisSatImproved(satInstance));
		printOut("Computed Core/Dead Features: " + ((System.nanoTime() - time) / 1_000_000) / 1000.0, 1);

		measureFGStatistic(fg_strong_complete);
		measureOfflineTime();
		measureOnlineTime(currentSeed, orderList, fg_strong_complete, fg_non_complete);
	}

	private void measureFGStatistic(IFeatureGraph2 fg_strong_complete) {
		final long time = System.nanoTime();

		statisticCSVWriter.createNewLine();
		statisticCSVWriter.addValue(modelName);

		statisticCSVWriter.addValue(fm.getNumberOfFeatures());
		statisticCSVWriter.addValue(fm.getConstraintCount());
		statisticCSVWriter.addValue(0); // fg.getSize());

		int countWeak = 0;
		int countStrong = 0;
		int countNone = 0;
		for (int j = 0; j < fm.getNumberOfFeatures(); j++) {
			for (int k = 0; k < fm.getNumberOfFeatures(); k++) {
				final byte value = fg_strong_complete.getValue(j, k, false);
				switch (value) {
				case AdjMatrix.VALUE_0:
					countStrong++;
					countNone++;
					break;
				case AdjMatrix.VALUE_1:
					countStrong++;
					countNone++;
					break;
				case AdjMatrix.VALUE_0Q:
					countWeak++;
					countNone++;
					break;
				case AdjMatrix.VALUE_1Q:
					countWeak++;
					countNone++;
					break;
				case AdjMatrix.VALUE_10Q:
					countWeak += 2;
					break;
				case AdjMatrix.VALUE_NONE:
					countNone += 2;
					break;
				}
			}
		}
		statisticCSVWriter.addValue(countNone);
		statisticCSVWriter.addValue(countStrong);
		statisticCSVWriter.addValue(countWeak);

		statisticCSVWriter.flush();

		printOut("Measuring Statistics: " + ((System.nanoTime() - time) / 1_000_000) / 1000.0, 1);
	}

	private void measureOfflineTime() {
		printOut("Measuring Init Times:", 1);
		Integer rounds = initRounds.getValue();

		long time = System.nanoTime();
		for (int j = -dummyRounds; j < rounds; j++) {
			offlineTimeCSVWriter.setDummy(j < 0);
			offlineTimeCSVWriter.createNewLine();
			offlineTimeCSVWriter.addValue(modelName);
			offlineTimeCSVWriter.addValue("FGStrongComplete");
			offlineTimeCSVWriter.addValue(j);
			final long localTime = System.nanoTime();
			AdjList.build(AdjMatrix.build(satInstance, true));
			offlineTimeCSVWriter.addValue(System.nanoTime() - localTime);
			offlineTimeCSVWriter.flush();
		}
		printOut("FGStrongComplete: " + ((System.nanoTime() - time) / 1_000_000) / 1000.0, 2);

		time = System.nanoTime();
		for (int j = -dummyRounds; j < rounds; j++) {
			offlineTimeCSVWriter.setDummy(j < 0);
			offlineTimeCSVWriter.createNewLine();
			offlineTimeCSVWriter.addValue(modelName);
			offlineTimeCSVWriter.addValue("FGNonComplete");
			offlineTimeCSVWriter.addValue(j);

			final long localTime = System.nanoTime();
			AdjList.build(AdjMatrix.build(satInstance, false));
			offlineTimeCSVWriter.addValue(System.nanoTime() - localTime);
			offlineTimeCSVWriter.flush();
		}
		printOut("FGNonComplete:    " + ((System.nanoTime() - time) / 1_000_000) / 1000.0, 2);

		time = System.nanoTime();
		for (int j = -dummyRounds; j < rounds; j++) {
			offlineTimeCSVWriter.setDummy(j < 0);
			offlineTimeCSVWriter.createNewLine();
			offlineTimeCSVWriter.addValue(modelName);
			offlineTimeCSVWriter.addValue("NoFG");
			offlineTimeCSVWriter.addValue(j);
			final long localTime = System.nanoTime();
			LongRunningWrapper.runMethod(new ConditionallyCoreDeadAnalysisSatImproved(satInstance));
			getSolver();
			offlineTimeCSVWriter.addValue(System.nanoTime() - localTime);
			offlineTimeCSVWriter.flush();
		}
		printOut("NoFG:             " + ((System.nanoTime() - time) / 1_000_000) / 1000.0, 2);
	}

	private void measureOnlineTime(final long currentSeed, List<String> orderList, IFeatureGraph2 fg_strong_complete, IFeatureGraph2 fg_non_complete) {
		final Path outputPath = rootOutPath.resolve(modelName);
		try {
			Files.createDirectories(outputPath);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		onlineTimeCSVWriter = new CSVWriter();
		onlineTimeCSVWriter.setOutputPath(outputPath);
		onlineTimeCSVWriter.setFileName("times.csv");

		initAnalysesList(fg_strong_complete, fg_non_complete);

		final Random seedGen = new Random(currentSeed);

		// Deselect all features in default order
		if (useFalse.getValue()) {
			for (int j = -dummyRounds; j < 1; j++) {
				onlineTimeCSVWriter.setDummy(j < 0);
				setSelectionOrder(orderList);
				run(new FalseStrategy(), 1);
			}
		}

		// Randomly select or deselect features in random order
		Integer rounds = randomRounds.getValue();
		if (rounds > 0) {
			for (int j = -dummyRounds; j < rounds; j++) {
				onlineTimeCSVWriter.setDummy(j < 0);
				final long seed = seedGen.nextLong();
				setSelectionOrderRandom(orderList, seed);
				run(new RandomStrategy(seed), rounds - j);
			}
		}

		// Select features in random order
		rounds = trueRounds.getValue();
		if (rounds > 0) {
			for (int j = -dummyRounds; j < rounds; j++) {
				onlineTimeCSVWriter.setDummy(j < 0);
				final long seed = seedGen.nextLong();
				setSelectionOrderRandom(orderList, seed);
				run(new TrueRandomStrategy(seed), rounds - j);
			}
		}

		// Deselect features in random order
		rounds = falseRounds.getValue();
		if (rounds > 0) {
			for (int j = -dummyRounds; j < rounds; j++) {
				onlineTimeCSVWriter.setDummy(j < 0);
				final long seed = seedGen.nextLong();
				setSelectionOrderRandom(orderList, seed);
				run(new FalseRandomStrategy(seed), rounds - j);
			}
		}

		// Select features in default order
		if (useTrue.getValue()) {
			for (int j = -dummyRounds; j < 1; j++) {
				onlineTimeCSVWriter.setDummy(j < 0);
				setSelectionOrder(orderList);
				run(new TrueStrategy(), 1);
			}
		}
	}

	private void initAnalysesList(final IFeatureGraph2 fg_strong_complete, final IFeatureGraph2 fg_non_complete) {
		analysesList.clear();
		final Integer algoFlags = algorithms.getValue();
		if ((algoFlags & FLAG_CFG_IMPROVED) == FLAG_CFG_IMPROVED) {
			analysesList.add(new ConditionallyCoreDeadAnalysisFGImproved4(getSolver(), fg_strong_complete));
		}
		if ((algoFlags & FLAG_FG_IMPROVED) == FLAG_FG_IMPROVED) {
			analysesList.add(new ConditionallyCoreDeadAnalysisFGImproved2(getSolver(), fg_non_complete));
		}
		if ((algoFlags & FLAG_SAT_IMPROVED) == FLAG_SAT_IMPROVED) {
			analysesList.add(new ConditionallyCoreDeadAnalysisSatImproved(getSolver()));
		}
		if ((algoFlags & FLAG_CFG_NAIVE) == FLAG_CFG_NAIVE) {
			analysesList.add(new ConditionallyCoreDeadAnalysisFGNaive4(getSolver(), fg_strong_complete));
		}
		if ((algoFlags & FLAG_FG_NAIVE) == FLAG_FG_NAIVE) {
			analysesList.add(new ConditionallyCoreDeadAnalysisFGNaive2(getSolver(), fg_non_complete));
		}
		if ((algoFlags & FLAG_SPLAR) == FLAG_SPLAR) {
			analysesList.add(new ConditionallyCoreDeadAnalysisSplar(getSolver()));
		}
		if ((algoFlags & FLAG_SAT_NAIVE) == FLAG_SAT_NAIVE) {
			analysesList.add(new ConditionallyCoreDeadAnalysisSatNaive(getSolver()));
		}
	}

	private void initSatInstance(final IFeatureModel fm, final List<String> orderList) {
		List<String> satOrderList = new ArrayList<>(orderList);
		Collections.reverse(satOrderList);

		final AdvancedNodeCreator nc = new AdvancedNodeCreator(fm);
		nc.setCnfType(CNFType.Regular);
		nc.setIncludeBooleanValues(false);

		final Node cnf = nc.createNodes();
		satInstance = new SatInstance(cnf, satOrderList);
	}

	private List<String> getOrderList(final IFeatureModel fm) {
		final IFeature root = FeatureUtils.getRoot(fm);
		AIterator it = new LevelOrderIterator(root, false);
		List<String> orderList = new ArrayList<>();
		while (it.hasNext()) {
			final IFeature next = it.next();
			orderList.add(next.getName());
		}
		return orderList;
	}

	private BasicSolver getSolver() {
		try {
			return new BasicSolver(satInstance);
		} catch (ContradictionException e) {
			throw new RuntimeException();
		}
	}

	private void setSelectionOrder(List<String> orderList) {
		selectionOrder = new int[orderList.size()];
		final List<String> selectionOrderList = new ArrayList<>(orderList);
		int j = 0;
		for (String featureName : selectionOrderList) {
			selectionOrder[j++] = satInstance.getVariable(featureName);
		}
	}

	private void setSelectionOrderRandom(List<String> orderList, long seed) {
		selectionOrder = new int[orderList.size()];
		final List<String> selectionOrderList = new ArrayList<>(orderList);
		Collections.shuffle(selectionOrderList, new Random(seed));
		int j = 0;
		for (String featureName : selectionOrderList) {
			selectionOrder[j++] = satInstance.getVariable(featureName);
		}
	}

	private void run(ISelectionStrategy strategy, int strategyCount) {
		final byte[] x = new byte[satInstance.getNumberOfVariables()];
		for (int j = 0; j < x.length; j++) {
			x[j] = strategy.getNextSelection();
		}
		strategyCount--;

		printOut(strategyCount + " " + strategy.toString(), 1);

		int[] result1 = null;
		int[] result2 = null;

		for (AConditionallyCoreDeadAnalysis analysis : analysesList) {

			byte[] y = Arrays.copyOf(x, x.length);
			int[] result = coreDead;
			final long globalTime = System.nanoTime();

			int i = 0;
			while (true) {
				onlineTimeCSVWriter.createNewLine();
				onlineTimeCSVWriter.addValue(modelName);
				onlineTimeCSVWriter.addValue(strategy);
				onlineTimeCSVWriter.addValue(analysis);
				onlineTimeCSVWriter.addValue(++i);

				final int[] newArray = new int[result.length + 1];
				System.arraycopy(result, 0, newArray, 1, result.length);

				for (int j = 0; j < result.length; j++) {
					y[Math.abs(result[j]) - 1] = 0;
				}

				int j = 0;
				for (; j < y.length; j++) {
					final int nextVariable = selectionOrder[j];
					int var = y[nextVariable - 1];
					if (var != 0) {
						final int next = var * nextVariable;
						newArray[0] = next;
						onlineTimeCSVWriter.addValue(next);
						break;
					}
				}
				if (newArray[0] == 0) {
					break;
				}

				analysis.setFixedFeatures(newArray, 1);
				final long localTime = System.nanoTime();
				result = LongRunningWrapper.runMethod(analysis);
				onlineTimeCSVWriter.addValue(System.nanoTime() - localTime);
				onlineTimeCSVWriter.addValue(analysis.satCount);
				onlineTimeCSVWriter.addValue((y.length - j));

				onlineTimeCSVWriter.flush();
			}
			printOut(analysis + ": " + ((System.nanoTime() - globalTime) / 1_000_000) / 1000.0, 2);

			if (result1 == null) {
				result1 = result;
			} else {
				result2 = result1;
				result1 = result;
			}
			if (result2 != null) {
				test(result1, result2);
			}
		}
	}

	private void test(int[] result, int[] result2) {
		Arrays.sort(result);
		Arrays.sort(result2);

		boolean equals = Arrays.equals(result, result2);
		if (!equals) {
			printOut("Compare Failed!");
			printErr("Error: Compare Failed!");
		}
	}

}
