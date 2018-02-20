package data.param;

import gui.base.GUI;

import java.io.File;
import java.util.Vector;

import javax.swing.JOptionPane;


import net.sf.samtools.AbstractBAMFileIndex;
import net.sf.samtools.BAMIndexer;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMSequenceRecord;


public class ExParameter {
	//Coordinate File
	private File coord = null;		// Stores the name of coordinate file
	private String intype = "custom";	// Stores input type (refGene, GFF, custom)
	
	private File output = null;	// Stores the name of the output file
	private String outtype = "basic";	// Stores the type of output (basic, treeview, wig)

	//BAM Files and Accompanying indexes
	private Vector<File> input;	// Stores the names of input files
	private Vector<File> index;		// Stores the name of the index file
	
	//Extraction Parameters
	private int extension = 120; //Default set extension to 120
	private String transformation = "standard"; //Default set transformation to log2
	private String strand = "C";
	private int windowSize = 2000;	//Default set Window frame for each extraction to 2kb
	private int resolution = 10;	//Default set extraction resolution to 10bp
	private int iterations = 1;	//Default set number of random iterations for weighting to 1
	
	//Parameters controlling the transformations and the node sizes
	Vector<double[]> genomeSize; // Vector containing the sizes of each chromosome in each experiment
	Vector<double[]> tagCount; // Vector containing the tag count for each chromosome in each experiment
	double[] TotalgenomeSize; // Array containing total genome sizes for each experiment
	double[] TotaltagCount; // Array containing total tag counts for each experiment
	
	//Misc Parameters
	private int threads = 1;	//Default set number of cpu's to use to 1
	private int currentExp = 0;	//Default current experiment under analysis

	//Transformation Param
	private int standardize = 10000000;	//Default set standardization to 10 million tags
	private double logaverage = 0;	//Default set genome-wide log average to 0
	private double logvariance = 1;	//Default set genome-wide log variance to 1
	private int SDsize = 20;
	private int SDnum = 3;
	private int tagShift = 0;	//Default set tag shift to 0 bp (Useful for examining dyads ~73bp shift)
	private int condWindow = 140;	//Default set conditional positioning window to 140bp
	
	public ExParameter() {
		input = new Vector<File>();
		index = new Vector<File>();
		genomeSize = new Vector<double[]>();
		tagCount = new Vector<double[]>();
	}
	
	public int getCondWindow() {
		return condWindow;
	}
	
	public void setCondWindow(int newwind) {
		condWindow = newwind;
	}
	
	public int getTagShift() {
		return tagShift;
	}
	
	public void setTagShift(int newshift) {
		tagShift = newshift;
	}
	
	public int getSDSize() {
		return SDsize;
	}
	
	public void setSDSize(int newsize) {
		SDsize = newsize;
	}
	
	public int getSDNum() {
		return SDnum;
	}
	
	public void setSDNum(int newnum) {
		SDnum = newnum;
	}
	
	public double getLogAvg() {
		return logaverage;
	}
	
	public void setLogAvg(double newavg) {
		logaverage = newavg;
	}
	
	public double getLogVar() {
		return logvariance;
	}
	
	public void setLogVar(double newvar) {
		logvariance = newvar;
	}
	
	public void setStandardWindow(int newstand) {
		standardize = newstand;
	}
	
	public int getStandardWindow() {
		return standardize;
	}
	
	public void setExp(int newexp) {
		currentExp = newexp;
	}
	
	public int getExp() {
		return currentExp;
	}
	
	public String getInType() {
		return intype;
	}
	
	public void setInType(String type) {
		intype = type;
	}
	
	public String getOutType() {
		return outtype;
	}
	
	public void setOutType(String type) {
		outtype = type;
	}
	
	public File getOutput() {
		return output;
	}
	
	public void setOutput(File out) {
		output = out;
	}
	
	public Vector<File> getInput() {
		return input;		
	}
	
	public void setInput(Vector<File> newinput) {
		input = newinput;		
	}
	
