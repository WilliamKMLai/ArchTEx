package components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CreateChartPanel {

	public CreateChartPanel() {
		
	}
	
	public ChartPanel createBarChart(Vector<Double> x, Vector<String> names) {
		// Create a simple Bar chart
        /*DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.setValue(6, "Profit", "Jane");
        dataset.setValue(7, "Profit", "Tom");
        dataset.setValue(8, "Profit", "Jill");
        dataset.setValue(5, "Profit", "John");
        dataset.setValue(12, "Profit", "Fred");
        JFreeChart chart = ChartFactory.createBarChart("Comparison between Salesman",
                "Salesman", "Profit", dataset, PlotOrientation.VERTICAL, false,
                true, false);*/
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(int i = 0; i < x.size(); i++) {
			dataset.setValue(x.get(i).doubleValue(), "Correlation", names.get(i));
		}
		final JFreeChart chart = createChart(dataset);		
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		return chartPanel;				
	}
	
	private JFreeChart createChart(final CategoryDataset dataset) {
        final JFreeChart chart = ChartFactory.createBarChart3D(
            "Genome-Genome Correlations",      // chart title
            "Genome-Genome",               // domain axis label
            "Correlations",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );

        final CategoryPlot plot = chart.getCategoryPlot();
        final CategoryAxis Xaxis = plot.getDomainAxis();
        Xaxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
       /* axis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 8.0)
        )*/;
        final BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        return chart;
    }

	public ChartPanel createXYPlot(double[] x, double[] y,String title){
		final XYSeries series1 = new XYSeries("Data");
		for (int i = 0; i < x.length; i++) {
			series1.add(x[i], y[i]);
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		final JFreeChart chart = createChart(dataset, 1,title);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		return chartPanel;
	}

	public ChartPanel createXYPlot(double[] x, double[] y1, double[] y2,String title){
		final XYSeriesCollection dataset = new XYSeriesCollection();
		final XYSeries series1 = new XYSeries("Feature");
		for (int i = 0; i < y1.length; i++) {
			series1.add(x[i], y1[i]);
		}
		final XYSeries series2 = new XYSeries("Variance");
		for (int i = 0; i < y2.length; i++) {
			series2.add(x[i], y2[i]);
		}
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		
		final JFreeChart chart = createChart(dataset, 2,title);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		return chartPanel;
	}
	
	public ChartPanel createXYPlot(double[] x, Vector<double[]> data, Vector<File> exp){
		final XYSeriesCollection dataset = new XYSeriesCollection();
		for(int i = 0; i < data.size(); i++) {
			final XYSeries series = new XYSeries(exp.get(i).getName());
			double[] y = data.get(i);
			for (int j = 0; j < y.length; j++) {
				series.add(x[j], y[j]);
			}
			dataset.addSeries(series);
		}
		
		final JFreeChart chart = createChart(dataset, data.size(), "Site Data");
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		return chartPanel;
	}
	
	private JFreeChart createChart(final XYDataset dataset, int numseries,String title) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createXYLineChart(
				title, // chart title
				"Distance from Feature (bp)", // x axis label
				"Score", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		chart.setBackgroundPaint(Color.white);
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.black);
		plot.setRangeGridlinePaint(Color.black);
		
		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for(int x = 0; x < numseries; x++) {
			renderer.setSeriesLinesVisible(x, true);	//Spline visibility
			renderer.setSeriesShapesVisible(x, false);	//Data point dot visibility
			renderer.setSeriesStroke(x, new BasicStroke(3));
		}
		plot.setRenderer(renderer);
		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		final ValueAxis domainAxis = (ValueAxis) plot.getDomainAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		domainAxis.setAxisLineVisible(true);
		rangeAxis.setAxisLineVisible(true);
		return chart;
	}
}