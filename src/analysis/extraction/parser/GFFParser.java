package analysis.extraction.parser;


//seqname - The name of the sequence. Must be a chromosome or scaffold.
//source - The program that generated this feature.
//feature - The name of this type of feature. Some examples of standard feature types are "CDS", "start_codon", "stop_codon", and "exon".
//start - The starting position of the feature in the sequence. The first base is numbered 1.
//end - The ending position of the feature (inclusive).
//score - A score between 0 and 1000. If the track line useScore attribute is set to 1 for this annotation data set, the score value will determine the level of gray in which this feature is displayed (higher numbers = darker gray). If there is no score value, enter ".".
//strand - Valid entries include '+', '-', or '.' (for don't know/don't care).
//frame - If the feature is a coding exon, frame should be a number between 0-2 that represents the reading frame of the first base. If the feature is not a coding exon, the value should be '.'.
//group - All lines with the same group are linked together into a single item.
public class GFFParser implements CoordParser {
	
	@Override
	public String getDirection(String[] temp) {
		// TODO Auto-generated method stub
		return temp[7];
	}

	@Override
	public int getStart(String[] temp) {
		//chr1.fa 2369880-2369980
		String s = temp[0];
		int start = Integer.parseInt(s.substring(s.indexOf(" ")+1,s.indexOf("-")));
		start = start + Integer.parseInt(temp[3]);
		return start;
	}

	@Override
	public String getChrom(String[] temp) {
		// TODO Auto-generated method stub
		String s = temp[0];
		String [] retVal = s.split(" ");
		String chrom = retVal[0];
		//chr1.fa
		chrom = chrom.substring(3,chrom.indexOf("."));
		int i = Integer.parseInt(chrom);
		if (i < 10){
			return "chr"+i;
		}else{
			return "chr"+i;
		}
	}

	@Override
	public String getGene(String[] temp) {
		// TODO Auto-generated method stub
		return temp[0];
	}

	@Override
	public String getId(String[] temp) {
		// TODO Auto-generated method stub
		return temp[0];
	}
}
