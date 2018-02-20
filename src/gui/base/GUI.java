package gui.base;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import components.RawDataDisplayPanel;
import data.param.CorrParameter;
import data.param.ExParameter;
import data.param.GenParameter;

public class GUI implements ActionListener {
	int corrCount = 0;
	private JFrame frame = new JFrame("ArchTEx");
	private JMenuBar bar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File"); // File Entry on Menu bar
	private JMenu menuHelp = new JMenu("Help"); // Help Menu entry
	private JMenu menuView = new JMenu("View"); // Param menu entry
	private JMenu menuAnalysis = new JMenu("Analysis");
	
	//Loads up all the menu items
	private JMenuItem menuItemQuit = new JMenuItem("Quit"); // Quit sub item
	private JMenuItem menuItemAbout = new JMenuItem("About"); // About sub item
	private JMenuItem menuItemViewCorr = new JMenuItem("View Correlations");
	private JMenuItem menuItemViewExtraction = new JMenuItem("View Extractions");
	private JMenuItem menuItemViewGenCorr = new JMenuItem("View Genome-Genome Correlations");
	private JMenuItem menuItemViewNone = new JMenuItem("View No Experiements");
	private JMenuItem menuItemCorrelation = new JMenuItem("Cross-Correlation");
	private JMenuItem menuItemExtraction = new JMenuItem("Mass Data Extraction");
	private JMenuItem menuItemSingleSiteExtraction = new JMenuItem("Single Site Extraction");
	private JMenuItem menuItemGenomeCorrelation = new JMenuItem("Genome-Genome Correlation");
	
	private JTabbedPane correlationTabbedPane;
	private JTabbedPane correlationPlotsTabbedPane;
	private JTabbedPane correlationRawTabbedPane;
	
	private JTabbedPane extractionTabbedPane;
	private JTabbedPane extractionPlotsTabbedPane;
	private JTabbedPane extractionRawTabbedPane;
	
	private JTabbedPane gencorrTabbedPane;
	private JTabbedPane gencorrPlotsTabbedPane;
	private JTabbedPane gencorrRawTabbedPane;

	
	protected JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")));	
	protected CorrParameter correlationParameters = new CorrParameter();
	protected ExParameter extractionParameters = new ExParameter();
	protected GenParameter gencorrParameters = new GenParameter();

	private TextArea textArea;
	
