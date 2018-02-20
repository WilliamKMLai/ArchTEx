package analysis.extraction;
import gui.base.GUI;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import components.CreateChartPanel;
import components.RawDataDisplayPanel;

import data.object.Node;
import data.param.ExParameter;

public class SiteLoad extends Thread{
	protected GUI gui;
	protected ExParameter param;
	protected Node site;

	private double[] xData;
	
	public SiteLoad(GUI gu, ExParameter par, Node node) {
		gui = gu;
		param = par;
		site = node;
	}
	
	public void run() {
		printTimeStamp(gui);
		
		Vector<double[]> DATAARRAY = new Vector<double[]>(param.getInput().size());
		for(int x = 0; x < param.getInput().size(); x++) {
			param.setExp(x);
			System.out.println("-----------------------------------------\nCurrent File: " + param.getInputIndex(param.getExp()));
			gui.printToGui("-----------------------------------------\nCurrent File: " + param.getInputIndex(param.getExp()));	
			//Acquire Statistics from BAM file
			System.out.println("Genome Size: " + param.getTotalgenomeSize()[param.getExp()] + "\nTotal Tags: " + param.getTotaltagCount()[param.getExp()]);
			gui.printToGui("Genome Size: " + param.getTotalgenomeSize()[param.getExp()] + "\nTotal Tags: " + param.getTotaltagCount()[param.getExp()]);

			if(param.getTrans().equals("zlog")) {
				param.setTrans("log2");
				StatGen.generate_LogStats(gui, param);
				param.setTrans("zlog");
			}
			//Load Features
			Vector<Node> NODES = new Vector<Node>();
			NODES.add(site);
			loadFeatures(param, NODES);
			DATAARRAY.add(NODES.get(0).getData());
		}
		
		addPanes(DATAARRAY);
		System.out.println();
		printTimeStamp(gui);
	}
	
	public void addPanes(Vector<double[]> arraydata) {
		xData = new double[(param.getWindow() / param.getResolution()) + 1];
		for(int x = 0; x < xData.length; x++) xData[x] = (x * param.getResolution()) - (param.getWindow() / 2);
		CreateChartPanel panel = new CreateChartPanel();
		gui.addExtraction(site.getID(), panel.createXYPlot(xData, arraydata, param.getInput()));
		//RawDataDisplayPanel raw = new RawDataDisplayPanel();
		//raw.setAreaXY(xData, arraydata);
		RawDataDisplayPanel raw = new RawDataDisplayPanel(xData, arraydata);
		gui.addRawExtraction(site.getID(), raw);
		gui.showOnlyExtractions();
	}
	
	private static void loadFeatures(ExParameter Parameters, Vector<Node> NODES) {
		//Multi-thread load data from BAM file
		//Split input coordinates into t subsets for BAM extraction (t: number of threads)
		int numberofThreads = Parameters.getThreads();
		int nodeSize = NODES.size();
		if(nodeSize < numberofThreads) {
			numberofThreads = nodeSize;
		}
		int subset = 0;
		int currentindex = 0;
		ExecutorService parseMaster = Executors.newFixedThreadPool(numberofThreads);
		for(int x = 0; x < numberofThreads; x++) {
			currentindex += subset;
			if(numberofThreads == 1) subset = nodeSize;
			else if(nodeSize % numberofThreads == 0) subset = nodeSize / numberofThreads;
			else {
				int remainder = nodeSize % numberofThreads;
				if(x < remainder ) subset = (int)(((double)nodeSize / (double)numberofThreads) + 1);
				else subset = (int)(((double)nodeSize / (double)numberofThreads));
			}
			Extract nodeextract = new Extract(Parameters, NODES, currentindex, subset);
			parseMaster.execute(nodeextract);
		}
		parseMaster.shutdown();
		while (!parseMaster.isTerminated()) {
		}
		Extract.resetProgress();
	}
	
	private static void printTimeStamp(GUI gui) {
		Calendar timestamp = new GregorianCalendar();
		System.out.print("Current Time: " + timestamp.get(Calendar.MONTH) + "-" + timestamp.get(Calendar.DAY_OF_MONTH) + "-" + timestamp.get(Calendar.YEAR));
		System.out.println("\t" + timestamp.get(Calendar.HOUR_OF_DAY) + ":" + timestamp.get(Calendar.MINUTE) + ":" + timestamp.get(Calendar.SECOND));
		gui.printToGui("Current Time: " + timestamp.get(Calendar.MONTH) + "-" + timestamp.get(Calendar.DAY_OF_MONTH) + "-" + timestamp.get(Calendar.YEAR) + "\t" + timestamp.get(Calendar.HOUR_OF_DAY) + ":" + timestamp.get(Calendar.MINUTE) + ":" + timestamp.get(Calendar.SECOND));

	}
}
