package analysis.extraction.parser;
/**
 * This class is implemented by concrete parsers. Basically each implementation
 * is responsible for returning specific information from a coordinate reference file.
 * @author jbard
 *
 */
public interface CoordParser {
	

	/**
	 * Return directionality of the node
	 * @param temp
	 * @return
	 */
	public String getDirection(String[] temp);

	/**
	 * Returns the "start, or center" location of the node, ie where the window
	 * will be centered around
	 * @param temp
	 * @return
	 */
	public int getStart(String[] temp) ;

	/**
	 * Returns which chromosome the node is on
	 * @param temp
	 * @return
	 */
	public String getChrom(String[] temp) ;

	/**
	 * Returns which possible gene it is on.. not all implentations will have this information
	 * @param temp
	 * @return
	 */
	public String getGene(String[] temp) ;

	/**
	 * Returns the id of the node. not all implentations can support this either.
	 * @param temp
	 * @return
	 */
	public String getId(String[] temp);

}
