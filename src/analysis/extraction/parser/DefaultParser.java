package analysis.extraction.parser;

public class DefaultParser implements CoordParser {

	@Override
	public String getDirection(String[] temp) {
		// TODO Auto-generated method stub
		return temp[3];
	}

	@Override
	public int getStart(String[] temp) {
		// TODO Auto-generated method stub
		return Integer.parseInt(temp[2]);
	}

	@Override
	public String getChrom(String[] temp) {
		// TODO Auto-generated method stub
		return temp[1];
	}

	@Override
	public String getGene(String[] temp) {
		// TODO Auto-generated method stub
		return temp[0];
	}


	@Override
	public String getId(String[] temp) {
		return temp[0];
	}

}
