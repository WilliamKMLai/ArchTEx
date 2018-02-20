package analysis.extraction;
import gui.base.GUI;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import data.object.GenomeNode;
import data.param.ExParameter;

import net.sf.samtools.AbstractBAMFileIndex;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMSequenceRecord;

public class StatGen {

	public static void generate_LogStats(GUI gui, ExParameter Parameters) {
		SAMFileReader reader = new SAMFileReader(Parameters.getInputIndex(Parameters.getExp()), Parameters.getIndexIndex(Parameters.getExp()));
		AbstractBAMFileIndex index = (AbstractBAMFileIndex) reader.getIndex();
		
		double mean = 0;
		double variance = 0;
		double counter = 0;
		
		//Need to extract all at once for optimal efficiency
		Vector<GenomeNode> ChromosomeWindows;	
		for(int numchrom = 0; numchrom < index.getNumberOfReferences(); numchrom++) {
			//Object to keep track of the chromosomal data
			ChromosomeWindows = new Vector<GenomeNode>();
			SAMSequenceRecord seq = reader.getFileHeader().getSequence(numchrom);
			System.out.println("\nAnalyzing: " + seq.getSequenceName());
			gui.printToGui("Analyzing: " + seq.getSequenceName());
			//Break chromosome into 100kb chunks and assign to independent BLASTNodes
			int numwindows = (int) (seq.getSequenceLength() / 100000);
			int Resolution = 1;
			//int Resolution = Parameters.getResolution();	// Resolution controls increment
			for(int x = 0; x < numwindows; x++) {
				int start = x * 100000;
				int stop = start + 100000;
				ChromosomeWindows.add(new GenomeNode(seq.getSequenceName(), start, stop));
			}
			int finalstart = numwindows * 100000;
			int finalstop = (seq.getSequenceLength() / Resolution) * Resolution;
			ChromosomeWindows.add(new GenomeNode(seq.getSequenceName(), finalstart, finalstop));
			
			//Load Chromosome Windows with data from ALL experiments
			int numberofThreads = Parameters.getThreads();
			int nodeSize = ChromosomeWindows.size();
			if(nodeSize < numberofThreads) {
				numberofThreads = nodeSize;
			}
			ExecutorService parseMaster = Executors.newFixedThreadPool(numberofThreads);
			int subset = 0;
			int currentindex = 0;
			for(int x = 0; x < numberofThreads; x++) {
				currentindex += subset;
				if(numberofThreads == 1) subset = nodeSize;
				else if(nodeSize % numberofThreads == 0) subset = nodeSize / numberofThreads;
				else {
					int remainder = nodeSize % numberofThreads;
					if(x < remainder ) subset = (int)(((double)nodeSize / (double)numberofThreads) + 1);
					else subset = (int)(((double)nodeSize / (double)numberofThreads));
				}
				GenomeExtract nodeextract = new GenomeExtract(Parameters, ChromosomeWindows, currentindex, subset);
				parseMaster.execute(nodeextract);
			}
			parseMaster.shutdown();
			while (!parseMaster.isTerminated()) {
			}
			GenomeExtract.resetProgress();
			
			for(int x = 0; x < ChromosomeWindows.size(); x++) {
				double[] tempwind = ChromosomeWindows.get(x).getData();
				for(int y = 0; y < tempwind.length; y++) {
					mean += tempwind[y];
					variance += Math.pow(tempwind[y], 2);
					counter++;
				}
			}
		}
		
		reader.close();
		
		variance = (variance - (Math.pow(mean, 2) / counter)) / (counter - 1);
		mean /= counter;
		
		System.out.println("\nMean: " + mean + "\nVariance: " + variance);
		
		Parameters.setLogAvg(mean);
		Parameters.setLogVar(variance);
	}
}