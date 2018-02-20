package analysis.gencorr;
import gui.base.GUI;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.samtools.AbstractBAMFileIndex;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMSequenceRecord;

import data.param.GenParameter;

public class GenCorrLoad {
	protected GUI gui;
	protected GenParameter param;
	
	public GenCorrLoad(GUI gu, GenParameter par) {
		gui = gu;
		param = par;
	}
	
	public void run() {
		printTimeStamp(gui);
		int PRIME = param.getPrimeExp();
		int SEC = param.getSecExp();
		
		SAMFileReader reader = new SAMFileReader(param.getInput().get(PRIME), param.getIndex().get(PRIME));
		AbstractBAMFileIndex bai = (AbstractBAMFileIndex) reader.getIndex();			
		
		double Sx = 0;
		double Sxx = 0;
		double Sy = 0;
		double Syy = 0;
		double Sxy = 0;
		double count = 0;
		
		//Acquire Statistics from BAM file
		System.out.println("-----------------------------------------\nCurrent File: " + param.getInputIndex(PRIME));
		gui.printToGui("-----------------------------------------\nCurrent File: " + param.getInputIndex(PRIME));
		System.out.println("Genome Size: " + param.getTotalgenomeSize()[PRIME] + "\nTotal Tags: " + param.getTotaltagCount()[PRIME]);
		gui.printToGui("Genome Size: " + param.getTotalgenomeSize()[PRIME] + "\nTotal Tags: " + param.getTotaltagCount()[PRIME]);
		
		System.out.println("-----------------------------------------\nCurrent File: " + param.getInputIndex(SEC));
		gui.printToGui("-----------------------------------------\nCurrent File: " + param.getInputIndex(SEC));
		System.out.println("Genome Size: " + param.getTotalgenomeSize()[SEC] + "\nTotal Tags: " + param.getTotaltagCount()[SEC]);
		gui.printToGui("Genome Size: " + param.getTotalgenomeSize()[SEC] + "\nTotal Tags: " + param.getTotaltagCount()[SEC]);
		
		//Need to extract all at once for optimal efficiency
		Vector<GenCorrNode> ChromosomeWindows = null;	
		for(int numchrom = 0; numchrom < bai.getNumberOfReferences(); numchrom++) {
			//Object to keep track of the chromosomal data
			ChromosomeWindows = new Vector<GenCorrNode>();
			SAMSequenceRecord seq = reader.getFileHeader().getSequence(numchrom);
			System.out.println("\nAnalyzing: " + seq.getSequenceName());
			gui.printToGui("Analyzing: " + seq.getSequenceName());
			//Break chromosome into 100kb chunks and assign to independent BLASTNodes
			int numwindows = (int) (seq.getSequenceLength() / 100000);
			int Resolution = param.getResolution();	// Resolution controls increment
			for(int x = 0; x < numwindows; x++) {
				int start = x * 100000;
				int stop = start + 100000;
				ChromosomeWindows.add(new GenCorrNode(seq.getSequenceName(), start, stop));
			}
			int finalstart = numwindows * 100000;
			int finalstop = (seq.getSequenceLength() / Resolution) * Resolution;
			ChromosomeWindows.add(new GenCorrNode(seq.getSequenceName(), finalstart, finalstop));
			//Load Chromosome Windows with data from ALL experiments
			int numberofThreads = param.getThreads();
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
				GenCorrExtract nodeextract = new GenCorrExtract(param, ChromosomeWindows, currentindex, subset);
				parseMaster.execute(nodeextract);
			}
			parseMaster.shutdown();
			while (!parseMaster.isTerminated()) {
			}
			GenCorrExtract.resetProgress();
						
			for(int x = 0; x < ChromosomeWindows.size(); x++) {
				Sx += ChromosomeWindows.get(x).getSx();
				Sxx += ChromosomeWindows.get(x).getSxx();
				Sy += ChromosomeWindows.get(x).getSy();
				Syy += ChromosomeWindows.get(x).getSyy();
				Sxy += ChromosomeWindows.get(x).getSxy();
				count += ChromosomeWindows.get(x).getCount();
			}
		}
				
		double numerator = 0;
		double denominator = 0;
				
		numerator = Sxy - ((Sx * Sy) / count);
		denominator = Math.sqrt((Sxx - ((Sx * Sx) / count)) * (Syy - ((Sy * Sy / count))));
		
		double correlation = numerator / denominator;
		System.out.println("\nCorrelation: " + correlation);
		gui.printToGui("Correlation: " + correlation);				
		
		String name = param.getInput().get(param.getPrimeExp()).getName() + "_" + param.getInput().get(param.getSecExp()).getName();
		param.addCorr(correlation);
		param.addName(name);
		
		reader.close();
		
		System.out.println("Analysis Complete");
		gui.printToGui("Analysis Complete");				
		printTimeStamp(gui);
	}
	
	private static void printTimeStamp(GUI gui) {
		Calendar timestamp = new GregorianCalendar();
		System.out.print("Current Time: " + timestamp.get(Calendar.MONTH) + "-" + timestamp.get(Calendar.DAY_OF_MONTH) + "-" + timestamp.get(Calendar.YEAR));
		System.out.println("\t" + timestamp.get(Calendar.HOUR_OF_DAY) + ":" + timestamp.get(Calendar.MINUTE) + ":" + timestamp.get(Calendar.SECOND));
		gui.printToGui("Current Time: " + timestamp.get(Calendar.MONTH) + "-" + timestamp.get(Calendar.DAY_OF_MONTH) + "-" + timestamp.get(Calendar.YEAR) + "\t" + timestamp.get(Calendar.HOUR_OF_DAY) + ":" + timestamp.get(Calendar.MINUTE) + ":" + timestamp.get(Calendar.SECOND));

	}
}
