package analysis.corr;

import gui.base.GUI;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import components.CreateChartPanel;
import components.RawDataDisplayPanel;

import data.param.CorrParameter;

import net.sf.samtools.AbstractBAMFileIndex;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMSequenceRecord;

public class CorrLoad {
	protected GUI gui;
	protected CorrParameter param;
	
	private double[] xData;
	private double[] yData;
	
	public CorrLoad(GUI gu, CorrParameter par) {
		gui = gu;
		param = par;
		xData = new double[param.getCorrWindow()];
		yData = new double[param.getCorrWindow()];
	}
	
	public void run(int index) {
		printTimeStamp(gui);
		SAMFileReader reader = new SAMFileReader(param.getInput().get(index), param.getIndex().get(index));
		AbstractBAMFileIndex bai = (AbstractBAMFileIndex) reader.getIndex();			
		
		double[] Sx = new double[param.getCorrWindow()];
		double[] Sxx = new double[param.getCorrWindow()];
		double[] Sy = new double[param.getCorrWindow()];
		double[] Syy = new double[param.getCorrWindow()];
		double[] Sxy = new double[param.getCorrWindow()];
		double[] count = new double[param.getCorrWindow()];
		
		//Need to extract all at once for optimal efficiency
		Vector<CorrNode> ChromosomeWindows;	
		for(int numchrom = 0; numchrom < bai.getNumberOfReferences(); numchrom++) {
			//Object to keep track of the chromosomal data
			ChromosomeWindows = new Vector<CorrNode>();
			SAMSequenceRecord seq = reader.getFileHeader().getSequence(numchrom);
			System.out.println("\nAnalyzing: " + seq.getSequenceName());
			gui.printToGui("Analyzing: " + seq.getSequenceName());
			if(!param.getCorrType()) {
				//Randomly select #_Iteration sample from each chromosome at user-specified size
				for(int x = 0; x < param.getIterations(); x++) {
					int start = (int)(Math.random() * (double)(seq.getSequenceLength() - param.getWindow()));
					int stop = start + param.getWindow();
					ChromosomeWindows.add(new CorrNode(seq.getSequenceName(), start, stop));
				}
				
			} else {
				//Break chromosome into 100kb chunks and assign to independent BLASTNodes
				int numwindows = (int) (seq.getSequenceLength() / 100000);
				int Resolution = param.getResolution();	// Resolution controls increment
				int windowSize = param.getCorrWindow(); // Size of theoretical sliding window
				for(int x = 0; x < numwindows; x++) {
					int start = x * 100000;
					int stop = start + 100000 + windowSize;
					ChromosomeWindows.add(new CorrNode(seq.getSequenceName(), start, stop));
				}
				int finalstart = numwindows * 100000;
				int finalstop = (seq.getSequenceLength() / Resolution) * Resolution;
				ChromosomeWindows.add(new CorrNode(seq.getSequenceName(), finalstart, finalstop));
			}
			
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
				CorrExtract nodeextract = new CorrExtract(param, ChromosomeWindows, currentindex, subset);
				parseMaster.execute(nodeextract);
			}
			parseMaster.shutdown();
			while (!parseMaster.isTerminated()) {
			}
			CorrExtract.resetProgress();
			
			for(int x = 0; x < ChromosomeWindows.size(); x++) {
				for(int y = 0; y < Sx.length; y++) {
					Sx[y] += ChromosomeWindows.get(x).getSx();
					Sxx[y] += ChromosomeWindows.get(x).getSxx();
					Sy[y] += ChromosomeWindows.get(x).getSy();
					Syy[y] += ChromosomeWindows.get(x).getSyy();
					Sxy[y] += ChromosomeWindows.get(x).getSxy()[y];
					count[y] += ChromosomeWindows.get(x).getCount();
				}
			}
		}
		
		double[] numerator = new double[param.getCorrWindow()];
		double[] denominator = new double[param.getCorrWindow()];
		
		double PEAK = -999;
		double PEAK_SHIFT = -999;
		for(int x = 0; x < Sx.length; x++) {
			numerator[x] = Sxy[x] - ((Sx[x] * Sy[x]) / count[x]);
			denominator[x] = Math.sqrt((Sxx[x] - ((Sx[x] * Sx[x]) / count[x])) * (Syy[x] - ((Sy[x] * Sy[x] / count[x]))));
			yData[x] = numerator[x] / denominator[x];
			xData[x] = x;
			if(yData[x] > PEAK) {
				PEAK = yData[x];
				PEAK_SHIFT = x;
			}
			//System.out.println(Sx[x] + "\t" + Sxx[x] + "\t" + Sy[x] + "\t" + Syy[x] + "\t" + Sxy[x] + "\t" + count[x]);
		}
		
		reader.close();
		
		System.out.println("Analysis Complete");
		gui.printToGui("Analysis Complete");
		
		System.out.println("Peak At: " + PEAK_SHIFT + "\nScore: " + PEAK);
		gui.printToGui("Peak At: " + PEAK_SHIFT + "\nScore: " + PEAK);
		
		printTimeStamp(gui);
		
		//Add correlation graph
		addPanes();
	}
	
	public void addPanes(){
		CreateChartPanel panel = new CreateChartPanel();
		gui.addCorrelation(param.getInput().get(param.getExp()).getName(), panel.createXYPlot(xData,yData,param.getInput().get(param.getExp()).getName()));
		RawDataDisplayPanel raw = new RawDataDisplayPanel();
		raw.setAreaXY(xData, yData);
		gui.addRawCorrelation(param.getInput().get(param.getExp()).getName(),raw);
		gui.showOnlyCorrelations();
	}
	
	private static void printTimeStamp(GUI gui) {
		Calendar timestamp = new GregorianCalendar();
		System.out.print("Current Time: " + timestamp.get(Calendar.MONTH) + "-" + timestamp.get(Calendar.DAY_OF_MONTH) + "-" + timestamp.get(Calendar.YEAR));
		System.out.println("\t" + timestamp.get(Calendar.HOUR_OF_DAY) + ":" + timestamp.get(Calendar.MINUTE) + ":" + timestamp.get(Calendar.SECOND));
		gui.printToGui("Current Time: " + timestamp.get(Calendar.MONTH) + "-" + timestamp.get(Calendar.DAY_OF_MONTH) + "-" + timestamp.get(Calendar.YEAR) + "\t" + timestamp.get(Calendar.HOUR_OF_DAY) + ":" + timestamp.get(Calendar.MINUTE) + ":" + timestamp.get(Calendar.SECOND));

	}
}
