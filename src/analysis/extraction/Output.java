package analysis.extraction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Vector;

import data.object.Node;
import data.param.ExParameter;


public class Output {
	static PrintStream OUT;
	
	public static void OutStandard(File output, Vector<Node> Features) throws FileNotFoundException {
        OUT = new PrintStream(output);
        for(int x = 0; x < Features.size(); x++) {
        	OUT.print(Features.get(x).getID() + "\t");
        	double[] temp = Features.get(x).getData();
    		for(int y = 0; y < temp.length; y++) {
    			if(y + 1 == temp.length) { OUT.println(temp[y]); }
            	else { OUT.print(temp[y] + "\t"); }
    		}
        }
        OUT.close();
	}
	
	public static void OutTreeview(File output, Vector<Node> Features) throws FileNotFoundException {
        OUT = new PrintStream(output);
        OUT.print("YORF\tNAME\tGWEIGHT\t");
        for(int x = 0; x < Features.get(0).getData().length; x++) {
        	if(x + 1 == Features.get(0).getData().length) { OUT.println(x); }
        	else { OUT.print(x + "\t"); }
        }
        for(int x = 0; x < Features.size(); x++) {
        	OUT.print(Features.get(x).getID() + "\t" + Features.get(x).getID() + "\t1\t");
        	double[] temp = Features.get(x).getData();
    		for(int y = 0; y < temp.length; y++) {
    			if(y + 1 == temp.length) { OUT.println(temp[y]); }
            	else { OUT.print(temp[y] + "\t"); }
    		}
        }
        OUT.close();
	}
	
	/*
	track type=wiggle_0 name="fixedStep" description="fixedStep format" visibility=full \
    autoScale=off viewLimits=0:1000 color=0,200,100 maxHeightPixels=100:50:20 \
    graphType=points priority=20
	fixedStep chrom=chr19 start=59307401 step=300 span=200
	1000
	*/
	public static void OutWIG(File output, Vector<Node> Features, ExParameter param) throws FileNotFoundException {
        OUT = new PrintStream(output);
    	OUT.println("track type=wiggle_0 visibility=full");

        for(int x = 0; x < Features.size(); x++) {
        	//String chromosome = ROMANIZE(Features.get(x).getChrom());
        	String chromosome = Features.get(x).getChrom();
        	int start = Features.get(x).getBP() - (param.getWindow() / 2);
        	OUT.println("fixedStep chrom=" + chromosome + " start=" + start + " step=" + param.getResolution() + " span=" + param.getResolution());
        	double[] temp = Features.get(x).getData();
    		for(int y = 0; y < temp.length; y++) {
    			OUT.println(temp[y]);
    		}
        }
        OUT.close();
	}
	
	/*private static String ROMANIZE(String chrom) {
		String[] RN = {"M", "CM", "D", "CD", "C", "XC", "L",  "XL", "X", "IX", "V", "IV", "I"};
		int[] value  = {1000, 900, 500, 400,  100,   90,  50, 40,   10,    9,   5,   4,    1};
		String NEWCHROM = "chr";
		int chromnum = Integer.parseInt(chrom.substring(3));
		for (int i = 0; i < RN.length; i++) {
            while (chromnum >= value[i]) {
            	chromnum -= value[i];
            	NEWCHROM  += RN[i];
            }
        }
        return NEWCHROM;
	}*/
}
