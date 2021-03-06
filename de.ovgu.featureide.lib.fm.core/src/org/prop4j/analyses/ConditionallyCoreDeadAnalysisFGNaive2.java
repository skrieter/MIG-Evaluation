/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2016  FeatureIDE team, University of Magdeburg, Germany
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
package org.prop4j.analyses;

import org.prop4j.solver.ISatSolver;
import org.prop4j.solver.SatInstance;
import org.sat4j.core.VecInt;

import de.ovgu.featureide.fm.core.conf.IFeatureGraph2;
import de.ovgu.featureide.fm.core.conf.IFeatureGraph2.ITraverser;
import de.ovgu.featureide.fm.core.job.monitor.IMonitor;

/**
 * Finds core and dead features.
 * 
 * @author Sebastian Krieter
 */
public class ConditionallyCoreDeadAnalysisFGNaive2 extends AConditionallyCoreDeadAnalysis {

	private final ITraverser traverser;

	public ConditionallyCoreDeadAnalysisFGNaive2(ISatSolver solver, IFeatureGraph2 featureGraph) {
		super(solver);
		this.traverser = featureGraph.traverse();
	}

	public ConditionallyCoreDeadAnalysisFGNaive2(SatInstance satInstance, IFeatureGraph2 featureGraph) {
		super(satInstance);
		this.traverser = featureGraph.traverse();
	}

	public int[] analyze(IMonitor monitor) throws Exception {
		satCount = 0;
		solver.getAssignment().ensure(fixedVariables.length);

		final int[] model2 = new int[solver.getSatInstance().getNumberOfVariables()];
		for (int i = 0; i < fixedVariables.length; i++) {
			final int var = fixedVariables[i];
			final int j = Math.abs(var) - 1;
			model2[j] = var;
		}

		traverser.clear();
		for (int i = 0; i < newCount; i++) {
			traverser.traverse(fixedVariables[i], model2);
		}
		final VecInt v = traverser.getRelevantVariables();

		for (int k = 0; k < model2.length; k++) {
			final int var = model2[k];
			if (var != 0) {
				solver.assignmentPush(var);
			}
		}

		if (!v.isEmpty()) {
			final int[] model1 = solver.findModel();
			satCount++;
			if (model1 != null) {
				for (int k = 0; k < model2.length; k++) {
					final int var = model2[k];
					if (var != 0) {
						model1[Math.abs(var) - 1] = 0;
					}
				}
				sat(model1, v);
			}
		}
		return solver.getAssignmentArray(0, solver.getAssignment().size());
	}

	private void sat(int[] model1, VecInt v) {
		while (!v.isEmpty()) {
			final int varX = v.get(v.size() - 1);
			v.pop();
			final int i = Math.abs(varX) - 1;
			if (model1[i] == varX) {
				solver.assignmentPush(-varX);
				satCount++;
				switch (solver.isSatisfiable()) {
				case FALSE:
					solver.assignmentReplaceLast(varX);
					model1[i] = 0;
					traverser.traverse2(varX, model1, solver.getAssignment());
					break;
				case TIMEOUT:
					throw new RuntimeException();
				case TRUE:
					solver.assignmentPop();
					break;
				}
			}
		}
	}

	@Override
	public String toString() {
		return "FG_Naive    ";
	}

}
