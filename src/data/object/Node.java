package data.object;

public class Node {
	private String uniqID = "";
	private String CHROM = "";
	private int BP = 0;
	private boolean Fstrand = true;
	private double[] dataArray;
	private double totalRawTagCount = 0;
	
	public Node () {
		
	}
	
	public Node (String id, double[] input) {
		uniqID = id;
		dataArray = new double[input.length];
		for(int x = 0; x < input.length; x++){
			dataArray[x] = input[x];
		}
	}
	
	public Node (String id, String chr, int index, boolean strand) {
		uniqID = id;
		CHROM = chr;
		BP = index;
		Fstrand = strand;
	}
	
	public Node (String id, String chr, int index, boolean strand, double[] input) {
		uniqID = id;
		CHROM = chr;
		BP = index;
		Fstrand = strand;
		dataArray = new double[input.length];
		for(int x = 0; x < input.length; x++){
			dataArray[x] = input[x];
		}
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
	
	public int getBP() {
		return BP;		
	}
	
	public void setBP(int newbp) {
		BP = newbp;		
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
		dataArray = new double[newarray.length];
		for(int x = 0; x < newarray.length; x++){
			dataArray[x] = newarray[x];
		}
	}
	
	public double getRawTagCount() {
		return totalRawTagCount;
	}
	
	public void setRawTagCount(double totalRegionTags) {
		totalRawTagCount = totalRegionTags;
	}
}
