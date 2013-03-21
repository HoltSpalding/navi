package edu.uw.cs.lil.navi.experiments.plat.resources;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.uw.cs.lil.navi.data.InstructionSeqTrace;
import edu.uw.cs.lil.navi.data.InstructionSeqTraceDataset;
import edu.uw.cs.lil.navi.data.InstructionTrace;
import edu.uw.cs.lil.navi.data.InstructionTraceDataset;
import edu.uw.cs.lil.navi.eval.Task;
import edu.uw.cs.lil.navi.experiments.plat.NaviExperiment;
import edu.uw.cs.lil.navi.map.NavigationMap;
import edu.uw.cs.lil.tiny.data.sentence.Sentence;
import edu.uw.cs.lil.tiny.explat.IResourceRepository;
import edu.uw.cs.lil.tiny.explat.ParameterizedExperiment.Parameters;
import edu.uw.cs.lil.tiny.explat.resources.IResourceObjectCreator;
import edu.uw.cs.lil.tiny.explat.resources.usage.ResourceUsage;
import edu.uw.cs.lil.tiny.parser.ccg.genlex.ILexiconGenerator;
import edu.uw.cs.lil.tiny.parser.joint.model.JointDataItemWrapper;

public class InstructionTraceDatasetCreator<Y> implements
		IResourceObjectCreator<InstructionTraceDataset<Y>> {
	
	@SuppressWarnings("unchecked")
	@Override
	public InstructionTraceDataset<Y> create(Parameters params,
			IResourceRepository repo) {
		if (params.contains("sets")) {
			final InstructionSeqTraceDataset<Y> sets = repo.getResource(params
					.get("sets"));
			final List<InstructionTrace<Y>> items = new LinkedList<InstructionTrace<Y>>();
			for (final InstructionSeqTrace<Y> set : sets) {
				for (final InstructionTrace<Y> st : set) {
					items.add(st);
				}
			}
			return new InstructionTraceDataset<Y>(items);
		} else {
			try {
				return InstructionTraceDataset
						.readFromFile(
								params.getAsFile("file"),
								(Map<String, NavigationMap>) repo
										.getResource(NaviExperiment.MAPS_RESOURCE),
								(ILexiconGenerator<JointDataItemWrapper<Sentence, Task>, Y>) repo
										.getResource(params.get("genlex")));
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public String type() {
		return "data.trc";
	}
	
	@Override
	public ResourceUsage usage() {
		return new ResourceUsage.Builder(type(), InstructionTraceDataset.class)
				.setDescription(
						"Dataset of single instructions paired with execution traces.")
				.addParam(
						"sets",
						"id",
						"Dataset of instruction sequences to construct a labeled instruction dataset from (may be used instead of file and genlex).")
				.addParam("file", "file", "Dataset file")
				.addParam("genlex", "id", "Lexical generator").build();
	}
	
}