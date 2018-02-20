package analysis.extraction;
import java.util.Vector;

import data.object.Node;
import data.param.ExParameter;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.util.CloseableIterator;

public class Extract implements Runnable {
	ExParameter Parameters;
	Vector<Node> NODES;
	int index;
	int subsetsize;
	static int progressCounter = 0;
	static int currentProgress = 0;
	SAMFileReader inputSam;
	
	public void run() {
		inputSam = new SAMFileReader(Parameters.getInput().get(Parameters.getExp()), Parameters.getIndex().get(Parameters.getExp()));
		for(int x = index; x < index + subsetsize; x++) {
			NODES.get(x).setData(extractRegion(NODES.get(x), Parameters));
			progressCounter++;
			int current = ((int)(0.5 + ((double)progressCounter / (double)NODES.size() * 100)));
			if(current > currentProgress) {
				currentProgress = current;
				System.out.print(current + "%" + "\t0 {");
				for(int bar = 1; bar <= 20; bar++) {
					if(current >= bar * 5) { System.out.print("="); }
					else { System.out.print(" "); }
				}
				System.out.print("} 100\r");
				System.out.flush();
			}
		}
		inputSam.close();
	}	
	
	public Extract() {
		
	}
	
	public Extract(ExParameter param, Vector<Node> allnodes, int current, int length) {
		Parameters = param;
		NODES = allnodes;
		index = current;
		subsetsize = length;
	}

	public double[] extractRegion(Node current, ExParameter param) {
		String CHROM = current.getChrom();
		int INDEX = current.getBP();
		int EXTENSION = param.getExtension();
		int RESOLUTION = param.getResolution();
		int TAGSHIFT = param.getTagShift();
		int half = param.getWindow() / 2;
		int GWINDOW = 0;
		
		double[] retVal;
		if(param.getTrans().equals("gauss")) {
			GWINDOW = ((param.getSDNum() * param.getSDSize()) * 2) / param.getResolution(); 
			retVal = new double[(param.getWindow() / param.getResolution()) + 1 + GWINDOW];
			half += (param.getSDNum() * param.getSDSize());
		} else if(param.getTrans().equals("condpos")) {
			GWINDOW = (((param.getSDNum() * param.getSDSize()) * 2) + param.getCondWindow()) / param.getResolution();
			retVal = new double[(param.getWindow() / param.getResolution()) + 1 + GWINDOW];
			half += (param.getSDNum() * param.getSDSize()) + (param.getCondWindow() / 2);
		}
		else retVal = new double[(param.getWindow() / param.getResolution()) + 1];
		
		//Create an iterator for every tag overlapping this region.
		try {
			CloseableIterator<SAMRecord> iter = inputSam.query(CHROM, INDEX - half - EXTENSION - TAGSHIFT - 1, INDEX + half + EXTENSION + TAGSHIFT + 1, false);
			/* Iterate through the records that overlap this region. */
			while (iter.hasNext()) {
				/* Create the record object */
				SAMRecord sr = iter.next();
				/* Get the start of the record */
				int recordStart = sr.getAlignmentStart();
				int windowMin = INDEX - half;
				int windowMax = INDEX + half;
				if (sr.getReadNegativeStrandFlag()) {
					recordStart -= TAGSHIFT;
					if(!Parameters.getStrand().toUpperCase().equals("F")) {
						recordStart = recordStart + sr.getReadLength() - 1;
						for(int i = recordStart; i >= recordStart - EXTENSION; i--){
							if(i - windowMin >= 0 && i <= windowMax && (i - INDEX) % RESOLUTION == 0) {
								retVal[(i - windowMin) / RESOLUTION]++;
							}
						}
					}
				}
				else {
					recordStart += TAGSHIFT;
					if(!Parameters.getStrand().toUpperCase().equals("R")){
						for(int i = recordStart; i <= recordStart + EXTENSION; i++){
							if(i - windowMin >= 0 && i <= windowMax && (i - INDEX) % RESOLUTION == 0) {
								retVal[(i - windowMin) / RESOLUTION]++;
							}
						}
					}
				}
			}
			iter.close();
			if(!current.getStrand()) {
				double[] reversed = new double[(param.getWindow() / param.getResolution()) + 1 + GWINDOW];
				for(int x = retVal.length - 1; x >= 0; x--) {
					reversed[retVal.length - 1 - x] = retVal[x];
				}
				for(int x = 0; x < retVal.length; x++) {
					retVal[x] = reversed[x];
				}
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("Exception caught");
		}
		
		Transform data = new Transform(Parameters);
		if(param.getTrans().equals("gauss") || param.getTrans().equals("condpos")) {
			retVal = data.transformData(retVal);
			double[] finalArray = new double[(param.getWindow() / param.getResolution()) + 1];
			int indexShift = (param.getSDNum() * param.getSDSize()) / param.getResolution();
			if(param.getTrans().equals("condpos")) indexShift += (param.getCondWindow() / (param.getResolution() * 2));
			for(int x = 0; x < finalArray.length; x++) {
				finalArray[x] = retVal[x + indexShift];
			}
			return finalArray;
		}
		else return data.transformData(retVal);
	}
	
	public static void resetProgress() {
		progressCounter = 0;
		currentProgress = 0;
	}
}