	public File getInputIndex(int index) {
		return input.get(index);		
	}
	
	public void addInput(File newinput) {
		input.add(newinput);		
	}
	
	public Vector<File> getIndex() {
		return index;		
	}
	
	public void setIndex(Vector<File> newindex) {
		index = newindex;		
	}
	
	public File getIndexIndex(int in) {
		return index.get(in);		
	}
	
	public void addIndex(File newinput) {
		index.add(newinput);		
	}
	
	public Vector<double[]> getGenomeSize() {
		return genomeSize;
	}
	
	public double[] getgenomeSize_Index(int ind) {
		return genomeSize.get(ind);
	}
	
	public Vector<double[]> getTagCount() {
		return tagCount;
	}
	
	public double[] gettagCount_Index(int ind) {
		return tagCount.get(ind);
	}
	
	public double[] getTotalgenomeSize() {
		return TotalgenomeSize;
	}
	
	public double[] getTotaltagCount() {
		return TotaltagCount;
	}
	
	public File getCoord() {
		return coord;		
	}
	
	public void setCoord(File newcoord) {
		coord = newcoord;		
	}
	
	public int getWindow() {
		return windowSize;		
	}
	
	public void setWindow(int newwindow) {
		windowSize = newwindow;		
	}
	
	public int getResolution() {
		return resolution;		
	}
	
	public void setResolution(int newresolution) {
		resolution = newresolution;		
	}
	
	public int getIterations() {
		return iterations;		
	}
	
	public void setIterations(int newiter) {
		iterations = newiter;		
	}
	
	public int getThreads() {
		return threads;		
	}
	
	public void setThreads(int newthreads) {
		threads = newthreads;		
	}
	
	public int getExtension() {
		return extension;		
	}
	
	public void setExtension(int newext) {
		extension = newext;		
	}
	
	public String getTrans() {
		return transformation;		
	}
	
	public void setTrans(String newtrans) {
		transformation = newtrans;		
	}
	
	public String getStrand() {
		return strand;		
	}
	
	public void setStrand(String newstrand) {
		strand = newstrand;		
	}
	
	public String printStats() {
		String OUTPUT = "";
		if(input == null)	OUTPUT += "Input file: NONE\n";
		else {
			for(int x = 0; x < input.size(); x++) {
				OUTPUT += "Input file: " + input.get(x) + "\n";
			}
		}
		if(index == null)	OUTPUT += "Index file: NONE\n";
		else {
			for(int x = 0; x < index.size(); x++) {
				OUTPUT += "Index file: " + input.get(x) + "\n";
			}
		}
		OUTPUT += "Reference file: " + coord + "\n";
		OUTPUT += "Input type: " + intype + "\n";
		OUTPUT += "Input type: " + outtype + "\n";
		OUTPUT += "Window: " + windowSize + "bp" + "\n";
		OUTPUT += "Resolution: " + resolution + "bp" + "\n";
		OUTPUT += "Extension: " + extension + "bp" + "\n";
		OUTPUT += "Transformation: " + transformation + "\n";
		OUTPUT += "CPU Cores: " + threads;
		return OUTPUT;
	}