	public GUI() {
		
		frame.setResizable(true);
		// Create the menus
		frame.setJMenuBar(bar);
		// Add menus to bar
		bar.add(fileMenu);
		bar.add(menuAnalysis);
		bar.add(menuView);
		bar.add(menuHelp);
	
		// Add items to menus
		fileMenu.add(menuItemQuit);
		menuHelp.add(menuItemAbout);
		menuAnalysis.add(menuItemCorrelation);
		menuAnalysis.add(menuItemExtraction);
		menuAnalysis.add(menuItemSingleSiteExtraction);
		menuAnalysis.add(menuItemGenomeCorrelation);
		menuView.add(menuItemViewCorr);
		menuView.add(menuItemViewExtraction);
		menuView.add(menuItemViewGenCorr);
		menuView.add(menuItemViewNone);
		menuItemQuit.addActionListener(new ListenMenuQuit());
		menuItemAbout.addActionListener(new ListenMenuAbout());
		menuItemCorrelation.addActionListener(new ListenMenuRunCorrelation(this));
		menuItemExtraction.addActionListener(new ListenMenuRunExtraction(this));
		menuItemSingleSiteExtraction.addActionListener(new ListenMenuRunSingleSiteExtraction(this));
		menuItemGenomeCorrelation.addActionListener(new ListenMenuRunGenCorr(this));
		menuItemViewCorr.addActionListener(this);
		menuItemViewExtraction.addActionListener(this);
		menuItemViewGenCorr.addActionListener(this);
		menuItemViewNone.addActionListener(this);
		
		JLayeredPane layeredPane = new JLayeredPane();
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(layeredPane, BorderLayout.CENTER);
		GridBagLayout gbl_layeredPane = new GridBagLayout();
		gbl_layeredPane.columnWidths = new int[]{600, 0};
		gbl_layeredPane.rowHeights = new int[]{374, 0};
		gbl_layeredPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_layeredPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		layeredPane.setLayout(gbl_layeredPane);
		
		correlationTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		correlationTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		layeredPane.setLayer(correlationTabbedPane, 2);
		correlationPlotsTabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		correlationPlotsTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		correlationPlotsTabbedPane.setTabPlacement(JTabbedPane.LEFT);
		correlationTabbedPane.addTab("C-C Plots", null, correlationPlotsTabbedPane, null);
		correlationTabbedPane.setEnabledAt(0, true);
		correlationRawTabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		correlationRawTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		correlationTabbedPane.addTab("C-C Data", null, correlationRawTabbedPane, null);
		correlationTabbedPane.setEnabledAt(1, true);
		correlationTabbedPane.setVisible(false);
		
		extractionTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		extractionTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		layeredPane.setLayer(extractionTabbedPane, 1);
		extractionPlotsTabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		extractionPlotsTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		extractionTabbedPane.addTab("Extraction Plots", null, extractionPlotsTabbedPane, null);
		extractionRawTabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		extractionRawTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		extractionTabbedPane.addTab("Extraction Data", null, extractionRawTabbedPane, null);
		extractionTabbedPane.setVisible(false);

		//TODO gencorr here
		gencorrTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		gencorrTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		layeredPane.setLayer(gencorrTabbedPane, 0);
		gencorrPlotsTabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		gencorrPlotsTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		gencorrTabbedPane.addTab("Genome Correlation Plots", null, gencorrPlotsTabbedPane, null);
		gencorrRawTabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		gencorrRawTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		gencorrTabbedPane.addTab("Genome Correlation Data", null, gencorrRawTabbedPane, null);
		gencorrTabbedPane.setVisible(false);

		GridBagConstraints gbc_extractionTabbedPane = new GridBagConstraints();
		gbc_extractionTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_extractionTabbedPane.gridx = 0;
		gbc_extractionTabbedPane.gridy = 0;
		layeredPane.add(extractionTabbedPane, gbc_extractionTabbedPane);
		GridBagConstraints gbc_correlationTabbedPane = new GridBagConstraints();
		gbc_correlationTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_correlationTabbedPane.gridx = 0;
		gbc_correlationTabbedPane.gridy = 0;
		layeredPane.add(correlationTabbedPane, gbc_correlationTabbedPane);
		GridBagConstraints gbc_gencorrTabbedPane = new GridBagConstraints();
		gbc_gencorrTabbedPane.fill = GridBagConstraints.BOTH;
		gbc_gencorrTabbedPane.gridx = 0;
		gbc_gencorrTabbedPane.gridy = 0;
		layeredPane.add(gencorrTabbedPane, gbc_gencorrTabbedPane);
		
		textArea = new TextArea();
		frame.getContentPane().add(textArea, BorderLayout.SOUTH);
		frame.setSize(685, 677);
		frame.setLocation(300,300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unused")
		GUI gui = new GUI();
	}
	
	public int getCorrCount(){
		return corrCount;
	}
	public void setCorrCount(int count){
		corrCount = count;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(menuItemViewCorr)){
			showOnlyCorrelations();
		} else if (arg0.getSource().equals(menuItemViewExtraction)){
			showOnlyExtractions();
		} else if(arg0.getSource().equals(menuItemViewGenCorr)) {
			showOnlyGenCorr();
		} else if (arg0.getSource().equals(menuItemViewNone)){
			correlationTabbedPane.setVisible(false);
			extractionTabbedPane.setVisible(false);
			gencorrTabbedPane.setVisible(false);
		}
	}
	
	public void showOnlyCorrelations(){
		correlationTabbedPane.setVisible(true);
		extractionTabbedPane.setVisible(false);
		gencorrTabbedPane.setVisible(false);
	}
	
	public void showOnlyExtractions(){
		correlationTabbedPane.setVisible(false);
		extractionTabbedPane.setVisible(true);
		gencorrTabbedPane.setVisible(false);
	}

	public void showOnlyGenCorr(){
		correlationTabbedPane.setVisible(false);
		extractionTabbedPane.setVisible(false);
		gencorrTabbedPane.setVisible(true);
	}
	
	public void addRawCorrelation(String title, RawDataDisplayPanel raw) {
		correlationRawTabbedPane.add(title,raw);
		correlationTabbedPane.setVisible(true);
	}

	public Component addCorrelation(String title,JPanel panel) {
		correlationTabbedPane.setVisible(true);
		return correlationPlotsTabbedPane.add(title,panel);
	}
	
	public void addRawExtraction(String title, RawDataDisplayPanel raw) {
		extractionRawTabbedPane.add(title,raw);
		extractionTabbedPane.setVisible(true);
	}

	public Component addExtraction(String title,JPanel panel) {
		extractionTabbedPane.setVisible(true);
		return extractionPlotsTabbedPane.add(title,panel);
	}
	
	public void addRawGenCorr(String title, RawDataDisplayPanel raw) {
		gencorrRawTabbedPane.add(title, raw);
		gencorrTabbedPane.setVisible(true);
	}

	public Component addGenCorr(String title, JPanel panel) {
		gencorrTabbedPane.setVisible(true);
		return gencorrPlotsTabbedPane.add(title, panel);
	}
	
	public void printToGui(String s){
		textArea.append(s+"\n");
	}
	
	public void resize() {
		frame.setMaximumSize(frame.getPreferredSize());
		frame.setMinimumSize(frame.getPreferredSize());
	}
}
