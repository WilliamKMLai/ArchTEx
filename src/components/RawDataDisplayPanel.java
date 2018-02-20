package components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class RawDataDisplayPanel extends JPanel {
	JTextArea area;
	JTable table;
	JScrollPane pane;

	public RawDataDisplayPanel(Vector<Double> x, String[] y) {
		table = new JTable(y.length, y.length);
		table.setName("Correlation Matrix");
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		int counter = 0;
		for(int i = 0; i < y.length; i++) {
			for(int j = 0; j < y.length; j++) {
				if(i == j) table.setValueAt(1, i, j);
				else if((i - j) >= 1) {
					table.setValueAt(x.get(counter), i, j);
					table.setValueAt(x.get(counter), j, i);
					counter++;
				}
			}
		}
		for(int i = 0; i < y.length; i++) table.getColumnModel().getColumn(i).setHeaderValue(y[i]);
		table.setPreferredSize(table.getPreferredSize());
		pane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		pane.setPreferredSize(new Dimension(590, 590));
		add(pane, BorderLayout.CENTER);
	}
	
	public RawDataDisplayPanel() {
		area = new JTextArea();
		//area = new JTextArea("", 1100, 590);
		area.setLineWrap(false);
		area.setPreferredSize(area.getPreferredSize());
		pane = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.setPreferredSize(new Dimension(590, 590));
		area.setMargin(new Insets(50,50,50,50));
		add(pane, BorderLayout.CENTER);
	}

	public RawDataDisplayPanel(double[] x, Vector<double[]> y) {
		area = new JTextArea();
		area.setLineWrap(false);
		area.setColumns(y.size() + 1);
		for (int i = 0; i < x.length; i++) {
			area.append((int)x[i] + "\t"); 
			for(int j = 0; j < y.size(); j++) {
				area.append(y.get(j)[i] + "\t");
			}
			area.append("\n");
		}
		area.setRows(x.length);
		area.setMargin(new Insets(50,50,50,50));
		area.setPreferredSize(area.getPreferredSize());
		pane = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.setPreferredSize(new Dimension(590, 590));
		add(pane, BorderLayout.CENTER);
	}

	
	public void setAreaXY(double[] x, double[] y){
		area.setColumns(3);
		for (int i = 0; i < x.length; i++) {
			area.append((int)x[i] + "\t" + y[i] +"\n");
		}
		area.setRows(x.length);
	}
	
	public void setAreaXY(double[] x, double[] y1, double[] y2){
		area.setColumns(4);
		for (int i = 0; i < x.length; i++) {
			area.append((int)x[i] + "\t" + y1[i] + "\t" + y2[i] + "\n");
		}
		area.setRows(x.length);
	}
	
	public void setAreaXY(double[] x, Vector<double[]> y){
		area.setColumns(y.size() + 1);
		for (int i = 0; i < x.length; i++) {
			area.append((int)x[i] + "\t"); 
			for(int j = 0; j < y.size(); j++) {
				area.append(y.get(j)[i] + "\t");
			}
			area.append("\n");
		}
		area.setRows(x.length);
	}
}