	public void generateStats() {
		//Initialize arrays to store statistics
		TotalgenomeSize = new double[input.size()];
		TotaltagCount = new double[input.size()];
		tagCount = new Vector<double[]>();
		genomeSize = new Vector<double[]>();
		
		//Load statistics into applicable arrays
		for(int x = 0; x < input.size(); x++) {
			SAMFileReader reader = new SAMFileReader(input.get(x), index.get(x));
			AbstractBAMFileIndex bamindex = (AbstractBAMFileIndex) reader.getIndex();
			
			genomeSize.add(new double[bamindex.getNumberOfReferences()]);
			tagCount.add(new double[bamindex.getNumberOfReferences()]);
			for (int y = 0; y < bamindex.getNumberOfReferences(); y++) {
				SAMSequenceRecord seq = reader.getFileHeader().getSequence(y);
				genomeSize.get(x)[y] = seq.getSequenceLength();
				tagCount.get(x)[y] = reader.getIndex().getMetaData(y).getAlignedRecordCount();
			}
			
			reader.close();
			bamindex.close();
		}
		
		//Check to make sure all experiments possess equally sized chromosomes
		for(int x = 0; x < TotalgenomeSize.length; x++) {
			for (int y = 0; y < genomeSize.get(x).length; y++) {
				for(int z = 0; z < TotalgenomeSize.length; z++) {
					if(genomeSize.get(x)[y] != genomeSize.get(z)[y]) {
						int chrom = (x + 1);
						String problem = "chr" + chrom;
						System.out.println("\nChromosome Sizes Unequal!!!");
						System.out.println("Genome: " + input.get(x).getName() + " Genome: " + input.get(z).getName() + "\nChromosome: " + problem);
						System.exit(0);
					}
				}
				TotalgenomeSize[x] += genomeSize.get(x)[y];
				TotaltagCount[x] += tagCount.get(x)[y];
			}
		}
		for(int x = 0; x < input.size(); x++) {
			System.out.println("Genome: " + input.get(x).getName());
			System.out.println("Size: " + TotalgenomeSize[x] + "\tTotal Tags: " + TotaltagCount[x]);
		}
	}
	
	public String[] getInputNames() {
		String[] names = new String[input.size()];
		for (int x = 0; x < names.length; x++) {
			names[x] = input.get(x).getName();
		}
		return names;
	}
	
	public String[] getIndexNames() {
		String[] names = new String[input.size()];
		for (int x = 0; x < names.length; x++) {
			names[x] = input.get(x).getName();
		}
		return names;
	}
	
	public String[] getChromNames() {
		SAMFileReader reader = new SAMFileReader(input.get(0), index.get(0));
		AbstractBAMFileIndex index = (AbstractBAMFileIndex) reader.getIndex();	
		String[] names = new String[index.getNumberOfReferences()];
		for (int x = 0; x < index.getNumberOfReferences(); x++) {
			SAMSequenceRecord seq = reader.getFileHeader().getSequence(x);
			names[x] = seq.getSequenceName();
		}
		reader.close();
		index.close();
		return names;
	}
	
	public int getChromSize(String chrom) {
		SAMFileReader reader = new SAMFileReader(input.get(0), index.get(0));
		AbstractBAMFileIndex index = (AbstractBAMFileIndex) reader.getIndex();	
		for (int x = 0; x < index.getNumberOfReferences(); x++) {
			SAMSequenceRecord seq = reader.getFileHeader().getSequence(x);
			if(seq.getSequenceName().equals(chrom)) {
				int size = seq.getSequenceLength();
				reader.close();
				return size;
			}
		}
		reader.close();
		index.close();
		return 0;
	}
	
	public  File generateIndex(File input, String output, GUI gui) {
		File retVal =null;
		System.out.println("Generating New Index File...");
		gui.printToGui("Generating New Index File...");
		try{
		retVal = new File(output);
		//Generate index
		SAMFileReader inputSam = new SAMFileReader(input);
		BAMIndexer bamindex = new BAMIndexer(retVal, inputSam.getFileHeader());
		inputSam.enableFileSource(true);
		int counter = 0;
		for(SAMRecord record : inputSam) {
			if(counter % 1000000 == 0) {
				System.out.print("Tags processed: " + counter + "\r");
				gui.printToGui("Tags processed: " + counter);
				System.out.flush();
			}
			counter++;
			bamindex.processAlignment(record);
		}
		bamindex.finish();
		inputSam.close();
		System.out.println("\nIndex File Generated");
		gui.printToGui("Index File Generated");
		return retVal;
		}
		catch(net.sf.samtools.SAMException exception){
			JOptionPane.showMessageDialog(null, "You do not have proper file permissions in this directory to write files");
			retVal = null;
		}
		return retVal;
	}
}