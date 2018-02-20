package analysis.gencorr;

import data.param.GenParameter;

public class GenTransform {
	GenParameter Parameters;
	int expInd = 0;
	
	public GenTransform() {
		
	}
	
	public GenTransform(GenParameter param) {
		Parameters = param;
		expInd = param.getExp();
	}
	
	public GenTransform(GenParameter param, int exp) {
		Parameters = param;
		expInd = exp;
	}
	
	public double[] transformData(double[] orig) {
		if(Parameters == null) {
			System.out.println("Transformation Object never initialized!!!");
			System.exit(0);
		}
		else if(Parameters.getTrans().equals("log2")) {
			return log2Tran(orig);
		}
		else if(Parameters.getTrans().equals("zlog")) {
			return zlogTran(orig);
		}
		else if(Parameters.getTrans().equals("standard")) {
			return standTran(orig);
		}
		else if(Parameters.getTrans().equals("squareroot")) {
			return squareTran(orig);
		}
		else if(Parameters.getTrans().equals("poisson")) {
			return poissonTran(orig);
		}
		else if(Parameters.getTrans().equals("gauss")) {
			return gaussTran(orig);
		}
		else if(Parameters.getTrans().equals("condpos")) {
			return condTran(orig);
		}
		return orig;
	}

	private double[] log2Tran(double[] orig) {
		double[] logvalue = new double[orig.length];
		double avgtag = Parameters.getTotaltagCount()[expInd] / Parameters.getTotalgenomeSize()[expInd];
		//double NaNavgtag = (Parameters.getTotaltagCount()[expInd] + 1) / Parameters.getTotalgenomeSize()[expInd];
		if(Parameters.getExtension() != 0) {
			avgtag *= Parameters.getExtension();
			//NaNavgtag *= Parameters.getExtension();
		}
		for(int x = 0; x < orig.length; x++) {
			if(orig[x] == 0) logvalue[x] = Math.sqrt(-1);
			//if(orig[x] == 0) logvalue[x] = Math.log(1 / NaNavgtag) / Math.log(2);
			else logvalue[x] = Math.log(orig[x]/ avgtag) / Math.log(2);
		}
		return logvalue;
	}
	
	private double[] zlogTran(double[] orig) {
		double[] logvalue = new double[orig.length];
		double avgtag = Parameters.getTotaltagCount()[expInd] / Parameters.getTotalgenomeSize()[expInd];
		double NaNavgtag = (Parameters.getTotaltagCount()[expInd] + 1) / Parameters.getTotalgenomeSize()[expInd];
		
		double logavg = Parameters.getLogAvg();
		double sd = Math.sqrt(Parameters.getLogVar());
		
		if(Parameters.getExtension() != 0) {
			avgtag *= Parameters.getExtension();
			NaNavgtag *= Parameters.getExtension();
		}
		for(int x = 0; x < orig.length; x++) {
			if(orig[x] == 0) logvalue[x] = Math.log(1 / NaNavgtag) / Math.log(2);
			else logvalue[x] = Math.log(orig[x]/ avgtag) / Math.log(2);
			logvalue[x] = (logvalue[x] - logavg) / sd;
		}
		return logvalue;
	}
	
	private double[] standTran(double[] orig) {
		double[] STDvalue = new double[orig.length];
		double ratio = Parameters.getTotaltagCount()[expInd] / Parameters.getStandardWindow();
		if(Parameters.getExtension() != 0) ratio *= Parameters.getExtension();
		for(int x = 0; x < orig.length; x++) {
			STDvalue[x] = orig[x] / ratio;
		}
		return STDvalue;
	}
	
	private double[] squareTran(double[] orig) {
		double[] SQvalue = new double[orig.length];
		for(int x = 0; x < orig.length; x++) {
			if(orig[x] < 0) SQvalue[x] = Math.sqrt(-1);
			else SQvalue[x] = Math.sqrt(orig[x]);
		}
		return SQvalue;
	}
	
	private double[] poissonTran(double[] orig) {
		//f(n, l) = (l^n)(e^-l)/(n!) for every number 0 to n.
		//f(k, l) = k*ln(l) - l - sum(ln(i)) from i = 1 to k
		double[] Pvalue = new double[orig.length];
		double bpProbability = (Parameters.getTotaltagCount()[expInd]) / Parameters.getTotalgenomeSize()[expInd];
		if(Parameters.getExtension() != 0) bpProbability *= Parameters.getExtension();
		for(int x = 0; x < Pvalue.length; x++) {
			for(int xi = 0; xi < orig[x]; xi++) {
				double sumI = 0;
				for(int i = 1; i <= xi; i++) sumI += Math.log(i);
				Pvalue[x] += Math.exp((xi * Math.log(bpProbability)) - bpProbability - sumI);
			}	
		}
		return Pvalue;
	}
	
	private double[] gaussTran(double[] orig) {
		double[] Garray = new double[orig.length];
		int window = (Parameters.getSDSize() * Parameters.getSDNum()) / Parameters.getResolution();
		double SDSize = (double)Parameters.getSDSize();
		for(int x = 0; x < orig.length; x++) {
             double score = 0;
             double weight = 0;
             for(int y = x - window; y <= x + window; y++) {
                    if(y < 0) y = -1;
                    else if(y < orig.length) {
                    	double HEIGHT = Math.exp(-1 * Math.pow((y - x), 2) / (2 * Math.pow(SDSize, 2)));
                    	score += (HEIGHT * orig[y]);
                    	weight += HEIGHT;
                    } else y = x + window + 1;
             }
             if(weight != 0) Garray[x] = score / weight;
		}
		return Garray;
     }
	
	private double[] condTran(double[] orig) {
		double[] Garray = gaussTran(orig);
		double[] Carray = new double[Garray.length];
		int Cwindow = Parameters.getCondWindow() / (2 * Parameters.getResolution());
		for(int x = 0; x < orig.length; x++) {
            double weight = 0;
            for(int y = x - Cwindow; y <= x + Cwindow; y++) {
                   if(y < 0) y = -1;
                   else if(y < orig.length) weight += Garray[y];
                   else y = x + Cwindow + 1;
            }
            Carray[x] = Garray[x] / weight;
		}		
		return Carray;
	}
}
