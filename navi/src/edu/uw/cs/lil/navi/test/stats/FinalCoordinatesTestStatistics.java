/*******************************************************************************
 * Navi. Copyright (C) 2013 Yoav Artzi
 * <p>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 ******************************************************************************/
package edu.uw.cs.lil.navi.test.stats;

import java.util.List;

import edu.uw.cs.lil.navi.data.Trace;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.map.Coordinates;
import edu.uw.cs.lil.tiny.data.IDataItem;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.tiny.test.stats.ITestingStatistics;
import edu.uw.cs.utils.composites.Pair;

public class FinalCoordinatesTestStatistics
		implements
		ITestingStatistics<Pair<Sentence, Task>, Pair<LogicalExpression, Trace>> {
	
	private final ITestingStatistics<Pair<Sentence, Task>, Coordinates>	baseStats;
	
	public FinalCoordinatesTestStatistics(
			ITestingStatistics<Pair<Sentence, Task>, Coordinates> baseStats) {
		this.baseStats = baseStats;
	}
	
	@Override
	public void recordNoParse(IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold) {
		baseStats.recordNoParse(dataItem, gold.second().getEndPosition()
				.getPose().getCoordinates());
		
	}
	
	@Override
	public void recordNoParseWithSkipping(
			IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold) {
		baseStats.recordNoParseWithSkipping(dataItem, gold.second()
				.getEndPosition().getPose().getCoordinates());
	}
	
	@Override
	public void recordParse(IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			Pair<LogicalExpression, Trace> label) {
		if (label.second() == null) {
			baseStats.recordNoParse(dataItem, gold.second().getEndPosition()
					.getPose().getCoordinates());
		} else {
			baseStats.recordParse(dataItem, gold.second().getEndPosition()
					.getPose().getCoordinates(), label.second()
					.getEndPosition().getPose().getCoordinates());
		}
	}
	
	@Override
	public void recordParses(IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			List<Pair<LogicalExpression, Trace>> labels) {
		final Coordinates coordinates = clusterCoordinates(labels);
		if (coordinates == null) {
			baseStats.recordNoParse(dataItem, gold.second().getEndPosition()
					.getPose().getCoordinates());
		} else {
			baseStats.recordParse(dataItem, gold.second().getEndPosition()
					.getPose().getCoordinates(), coordinates);
		}
	}
	
	@Override
	public void recordParsesWithSkipping(
			IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			List<Pair<LogicalExpression, Trace>> labels) {
		final Coordinates coordinates = clusterCoordinates(labels);
		if (coordinates == null) {
			baseStats.recordNoParseWithSkipping(dataItem, gold.second()
					.getEndPosition().getPose().getCoordinates());
		} else {
			baseStats.recordParseWithSkipping(dataItem, gold.second()
					.getEndPosition().getPose().getCoordinates(), coordinates);
		}
	}
	
	@Override
	public void recordParseWithSkipping(
			IDataItem<Pair<Sentence, Task>> dataItem,
			Pair<LogicalExpression, Trace> gold,
			Pair<LogicalExpression, Trace> label) {
		if (label.second() == null) {
			baseStats.recordNoParseWithSkipping(dataItem, gold.second()
					.getEndPosition().getPose().getCoordinates());
		} else {
			baseStats.recordParseWithSkipping(dataItem, gold.second()
					.getEndPosition().getPose().getCoordinates(), label
					.second().getEndPosition().getPose().getCoordinates());
		}
	}
	
	@Override
	public String toString() {
		return baseStats.toString();
	}
	
	@Override
	public String toTabDelimitedString() {
		return baseStats.toTabDelimitedString();
	}
	
	private Coordinates clusterCoordinates(
			List<Pair<LogicalExpression, Trace>> labels) {
		Coordinates coordinates = null;
		for (final Pair<LogicalExpression, Trace> label : labels) {
			if (label.second() == null) {
				return null;
			} else if (coordinates == null) {
				coordinates = label.second().getEndPosition().getPose()
						.getCoordinates();
			} else if (!coordinates.equals(label.second().getEndPosition()
					.getPose().getCoordinates())) {
				return null;
			}
		}
		return coordinates;
	}
}
