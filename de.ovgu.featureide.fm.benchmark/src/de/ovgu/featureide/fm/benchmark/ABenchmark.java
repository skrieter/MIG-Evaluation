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
package de.ovgu.featureide.fm.benchmark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import de.ovgu.featureide.fm.benchmark.properties.IProperty;
import de.ovgu.featureide.fm.benchmark.properties.Seed;
import de.ovgu.featureide.fm.benchmark.properties.StringProperty;
import de.ovgu.featureide.fm.benchmark.properties.Timeout;
import de.ovgu.featureide.fm.core.Logger;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.manager.FeatureModelManager;

/**
 * @author Sebastian Krieter
 */
public abstract class ABenchmark {

	private static final String MODELS_ZIP_FILE = "models.zip";
	private static final String MODEL_FILE = "model.xml";

	private static final List<IProperty> propertyList = new LinkedList<>();

	protected static abstract class ATestRunner<T> implements Runnable {

		private T result;

		@Override
		public void run() {
			result = null;
			try {
				result = execute();
			} catch (Throwable e) {
				if (!(e instanceof ThreadDeath)) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}

		protected abstract T execute();

		public T getResult() {
			return result;
		}

	}

	protected static class ConsoleLogger extends FileOutputStream {

		private final OutputStream orgConsole;

		public ConsoleLogger(File file, OutputStream orgConsole) throws FileNotFoundException {
			super(file);
			this.orgConsole = orgConsole;
		}

		public void flush() throws IOException {
			super.flush();
			orgConsole.flush();
		}

		@Override
		public void write(byte[] buf, int off, int len) throws IOException {
			super.write(buf, off, len);
			orgConsole.write(buf, off, len);
		}

		@Override
		public void write(int b) throws IOException {
			super.write(b);
			orgConsole.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			super.write(b);
			orgConsole.write(b);
		}
	}

	private static final String MODELS_DIRECTORY = "models";
	private static final String CONFIG_DIRECTORY = "config";

	private static final String COMMENT = "#";
	private static final String STOP_MARK = "###";

	protected final static ProgressLogger logger = new ProgressLogger();
	protected final CSVWriter csvWriter = new CSVWriter();

	protected final List<String> modelNames;
	private final Random randSeed;

	private static final Seed seed = new Seed();
	protected static final Timeout timeout = new Timeout();
	protected static final StringProperty outputPath = new StringProperty("output");
	protected static final StringProperty modelsPath = new StringProperty("models");

	protected Path rootOutPath, pathToModels;

	protected final IFeatureModel init(final String name) {
		IFeatureModel fm = null;

		fm = lookUpFolder(pathToModels, name, fm);
		fm = lookUpFile(pathToModels.resolve("xml"), name, fm);
		fm = lookUpFile(pathToModels.resolve("dimacs"), name, fm);
		fm = lookUpZip(pathToModels, name, fm);

		if (fm == null) {
			throw new RuntimeException("Model not found: " + name);
		} else {
			return fm;
		}
	}

	protected IFeatureModel loadFile(final Path path) {
		return FeatureModelManager.load(path).getObject();
	}

	protected IFeatureModel lookUpFolder(final Path rootPath, final String name, IFeatureModel fm) {
		if (fm != null) {
			return fm;
		} else {
			final Path path = rootPath.resolve(name).resolve(MODEL_FILE);
			return (Files.exists(path)) ? loadFile(path) : null;
		}
	}

	protected IFeatureModel lookUpFile(final Path rootPath, final String name, IFeatureModel fm) {
		if (fm != null) {
			return fm;
		} else {
			try {
				DirectoryStream<Path> files = Files.newDirectoryStream(rootPath, new Filter<Path>() {
					@Override
					public boolean accept(Path entry) throws IOException {
						return entry.getFileName().toString().matches("^" + name + "\\.\\w+$")
								&& Files.isRegularFile(entry) && Files.isReadable(entry);
					}
				});
				final Iterator<Path> iterator = files.iterator();
				return iterator.hasNext() ? loadFile(iterator.next()) : null;
			} catch (IOException e) {
				printErr(e.getMessage());
			}
			return null;
		}
	}

