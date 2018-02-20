package data.object;

public class GenomeNode {
	private String uniqID = "";
	private String CHROM = "";
	private int BP_START = 0;
	private int BP_STOP = 0;
	private boolean Fstrand = true;
	private double[] dataArray;
	public double SCORE = 0;
	
	public GenomeNode () {
		
	}

	public GenomeNode (String chr, int start, int stop) {
		CHROM = chr;
		BP_START = start;
		BP_STOP = stop;
	}
	
	public double getScore() {
		return SCORE;
	}
	
	public void setScore(double newscore) {
		SCORE = newscore;
	}
	
	public String getID() {
		return uniqID;		
	}
	
	public void setID(String newid) {
		uniqID = newid;		
	}
	
	public String getChrom() {
		return CHROM;		
	}
	
	public void setChrom(String newchr) {
		CHROM = newchr;		
	}
		
	public int getStop() {
		return BP_STOP;		
	}
	
	public void setStop(int newbp) {
		BP_STOP = newbp;		
	}
	
	public int getStart() {
		return BP_START;		
	}
	
	public void setStart(int newbp) {
		BP_START = newbp;		
	}
	
	public boolean getStrand() {
		return Fstrand;		
	}
	
	public void setStrand(boolean newstrand) {
		Fstrand = newstrand;		
	}
	
	public double[] getData() {
		return dataArray;
	}
	
	public void setData(double[] newarray) {
		dataArray = newarray;
	}
}