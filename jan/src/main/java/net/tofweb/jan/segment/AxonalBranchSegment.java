package net.tofweb.jan.segment;

import java.util.concurrent.ThreadLocalRandom;

import net.tofweb.jan.Configuration;
import net.tofweb.jan.network.SynapticTerminal;
import net.tofweb.jan.neuron.ArtificialNeuron;

public class AxonalBranchSegment extends BranchSegment {

	public AxonalBranchSegment(ArtificialNeuron parentNeuron, Segment parentSegment) {
		super(parentNeuron, parentSegment);

		setLength(Configuration.getAxonSegmentLength());
		setRadius(Configuration.getAxonSegmentRadius());
		setMembraneCapacitance(Configuration.getAxonMembraneCapacitance());
		setMembraneResistance(Configuration.getAxonMembraneResistance());
		setIntracellularResistance(Configuration.getAxonIntracellularResistance());
		setRestingPotential(Configuration.getAxonRestingPotential());
		setSegmentSplitMaximum(Configuration.getAxonSegmentSplitMaximum());
		setSynapsesPerMicroMeterSquared(Configuration.getAxonSynapsesPerMicroMeterSquared());

		populateSynapses();
	}

	public void arborize() {
		int maxRemainingAxonalChildren = this.getParentNeuron().getNumRemainingAxonalChildren();
		int remainingSegmentChildren = ThreadLocalRandom.current().nextInt(0, getSegmentSplitMaximum() + 1);
		nativeArborize(maxRemainingAxonalChildren, remainingSegmentChildren);
	}

	private void populateSynapses() {
		Integer synapsesRemaining = getSynapsesPerMicroMeterSquared() * getSurfaceArea().getMicroMeters().intValue();

		getSynapses().clear();
		while (synapsesRemaining > 0) {
			addSynapse(new SynapticTerminal());
			synapsesRemaining--;
		}
	}

	private void nativeArborize(Integer maxRemainingChildren, Integer remainingSegmentSplits) {
		if (maxRemainingChildren > 0) {
			while (remainingSegmentSplits > 0) {

				ArtificialNeuron parentNeuron = this.getParentNeuron();
				maxRemainingChildren = parentNeuron.getNumRemainingAxonalChildren();

				if (maxRemainingChildren > 0) {
					AxonalBranchSegment nextChild = new AxonalBranchSegment(parentNeuron, this);
					this.addChildSegment(nextChild);
					parentNeuron.setNumRemainingAxonalChildren(--maxRemainingChildren);
					remainingSegmentSplits--;
					nextChild.arborize();
				} else {
					break;
				}
			}
		}
	}
}