	protected IFeatureModel lookUpZip(final Path rootPath, final String name, IFeatureModel fm) {
		if (fm != null) {
			return fm;
		} else {
			final URI uri = URI.create("jar:" + rootPath.resolve(MODELS_ZIP_FILE).toUri().toString());
			try (final FileSystem zipFs = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap())) {
				for (Path root : zipFs.getRootDirectories()) {
					fm = lookUpFolder(root, name, fm);
					fm = lookUpFile(root, name, fm);
				}
				return fm;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static void addProperty(IProperty property) {
		propertyList.add(property);
	}

	private static void readProperties() {
		final Path path = Paths.get(CONFIG_DIRECTORY + File.separator + "config.properties");
		logger.print("Reading config file. (" + path.toString() + ") ... ");
		final Properties properties = new Properties();
		try {
			properties.load(Files.newInputStream(path));
			logger.println("Success!");
		} catch (IOException e) {
			logger.println("Fail! -> " + e.getMessage());
		}
		for (IProperty prop : propertyList) {
			logger.print("\t" + prop.getKey() + " = ");
			boolean success = prop.setValue(properties.getProperty(prop.getKey()));
			logger.print(prop.getValue().toString());
			logger.println(success ? "" : " (default value!)");
		}
	}

	protected final <T> T run(ATestRunner<T> testRunner) {
		final Thread thread = new Thread(testRunner);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return testRunner.getResult();
	}

	@SuppressWarnings("deprecation")
	protected final <T> T run(ATestRunner<T> testRunner, long timeout) {
		final Thread thread = new Thread(testRunner);
		logger.getTimer().start();
		thread.start();
		try {
			thread.join(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (thread.isAlive()) {
			thread.stop();
		}
		logger.getTimer().stop();
		return testRunner.getResult();
	}

	public ABenchmark() {
		readProperties();
		initPaths();

		final Path consoleOutputPath = rootOutPath.resolve("console.txt");
		final Path consoleErrorPath = rootOutPath.resolve("error_log.txt");
		final PrintStream orgOutConsole = System.out;
		final PrintStream orgErrConsole = System.err;
		try {
			System.setOut(new PrintStream(new ConsoleLogger(consoleOutputPath.toFile(), orgOutConsole)));
			System.setErr(new PrintStream(new ConsoleLogger(consoleErrorPath.toFile(), orgErrConsole)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(CONFIG_DIRECTORY + File.separator + "models.txt"),
					Charset.defaultCharset());
		} catch (IOException e) {
			logger.println("No feature models specified!");
			Logger.logError(e);
		}

		if (lines != null) {
			boolean pause = false;
			modelNames = new ArrayList<>(lines.size());
			for (String modelName : lines) {
				// modelName = modelName.trim();
				if (!modelName.trim().isEmpty()) {
					if (!modelName.startsWith("\t")) {

						if (modelName.startsWith(COMMENT)) {
							if (modelName.equals(STOP_MARK)) {
								pause = !pause;
							}
						} else if (!pause) {
							modelNames.add(modelName.trim());
						}
					}
				}
			}
		} else {
			modelNames = Collections.<String>emptyList();
		}

		randSeed = new Random(seed.getValue());
	}

	private void initPaths() {
		rootOutPath = Paths.get(((outputPath.getValue().isEmpty()) ? "output" : outputPath.getValue()) + File.separator
				+ (Long.MAX_VALUE - System.currentTimeMillis()));
		try {
			Files.createDirectories(rootOutPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pathToModels = Paths.get((modelsPath.getValue().isEmpty()) ? MODELS_DIRECTORY : modelsPath.getValue());
	}

	protected final long getNextSeed() {
		return randSeed.nextLong();
	}

	protected final IFeatureModel initModel(int index) {
		return init(modelNames.get(index));
	}

	protected static final String getCurTime() {
		return new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss").format(new Timestamp(System.currentTimeMillis()));
	}

	protected static final void printErr(String message) {
		System.err.println(getCurTime() + " " + message);
	}

	protected static final void printOut(String message) {
		System.out.println(getCurTime() + " " + message);
	}

	protected static final void printOut(String message, int tabs) {
		StringBuilder sb = new StringBuilder(getCurTime());
		sb.append(" ");
		for (int i = 0; i < tabs; i++) {
			sb.append('\t');
		}
		sb.append(message);
		System.out.println(sb.toString());
	}

}
