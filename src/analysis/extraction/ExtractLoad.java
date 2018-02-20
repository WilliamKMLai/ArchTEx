package analysis.extraction;
import gui.base.GUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import analysis.extraction.parser.CoordParser;
import analysis.extraction.parser.DefaultParser;
import analysis.extraction.parser.GFFParser;
import analysis.extraction.parser.RefGeneParser;

import components.CreateChartPanel;
import components.RawDataDisplayPanel;

import data.object.Node;
import data.param.ExParameter;

public class ExtractLoad extends Thread{
	protected GUI gui;
	protected ExParameter param;

	private double[] xData;
	private double[] yData;
	private double[] columnVar;
	
	public ExtractLoad(GUI gu, ExParameter par) {
		gui = gu;
		param = par;

	}
	
	public void run() {
		printTimeStamp(gui);
		System.out.println("-----------------------------------------\nCurrent File: " + param.getInputIndex(param.getExp()));
		gui.printToGui("-----------------------------------------\nCurrent File: " + param.getInputIndex(param.getExp()));	
		//Acquire Statistics from BAM file
		System.out.println("Genome Size: " + param.getTotalgenomeSize()[param.getExp()] + "\nTotal Tags: " + param.getTotaltagCount()[param.getExp()]);
		gui.printToGui("Genome Size: " + param.getTotalgenomeSize()[param.getExp()] + "\nTotal Tags: " + param.getTotaltagCount()[param.getExp()]);

		//Open file containing Genomic coordinates
		System.out.println("-----------------------------------------\nLoading Coordinates for Extraction...");
		gui.printToGui("-----------------------------------------\nLoading Coordinates for Extraction...");
		
		Vector<Node> NODES = new Vector<Node>();
		CoordParser parser = parserFactory(param.getInType());
		try { loadCoords(param.getCoord(), parser, NODES);
		} catch (FileNotFoundException e) {	e.printStackTrace(); }

		if(NODES.size() == 0) {
			System.out.println("No Coordinates Available!!!");
			gui.printToGui("No Coordinates Available!!!");
			System.exit(0);
		}
		System.out.println("Coordinates to Extract: " + NODES.size());
		gui.printToGui("Coordinates to Extract: " + NODES.size());
		
		if(param.getTrans().equals("zlog")) {
			param.setTrans("log2");
			StatGen.generate_LogStats(gui, param);
			param.setTrans("zlog");
		}
		
		//Load Features
		loadFeatures(param, NODES);
		
		//TODO reactivate output
		if(param.getOutType().equals("treeview")) {
			try {	Output.OutTreeview(param.getOutput(), NODES);	}
			catch (FileNotFoundException e) {	e.printStackTrace();	}
		} else if (param.getOutType().equals("wig")){
			try {	Output.OutWIG(param.getOutput(), NODES, param);	}
			catch (FileNotFoundException e) {	e.printStackTrace(); }
		} else {
			try {	Output.OutStandard(param.getOutput(), NODES);	}
			catch (FileNotFoundException e) {	e.printStackTrace();	}
		}

		//Calculate Average Here
		xData = new double[(param.getWindow() / param.getResolution()) + 1];
		yData = new double[(param.getWindow() / param.getResolution()) + 1];
		//Array to hold the column variances for the template
		columnVar = new double[yData.length];
		double[] avgweight = new double[yData.length];

		for(int x = 0; x < NODES.size(); x++) {
			for(int y = 0; y < yData.length; y++) {
				if(!Double.isNaN(NODES.get(x).getData()[y])) {
					yData[y] += NODES.get(x).getData()[y];
					avgweight[y]++;
				}
			}
		}
		for(int x = 0; x < xData.length; x++) {
			xData[x] = (x * param.getResolution()) - (param.getWindow() / 2);
			yData[x] /= NODES.size();
		}
		//Calculate sample column variance
		for(int x = 0; x < columnVar.length; x++) {
			for(int y = 0; y < NODES.size(); y++) {
				if(!Double.isNaN(NODES.get(y).getData()[x])) {
					columnVar[x] += Math.pow(NODES.get(y).getData()[x] - yData[x], 2);
				}
			}
			//Need to account for only loading 1 node as a template, variance == 0 (variance == (lowest theoretical)/(n-1))
			if(avgweight[x] == 1) columnVar[x] = 1;
			else if(columnVar[x] == 0) {
				double ratio = param.getTotaltagCount()[param.getExp()] / param.getStandardWindow();
				if(param.getExtension() != 0) ratio *= param.getExtension();
				columnVar[x] = (1 / ratio) / (avgweight[x] - 1);
			}
			else columnVar[x] /= (avgweight[x] - 1);
		}
		addPanes();
		
		System.out.println();
		
		printTimeStamp(gui);
	}
	
	public void addPanes() {
		CreateChartPanel panel = new CreateChartPanel();
		//TODO Variance appears here
		//gui.addExtraction(param.getInput().get(param.getExp()).getName(), panel.createXYPlot(xData, yData, param.getInput().get(param.getExp()).getName()));
		gui.addExtraction(param.getInput().get(param.getExp()).getName(), panel.createXYPlot(xData, yData, columnVar, param.getInput().get(param.getExp()).getName()));
		RawDataDisplayPanel raw = new RawDataDisplayPanel();
		raw.setAreaXY(xData, yData, columnVar);
		gui.addRawExtraction(param.getInput().get(param.getExp()).getName(), raw);
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
	
	private static void loadCoords(File coord, CoordParser parser, Vector<Node> features ) throws FileNotFoundException {
		Scanner scan = new Scanner(coord);
		while (scan.hasNextLine()) {
			String[] temp = scan.nextLine().split("\t");
			if(!temp[0].equals("name")) {
				if(parser.getDirection(temp).equals("+")) {
					features.add(new Node(parser.getId(temp),parser.getChrom(temp),parser.getStart(temp),true));
					//features.add(new Node(temp[0], temp[1], Integer.parseInt(temp[2]), true));
				}
				else {
					features.add(new Node(parser.getId(temp),parser.getChrom(temp),parser.getStart(temp),false));
					//features.add(new Node(temp[0], temp[1], Integer.parseInt(temp[2]), false));
				}
			}
		}
		scan.close();
	}

	private  CoordParser parserFactory(String referenceType){
		CoordParser parse;
		if (referenceType.toLowerCase().equals("refgene")
				|| referenceType.toLowerCase().equals("rg")) {
			parse = new RefGeneParser();
		} else if (referenceType.toLowerCase().equals("gff")) {
			parse = new GFFParser();
		} else {
			parse = new DefaultParser();
		}
		return parse;
	}
	
	private static void printTimeStamp(GUI gui) {
		Calendar timestamp = new GregorianCalendar();
		System.out.print("Current Time: " + timestamp.get(Calendar.MONTH) + "-" + timestamp.get(Calendar.DAY_OF_MONTH) + "-" + timestamp.get(Calendar.YEAR));
		System.out.println("\t" + timestamp.get(Calendar.HOUR_OF_DAY) + ":" + timestamp.get(Calendar.MINUTE) + ":" + timestamp.get(Calendar.SECOND));
		gui.printToGui("Current Time: " + timestamp.get(Calendar.MONTH) + "-" + timestamp.get(Calendar.DAY_OF_MONTH) + "-" + timestamp.get(Calendar.YEAR) + "\t" + timestamp.get(Calendar.HOUR_OF_DAY) + ":" + timestamp.get(Calendar.MINUTE) + ":" + timestamp.get(Calendar.SECOND));

	}
}
