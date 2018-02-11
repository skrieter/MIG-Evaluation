package de.ovgu.featureide.fm.benchmark.simple;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Random;

import org.prop4j.And;
import org.prop4j.Node;
import org.prop4j.Or;
import org.prop4j.solver.ISatSolver.SatResult;
import org.prop4j.solver.ModifiableSolver;
import org.prop4j.solver.SatInstance;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;

import de.ovgu.featureide.fm.core.ExtensionManager.NoSuchExtensionException;
import de.ovgu.featureide.fm.core.base.FeatureUtils;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.IFeatureModelFactory;
import de.ovgu.featureide.fm.core.base.impl.FMFactoryManager;
import de.ovgu.featureide.fm.core.editing.AdvancedNodeCreator;
import de.ovgu.featureide.fm.core.editing.AdvancedNodeCreator.CNFType;
import de.ovgu.featureide.fm.core.filter.AbstractFeatureFilter;
import de.ovgu.featureide.fm.core.io.IFeatureModelFormat;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.ovgu.featureide.fm.core.io.sxfm.SXFMFormat;

public class Generator {

	private static Random rand = new Random(0);
	private static SatInstance satInstance;

	public static void main(String[] args) throws ContradictionException, IOException, NoSuchExtensionException {
		if (args.length != 1) {
			return;
		}

		final Path in = Paths.get(args[1]);
		final IFeatureModelFormat format = new SXFMFormat();
		final IFeatureModelFactory factory = FMFactoryManager.getDefaultFactoryForFormat(format);

		Files.walkFileTree(in, new FileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (attrs.isRegularFile() && Files.isReadable(file)) {
					final IFeatureModel fm = factory.createFeatureModel();
					FileHandler.load(file, fm, format);
					try {
						String fileName = file.getFileName().toString();
						final int extIndex = fileName.lastIndexOf('.');
						if (extIndex > 0) {
							fileName = fileName.substring(0, extIndex);
						}
						final String folderName = file.getParent().getFileName().toString() + "_" + fileName;
						blub(fm, folderName);
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private static void blub(IFeatureModel fm, String folderName) throws ContradictionException, IOException {
		AdvancedNodeCreator nodeCreator = new AdvancedNodeCreator(fm, new AbstractFeatureFilter());
		nodeCreator.setCnfType(CNFType.Regular);
		nodeCreator.setIncludeBooleanValues(false);
		nodeCreator.setOptionalRoot(false);
		satInstance = new SatInstance(nodeCreator.createNodes());

		final IFeatureModelFactory factory = FMFactoryManager.getDefaultFactory();
		final IFeatureModel fmNew = factory.createFeatureModel();

		final IFeature orgRoot = FeatureUtils.getRoot(fm);

		final IFeature newRoot = factory.createFeature(fmNew, orgRoot.getName());
		fmNew.addFeature(newRoot);
		fmNew.getStructure().setRoot(newRoot.getStructure());

		for (IFeature orgFeature : fm.getFeatures()) {
			if (orgFeature != orgRoot && orgFeature.getStructure().isConcrete()) {
				final IFeature newFeature = factory.createFeature(fmNew, orgFeature.getName());
				newFeature.getStructure().setMandatory(false);
				newRoot.getStructure().addChild(newFeature.getStructure());
				fmNew.addFeature(newFeature);
			}
		}
		for (Node clause : satInstance.getCnf().getChildren()) {
			fmNew.addConstraint(factory.createConstraint(fmNew, clause));
		}

		final ModifiableSolver solver = new ModifiableSolver(satInstance);
		curModel = solver.findModel();

		rand = new Random(0);

		final int nof = satInstance.getNumberOfVariables();
		assert nof % 1000 == 0 : nof + " " + folderName;

		long timeDelta = 0;
		long startTime = System.currentTimeMillis();
		for (int i = 1; i <= nof; i++) {
			final Node newClause = newClause(solver);
			if (newClause == null) {
				i--;
				if (timeDelta > 4000) {
					break;
				}
			} else {
				fmNew.addConstraint(factory.createConstraint(fmNew, newClause));
				System.out.println(nof - i);
				if (i % 100 == 0) {
					save(fmNew, folderName + "_" + i);
				}
				startTime = System.currentTimeMillis();
			}
			timeDelta = System.currentTimeMillis() - startTime;
		}
	}

	private static void save(IFeatureModel fm, String folderName) throws IOException {
		final Path out = Paths.get("C:\\Kriety\\icse17\\Gen_Refined\\Models\\" + folderName);
		Files.createDirectories(out);
		FeatureModelManager.writeToFile(fm, out.resolve("model.xml"));
		Files.write(Paths.get("C:\\Kriety\\icse17\\Gen_Refined\\models.txt"), (folderName + "\n").getBytes(),
				StandardOpenOption.APPEND, StandardOpenOption.CREATE);
	}

	private static final int clauseSize = 3;

	private static int[] curModel;

	private static Node newClause(ModifiableSolver solver) {
		final Node[] literals = new Node[clauseSize];
		boolean satisfied = false;
		for (int i = 0; i < clauseSize; i++) {
			final int var = createVar();
			if (curModel[Math.abs(var) - 1] == var) {
				satisfied = true;
			}
			solver.assignmentPush(-var);
			literals[i] = satInstance.convertToLiteral(var);
		}

		if (solver.isSatisfiable() == SatResult.FALSE) {
			solver.assignmentClear(0);
			return null;
		}

		final And constraint = new And(new Or(literals));
		List<IConstr> constList;
		try {
			constList = solver.addClauses(constraint);
		} catch (ContradictionException e) {
			return null;
		}
		assert constList.size() == 1;

		if (!satisfied) {
			final int[] tempModel = solver.findModel();
			if (tempModel == null) {
				solver.removeConstraint(constList.get(0));
				return null;
			} else {
				curModel = tempModel;
			}
		}
		return constraint;
	}

	private static int createVar() {
		final int numberOfVariables = satInstance.getNumberOfVariables();
		int var = rand.nextInt(numberOfVariables << 1) - numberOfVariables;
		if (var >= 0) {
			var += 1;
		}
		return var;
	}

}
