package de.ovgu.featureide.fm.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class CSVWriter {
	private static final String NEWLINE = System.lineSeparator();

	private final List<List<String>> values = new ArrayList<>();

	private String separator = ";";
	private List<String> header = null;

	private Path outputPath = Paths.get("");
	private Path p;
	private boolean dummy = false;

	public Path getOutputPath() {
		return outputPath;
	}

	public boolean setOutputPath(Path outputPath) {
		if (Files.isDirectory(outputPath)) {
			this.outputPath = outputPath;
			return true;
		} else if (!Files.exists(outputPath)) {
			try {
				Files.createDirectories(outputPath);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			this.outputPath = outputPath;
			return true;
		} else {
			return false;
		}
	}

	public void setFileName(Path fileName) {
		p = outputPath.resolve(fileName);
		try {
			Files.deleteIfExists(p);
			Files.createFile(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		reset();
	}

	public void setFileName(String fileName) {
		p = outputPath.resolve(fileName);
		try {
			Files.deleteIfExists(p);
			Files.createFile(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		reset();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (header != null) {
			writer(sb, header);
		}
		for (List<String> lines : values) {
			writer(sb, lines);
		}
		return sb.toString();
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public List<String> getHeader() {
		return header;
	}

	public void setHeader(List<String> header) {
		this.header = header;
	}

	public void addLine(List<String> line) {
		values.add(line);
	}

	public void createNewLine() {
		values.add(new ArrayList<String>());
	}

	public void flush() {
		if (p != null) {
			if (dummy) {
				values.remove(values.size() - 1);
			} else {
				if (!values.isEmpty()) {
					try {
						StringBuilder sb = new StringBuilder();
						writer(sb, values.get(values.size() - 1));
						Files.write(p, sb.toString().getBytes(), StandardOpenOption.APPEND);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void addValue(Object o) {
		values.get(values.size() - 1).add(o.toString());
	}

	public List<List<String>> getValues() {
		return values;
	}

	private void writer(StringBuilder sb, List<String> line) {
		for (String value : line) {
			if (value != null) {
				sb.append(value);
			}
			sb.append(separator);
		}
		if (line.isEmpty()) {
			sb.append(NEWLINE);
		} else {
			final int length = sb.length() - 1;
			sb.replace(length, length + separator.length(), NEWLINE);
		}
	}

	public boolean saveToFile(Path p) {
		try {
			Files.write(p, toString().getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void reset() {
		values.clear();
	}

	public boolean isDummy() {
		return dummy;
	}

	public void setDummy(boolean dummy) {
		this.dummy = dummy;
	}

}
