package analysis.extraction;

import java.util.Vector;

import data.object.GenomeNode;
import data.param.ExParameter;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.util.CloseableIterator;

public class GenomeExtract implements Runnable {
	ExParameter Parameters;
	Vector<GenomeNode> ALLNodes;
	
	int INDEX;
	int SUBSETSIZE;
	
	static int progressCounter = 0;
	static int currentProgress = 0;
	
	SAMFileReader inputSam;
	
	public void run() {
		inputSam = new SAMFileReader(Parameters.getInput().get(Parameters.getExp()), Parameters.getIndex().get(Parameters.getExp()));
		for(int x = INDEX; x < INDEX + SUBSETSIZE; x++) {
			ALLNodes.get(x).setData(extractRegion(ALLNodes.get(x), Parameters));
						
			progressCounter++;
			int current = ((int)(0.5 + ((double)progressCounter / (double)ALLNodes.size() * 100)));
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
	
	public GenomeExtract() {
		
	}
	
	public GenomeExtract(ExParameter param, Vector<GenomeNode> nodes, int current, int size) {
		Parameters = param;
		ALLNodes = nodes;
		INDEX = current;
		SUBSETSIZE = size;
	}

	public double[] extractRegion(GenomeNode current, ExParameter param) {
		int WINDOW = current.getStop() - current.getStart();
		if(WINDOW < 1) {
			System.out.println(WINDOW + "\t" + current.getStop() + "\t" + current.getStart() + "\t" + param.getResolution());
		}
		double[] retVal = new double[WINDOW + 1];
		String CHROM = current.getChrom();
		int START = current.getStart();
		int STOP = current.getStop();
		int EXTENSION = param.getExtension();
		int RESOLUTION = 1;
		int TAGSHIFT = param.getTagShift();
		
		//Create an iterator for every tag overlapping this region.
		try {
			CloseableIterator<SAMRecord> iter = inputSam.query(CHROM, START - EXTENSION - 1, STOP + EXTENSION + 1, false);
			/* Iterate through the records that overlap this region. */
			while (iter.hasNext()) {
				/* Create the record object */
				SAMRecord sr = iter.next();
				/* Get the start of the record */
				int recordStart = sr.getAlignmentStart();
				int windowMin = START;
				int windowMax = STOP;
				
				if (sr.getReadNegativeStrandFlag()) {
					recordStart -= TAGSHIFT;
					if(!Parameters.getStrand().toUpperCase().equals("F")) {
						recordStart = recordStart + sr.getReadLength() - 1;
						for(int i = recordStart; i >= recordStart - EXTENSION; i--){
							if(i - windowMin >= 0 && i <= windowMax && (i - START) % RESOLUTION == 0) {
								retVal[(i - windowMin) / RESOLUTION]++;
							}
						}
					}
				}
				else {
					recordStart += TAGSHIFT;
					if(!Parameters.getStrand().toUpperCase().equals("R")){
						for(int i = recordStart; i <= recordStart + EXTENSION; i++){
							if(i - windowMin >= 0 && i <= windowMax && (i - START) % RESOLUTION == 0) {
								retVal[(i - windowMin) / RESOLUTION]++;
							}
						}
					}
				}
			}
			iter.close();
			if(!current.getStrand()) {
				double[] reversed = new double[(param.getWindow() / param.getResolution()) + 1];
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
		return data.transformData(retVal);
	}
	
	public static void resetProgress() {
		progressCounter = 0;
		currentProgress = 0;
	}
}