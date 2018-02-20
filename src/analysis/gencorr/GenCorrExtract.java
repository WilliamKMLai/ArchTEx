package analysis.gencorr;

import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import data.param.GenParameter;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.util.CloseableIterator;

public class GenCorrExtract implements Runnable {
	final Lock lock = new ReentrantLock();
	final Condition inUse  = lock.newCondition(); 
	
	GenParameter Parameters;
	Vector<GenCorrNode> ALLNodes;	
	
	int INDEX;
	int SUBSETSIZE;
	
	static int progressCounter = 0;
	static int currentProgress = 0;
	
	SAMFileReader inputSam;
	
	public void run() {
		for(int x = INDEX; x < INDEX + SUBSETSIZE; x++) {
			inputSam = new SAMFileReader(Parameters.getInput().get(Parameters.getPrimeExp()), Parameters.getIndex().get(Parameters.getPrimeExp()));
			ALLNodes.get(x).addData(extractRegion(ALLNodes.get(x), Parameters, Parameters.getPrimeExp()));
			inputSam.close();

			inputSam = new SAMFileReader(Parameters.getInput().get(Parameters.getSecExp()), Parameters.getIndex().get(Parameters.getSecExp()));
			ALLNodes.get(x).addData(extractRegion(ALLNodes.get(x), Parameters, Parameters.getSecExp()));
			inputSam.close();
			
			//After tag data is added, calculate out statistics for the forward and reverse tags in the node
			double[] Ftag = ALLNodes.get(x).getData().get(0);
			double[] Rtag = ALLNodes.get(x).getData().get(1);
			
			//System.out.println(Ftag.length + "\t" + Rtag.length);
			double count = 0, sx = 0, sxx = 0, sy = 0, syy = 0, sxy = 0;
			for(int winIndex = 0; winIndex < Ftag.length; winIndex++) {
				if(!Double.isNaN(Ftag[winIndex]) && !Double.isInfinite(Ftag[winIndex]) && !Double.isNaN(Rtag[winIndex]) && !Double.isInfinite(Rtag[winIndex])) {
					count++;
					sx += Ftag[winIndex];
					sxx += (Ftag[winIndex] * Ftag[winIndex]);
					sy += Rtag[winIndex];
					syy += (Rtag[winIndex] * Rtag[winIndex]);
					sxy += (Ftag[winIndex] * Rtag[winIndex]);
				}
			}
			
			ALLNodes.get(x).setCount(count);
			ALLNodes.get(x).setSx(sx);
			ALLNodes.get(x).setSxx(sxx);
			ALLNodes.get(x).setSy(sy);
			ALLNodes.get(x).setSyy(syy);
			ALLNodes.get(x).setSxy(sxy);
			
			//Remove residual tag data after analysis to conserve memory
			ALLNodes.get(x).setData(null);
			
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
	}	
	
	public GenCorrExtract() {
		
	}
	
	public GenCorrExtract(GenParameter param, Vector<GenCorrNode> nodes, int current, int size) {
		Parameters = param;
		ALLNodes = nodes;
		INDEX = current;
		SUBSETSIZE = size;
	}

	public double[] extractRegion(GenCorrNode current, GenParameter param, int expInd) {
		int WINDOW = current.getStop() - current.getStart();
		if(WINDOW < 1) {
			System.out.println(WINDOW + "\t" + current.getStop() + "\t" + current.getStart() + "\t" + param.getResolution());
		}
		double[] retVal = new double[(WINDOW / param.getResolution()) + 1];
		String CHROM = current.getChrom();
		int START = current.getStart();
		int STOP = current.getStop();
		int EXTENSION = param.getExtension();
		int TAGSHIFT = param.getTagShift();
		int RESOLUTION = param.getResolution();
		int GWINDOW = 0;
		
		if(param.getTrans().equals("gauss")) {
			GWINDOW = ((param.getSDNum() * param.getSDSize()) * 2) / param.getResolution(); 
			retVal = new double[retVal.length + GWINDOW];
		} else if(param.getTrans().equals("condpos")) {
			GWINDOW = (((param.getSDNum() * param.getSDSize()) * 2) + param.getCondWindow()) / param.getResolution();
			retVal = new double[retVal.length + GWINDOW];
		}
		
		//Create an iterator for every tag overlapping this region.
		try {
			CloseableIterator<SAMRecord> iter = inputSam.query(CHROM, START - EXTENSION - TAGSHIFT - 1, STOP + EXTENSION + TAGSHIFT + 1, false);
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
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("Exception caught");
		}
		
		GenTransform data = new GenTransform(Parameters, expInd);
		if(param.getTrans().equals("gauss") || param.getTrans().equals("condpos")) {
			retVal = data.transformData(retVal);
			double[] finalArray = new double[WINDOW];
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