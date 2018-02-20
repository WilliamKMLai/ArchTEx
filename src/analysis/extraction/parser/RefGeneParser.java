package analysis.extraction.parser;


/**
 * Handles the parsing of refGene.txt files
 * 
 * @author jbard
 * 
 */
public class RefGeneParser implements CoordParser {

	@Override
	public String getChrom(String [] temp){
		String chrom = temp[2];
		return chrom;
	}
	@Override
	public String getGene(String [] temp){
		String gene = temp[12];
		return gene;
	}
	@Override
	public String getDirection(String [] temp){
		String direction = temp[3];
		return direction;
	}
	@Override
	public int getStart(String [] temp){
		int tssStart = Integer.parseInt(temp[4]);
		return tssStart;
	}
	@Override
	public String getId(String [] temp){
		String id = temp[1];
		return id;
	}

}
