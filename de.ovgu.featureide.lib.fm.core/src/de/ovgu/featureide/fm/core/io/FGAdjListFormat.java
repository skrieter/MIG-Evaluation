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
package de.ovgu.featureide.fm.core.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;

import org.prop4j.analyses.AdjList;

import de.ovgu.featureide.fm.core.Logger;
import de.ovgu.featureide.fm.core.PluginID;

/**
 * Reads / Writes a feature graph.
 * 
 * @author Sebastian Krieter
 */
public class FGAdjListFormat implements IPersistentFormat<AdjList> {

	public static final String ID = PluginID.PLUGIN_ID + ".format.fg." + FGAdjListFormat.class.getSimpleName();

	@Override
	public ProblemList read(AdjList object, CharSequence source) {
		ProblemList problems = new ProblemList();
		try (final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(source.toString().getBytes(Charset.forName("UTF-8"))))) {
			object.copyValues((AdjList) in.readObject());
		} catch (IOException | ClassNotFoundException e) {
			problems.add(new Problem(e));
		}
		return problems;
	}

	@Override
	public String write(AdjList object) {
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		String ret = null;
		try (final ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream)) {
			out.writeObject(object);
			ret = byteArrayOutputStream.toString("UTF-8");
		} catch (IOException e) {
			Logger.logError(e);
		}
		return ret;
	}

	@Override
	public String getSuffix() {
		return "fg";
	}

	@Override
	public IPersistentFormat<AdjList> getInstance() {
		return this;
	}

	@Override
	public boolean supportsRead() {
		return true;
	}

	@Override
	public boolean supportsWrite() {
		return true;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean supportsContent(CharSequence content) {
		return supportsRead();
	}

}
