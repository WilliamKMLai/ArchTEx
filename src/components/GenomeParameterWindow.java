package components;

import gui.base.GUI;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.param.BamFilter;
import data.param.GenParameter;

import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import analysis.gencorr.GenCorrLoad;

@SuppressWarnings("serial")
public class GenomeParameterWindow extends JDialog {
	public GenParameter param;
	
	private JPanel contentPane;
	protected JFileChooser fc;
	private GUI gui;
	
	private JDialog frame = this;
	private JLabel lblCoordinateFileName;
	private JTextField txtRes;
	private JTextField txtExt;
	private JTextField txtThread;
	
	final DefaultListModel<String> expList;
	final DefaultListModel<String> indList;
	private JTextField txtTagCount;
	private JTextField txtStdDevSize;
	private JTextField txtNumStdDev;
	private JTextField txtCondWindow;
	private JTextField txtShift;

	//Create the frame.
	public GenomeParameterWindow(GenParameter templateparam, GUI gu, JFileChooser f) {
		gui = gu;
		fc = f;
		this.setModal(true);
		param = templateparam;
				
		setTitle("Genome Correlation Parameters");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 537, 612);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblBamData = new JLabel("BAM Data:");
		lblBamData.setBounds(56, 67, 103, 14);
		contentPane.add(lblBamData);
		
		JLabel lblInputFiles = new JLabel("Input Files:");
		lblInputFiles.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblInputFiles.setBounds(238, 11, 77, 14);
		contentPane.add(lblInputFiles);
		
		 //Frame to contain all experiments
      	JScrollPane scrlPane_Input = new JScrollPane();
      	scrlPane_Input.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
      	scrlPane_Input.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      	scrlPane_Input.setBounds(25, 97, 226, 135);
      	contentPane.add(scrlPane_Input);	
      	expList = new DefaultListModel<String>();
      	String[] exp_names = param.getInputNames();
      	for(int x = 0; x < exp_names.length; x++) expList.addElement(exp_names[x]);
      	final JList<String> lstExp = new JList<String>(expList);
      	lstExp.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      	scrlPane_Input.setViewportView(lstExp);
		JScrollPane scrlPane_Index = new JScrollPane();
		scrlPane_Index.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrlPane_Index.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrlPane_Index.setBounds(278, 97, 226, 135);
		contentPane.add(scrlPane_Index);
		indList = new DefaultListModel<String>();
      	String[] ind_names = param.getIndexNames();
      	for(int x = 0; x < ind_names.length; x++) indList.addElement(ind_names[x]);
		final JList<String> lstIndex = new JList<String>(indList);
		lstIndex.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrlPane_Index.setViewportView(lstIndex);
		
		lstExp.addListSelectionListener(new ListSelectionListener() {
      		public void valueChanged(ListSelectionEvent e) {
      			lstIndex.setSelectedIndex(lstExp.getSelectedIndex());
      		}
      	});
		lstIndex.addListSelectionListener(new ListSelectionListener() {
      		public void valueChanged(ListSelectionEvent e) {
      			lstExp.setSelectedIndex(lstIndex.getSelectedIndex());
      		}
      	});
		
		//Handles the input/index selection event
		JButton bamButton = new JButton("Select File");
		bamButton.setBounds(165, 63, 103, 23);
		contentPane.add(bamButton);
		bamButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fc.setFileFilter(new BamFilter());
				File newBamFile = getBamFile();
				if(newBamFile != null) {
					IndexBuilderWindow index = new IndexBuilderWindow(param, fc);
					index.setVisible(true);
					File newBamIndex = index.getIndexFile();
					if(index.getIndexFile() != null) {
						if(newBamIndex.getName().equals("NONE")) {
							newBamIndex = param.generateIndex(newBamFile,newBamFile+".bai",gui);
						}
						if(!newBamIndex.getName().equals(newBamFile.getName() + ".bai")) {
							JOptionPane.showMessageDialog(null, "BAM-BAI File Names Do Not Match!!!");
						} else {
							param.addInput(newBamFile);
							expList.addElement(newBamFile.getName());
							param.addIndex(newBamIndex);
							indList.addElement(newBamIndex.getName());
						}
					}
				}
			}
		});		
		
		JSeparator separator = new JSeparator();
		separator.setBounds(35, 243, 479, 4);
		contentPane.add(separator);
		
		JLabel lblExtractionParameters = new JLabel("Genome Correlation Parameters");
		lblExtractionParameters.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblExtractionParameters.setBounds(165, 258, 216, 14);
		contentPane.add(lblExtractionParameters);
		
		JLabel lblResolutionbp = new JLabel("Resolution (bp):");
		lblResolutionbp.setBounds(278, 286, 99, 14);
		contentPane.add(lblResolutionbp);
		
		JLabel lblTagExtensionbp_1 = new JLabel("Tag Extension (bp):");
		lblTagExtensionbp_1.setBounds(35, 286, 124, 14);
		contentPane.add(lblTagExtensionbp_1);
		
		JLabel lblCpusToUse = new JLabel("CPU's to Use:");
		lblCpusToUse.setBounds(278, 317, 99, 14);
		contentPane.add(lblCpusToUse);
		
		JLabel lblStrandShift = new JLabel("Strand Shift (bp):");
		lblStrandShift.setBounds(35, 317, 113, 14);
		contentPane.add(lblStrandShift);
		
		txtRes = new JTextField(Integer.toString(param.getResolution()));
		txtRes.setBounds(386, 283, 31, 20);
		contentPane.add(txtRes);
		txtRes.setColumns(10);

		txtExt = new JTextField(Integer.toString(param.getExtension()));
		txtExt.setBounds(175, 283, 49, 20);
		contentPane.add(txtExt);
		txtExt.setColumns(10);

		txtThread = new JTextField(Integer.toString(param.getThreads()));
		txtThread.setBounds(387, 314, 31, 20);
		contentPane.add(txtThread);
		txtThread.setColumns(10);
		
		txtShift = new JTextField(Integer.toString(param.getTagShift()));
		txtShift.setHorizontalAlignment(SwingConstants.LEFT);
		txtShift.setColumns(10);
		txtShift.setBounds(175, 314, 49, 20);
		contentPane.add(txtShift);
		
		JLabel lblTransformation = new JLabel("Transformation:");
		lblTransformation.setBounds(191, 371, 109, 14);
		contentPane.add(lblTransformation);
		
		//Variable fields for transformations
		final JLabel lblStandard = new JLabel("Standardize to Tag Count (bp):");
		lblStandard.setBounds(56, 478, 212, 14);
		contentPane.add(lblStandard);
		lblStandard.setVisible(false);
		txtTagCount = new JTextField();
		txtTagCount.setText(Integer.toString(param.getStandardWindow()));
		txtTagCount.setBounds(290, 475, 86, 20);
		contentPane.add(txtTagCount);
		txtTagCount.setColumns(10);	
		txtTagCount.setVisible(false);
		final JLabel lblGauss = new JLabel("Gaussian Smoothing:");
		lblGauss.setBounds(25, 474, 133, 23);
		contentPane.add(lblGauss);
		lblGauss.setVisible(false);
		final JLabel lblStdDevSize = new JLabel("Std Dev Size (bp):");
		lblStdDevSize.setBounds(165, 478, 109, 14);
		contentPane.add(lblStdDevSize);
		lblStdDevSize.setVisible(false);
		final JLabel lblOfStd = new JLabel("# of Std Devs:");
		lblOfStd.setBounds(340, 478, 94, 14);	
		contentPane.add(lblOfStd);
		lblOfStd.setVisible(false);
		txtStdDevSize = new JTextField();
		txtStdDevSize.setText(Integer.toString(param.getSDSize()));
		txtStdDevSize.setBounds(278, 475, 49, 20);
		contentPane.add(txtStdDevSize);
		txtStdDevSize.setVisible(false);
		txtStdDevSize.setColumns(10);
		txtNumStdDev = new JTextField();
		txtNumStdDev.setText(Integer.toString(param.getSDNum()));
		txtNumStdDev.setBounds(444, 475, 31, 20);
		contentPane.add(txtNumStdDev);
		txtNumStdDev.setVisible(false);
		txtNumStdDev.setColumns(10);
		
		final JLabel lblCondWindow = new JLabel("Conditional Window Size (bp):");
		lblCondWindow.setBounds(56, 506, 202, 14);
		contentPane.add(lblCondWindow);
		lblCondWindow.setVisible(false);
		txtCondWindow = new JTextField();
		txtCondWindow.setText(Integer.toString(param.getCondWindow()));
		txtCondWindow.setBounds(289, 503, 49, 20);
		contentPane.add(txtCondWindow);
		txtCondWindow.setColumns(10);
		txtCondWindow.setVisible(false);
				
		final JRadioButton rdbtnStandardize = new JRadioButton("Standardize");
		rdbtnStandardize.setBounds(35, 392, 109, 23);
		contentPane.add(rdbtnStandardize);
		final JRadioButton rdbtnSquareroot = new JRadioButton("Squareroot");
		rdbtnSquareroot.setBounds(35, 418, 108, 23);
		contentPane.add(rdbtnSquareroot);
		final JRadioButton rdbtnLog = new JRadioButton("Log2");
		rdbtnLog.setBounds(165, 392, 86, 23);
		contentPane.add(rdbtnLog);
		final JRadioButton rdbtnCndPos = new JRadioButton("Conditional Positioning");
		rdbtnCndPos.setBounds(326, 418, 197, 23);
		contentPane.add(rdbtnCndPos);
		final JRadioButton rdbtnGauss = new JRadioButton("Gaussian Smoothing");
		rdbtnGauss.setBounds(326, 392, 197, 23);
		contentPane.add(rdbtnGauss);
		final JRadioButton rdbtnPoisson = new JRadioButton("Poisson Probability");
		rdbtnPoisson.setBounds(165, 418, 150, 23);
		contentPane.add(rdbtnPoisson);
		final JRadioButton rdbtnNone = new JRadioButton("None");
		rdbtnNone.setBounds(35, 444, 109, 23);
		contentPane.add(rdbtnNone);

        //Add radio buttons for transformation analysis
        ButtonGroup TransStrand = new ButtonGroup();
        TransStrand.add(rdbtnStandardize);
        TransStrand.add(rdbtnSquareroot);
        TransStrand.add(rdbtnLog);
        TransStrand.add(rdbtnCndPos);
        TransStrand.add(rdbtnGauss);
        TransStrand.add(rdbtnPoisson);
        TransStrand.add(rdbtnNone);
        if(param.getTrans().equals("standard")) {
        	rdbtnStandardize.setSelected(true);
        	lblStandard.setVisible(true);
    		txtTagCount.setVisible(true);
        }
        if(param.getTrans().equals("squareroot")) rdbtnSquareroot.setSelected(true);
        if(param.getTrans().equals("log2")) rdbtnLog.setSelected(true);
        if(param.getTrans().equals("poisson")) rdbtnPoisson.setSelected(true);
        if(param.getTrans().equals("condpos")) {
        	rdbtnCndPos.setSelected(true);
    		lblGauss.setVisible(true);
    		lblStdDevSize.setVisible(true);
    		lblOfStd.setVisible(true);
    		txtStdDevSize.setVisible(true);
    		txtNumStdDev.setVisible(true);
    		lblCondWindow.setVisible(true);
    		txtCondWindow.setVisible(true);
        }
        if(param.getTrans().equals("gauss")) {
        	rdbtnGauss.setSelected(true);
        	lblGauss.setVisible(true);
    		lblStdDevSize.setVisible(true);
    		lblOfStd.setVisible(true);
    		txtStdDevSize.setVisible(true);
    		txtNumStdDev.setVisible(true);
        }
        if(param.getTrans().equals("none")) rdbtnNone.setSelected(true);

        ActionListener standardizeprompts = new ActionListener() {public void actionPerformed(ActionEvent arg0) {
			lblStandard.setVisible(true);
			txtTagCount.setVisible(true);
			lblGauss.setVisible(false);
			lblStdDevSize.setVisible(false);
			lblOfStd.setVisible(false);
			txtStdDevSize.setVisible(false);
			txtNumStdDev.setVisible(false);
			lblCondWindow.setVisible(false);
			txtCondWindow.setVisible(false);
		};};
		ActionListener nullprompts = new ActionListener() {public void actionPerformed(ActionEvent arg0) {
			lblStandard.setVisible(false);
			txtTagCount.setVisible(false);
			lblGauss.setVisible(false);
			lblStdDevSize.setVisible(false);
			lblOfStd.setVisible(false);
			txtStdDevSize.setVisible(false);
			txtNumStdDev.setVisible(false);
			lblCondWindow.setVisible(false);
			txtCondWindow.setVisible(false);
		};};
		ActionListener gaussprompts = new ActionListener() {public void actionPerformed(ActionEvent arg0) {
			lblStandard.setVisible(false);
			txtTagCount.setVisible(false);
			lblGauss.setVisible(true);
			lblStdDevSize.setVisible(true);
			lblOfStd.setVisible(true);
			txtStdDevSize.setVisible(true);
			txtNumStdDev.setVisible(true);
			lblCondWindow.setVisible(false);
			txtCondWindow.setVisible(false);
		};};
		ActionListener condprompts = new ActionListener() {public void actionPerformed(ActionEvent arg0) {
			lblStandard.setVisible(false);
			txtTagCount.setVisible(false);
			lblGauss.setVisible(true);
			lblStdDevSize.setVisible(true);
			lblOfStd.setVisible(true);
			txtStdDevSize.setVisible(true);
			txtNumStdDev.setVisible(true);
			lblCondWindow.setVisible(true);
			txtCondWindow.setVisible(true);
		};};
		
        rdbtnStandardize.addActionListener(standardizeprompts);
        rdbtnSquareroot.addActionListener(nullprompts);
        rdbtnLog.addActionListener(nullprompts);
        rdbtnPoisson.addActionListener(nullprompts); 
        rdbtnNone.addActionListener(nullprompts); 
        rdbtnGauss.addActionListener(gaussprompts);
        rdbtnCndPos.addActionListener(condprompts);
			
		JLabel label = new JLabel("Strand to Examine:");
		label.setBounds(35, 345, 126, 14);
		contentPane.add(label);
		
		final JRadioButton rdbtnCombined = new JRadioButton("Combined");
		rdbtnCombined.setBounds(160, 341, 109, 23);
		contentPane.add(rdbtnCombined);
		final JRadioButton rdbtnForward = new JRadioButton("Forward");
		rdbtnForward.setBounds(272, 341, 109, 23);
		contentPane.add(rdbtnForward);
		final JRadioButton rdbtnReverse = new JRadioButton("Reverse");
		rdbtnReverse.setBounds(383, 341, 109, 23);
		contentPane.add(rdbtnReverse);
		ButtonGroup StrandGroup = new ButtonGroup();
		StrandGroup.add(rdbtnCombined);
		StrandGroup.add(rdbtnForward);
		StrandGroup.add(rdbtnReverse);
		if(param.getStrand().equals("C")) rdbtnCombined.setSelected(true);
		else if(param.getStrand().equals("F")) rdbtnForward.setSelected(true);
		else if(param.getStrand().equals("R")) rdbtnReverse.setSelected(true);
		
		//Handles the Cancel event
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(314, 546, 103, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		
		JButton btnLoadTemplate = new JButton("Run Genome Correlation");
		btnLoadTemplate.setBounds(89, 546, 191, 23);
		contentPane.add(btnLoadTemplate);		
		
		JButton btnResetBam = new JButton("Reset BAM");
		btnResetBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ResetBamWindow reset = new ResetBamWindow();
				reset.setVisible(true);
				if(reset.getConfirm()) {
					param.setIndex(new Vector<File>());
					param.setInput(new Vector<File>());
					expList.removeAllElements();
					indList.removeAllElements();
				}
			}
		});
		btnResetBam.setBounds(387, 36, 117, 23);
		contentPane.add(btnResetBam);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(25, 531, 479, 4);
		contentPane.add(separator_2);
		
		JButton removeButton = new JButton("Remove BAM");
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(lstExp.getSelectedIndex() > -1) {
					param.getInput().remove(lstExp.getSelectedIndex());
					param.getIndex().remove(lstIndex.getSelectedIndex());
					expList.remove(lstExp.getSelectedIndex());
					indList.remove(lstIndex.getSelectedIndex());
				}
			}
		});
		removeButton.setBounds(387, 63, 117, 23);
		contentPane.add(removeButton);
		
		//Handles the Start of a run
		btnLoadTemplate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(param.getInput().size() < 2) {
						JOptionPane.showMessageDialog(null, "Not Enough BAM Files Loaded!!!");
					}
					else {
						System.out.println("Loading Parameters...");
						gui.printToGui("Loading Parameters...");
						param.setResolution(Integer.parseInt(txtRes.getText()));	
						param.setExtension(Integer.parseInt(txtExt.getText()));	
						param.setThreads(Integer.parseInt(txtThread.getText()));
						param.setTagShift(Integer.parseInt(txtShift.getText()));
						if(rdbtnStandardize.isSelected()) {
							param.setTrans("standard");
							param.setStandardWindow(Integer.parseInt(txtTagCount.getText()));
						} else if(rdbtnSquareroot.isSelected()) param.setTrans("squareroot");
						else if(rdbtnLog.isSelected()) param.setTrans("log2");
						else if(rdbtnPoisson.isSelected()) param.setTrans("poisson");
						else if(rdbtnNone.isSelected()) param.setTrans("none");
						else if(rdbtnGauss.isSelected()) {
							param.setSDSize(Integer.parseInt(txtStdDevSize.getText()));
							param.setSDNum(Integer.parseInt(txtNumStdDev.getText()));
							param.setTrans("gauss");
						}
						else if(rdbtnCndPos.isSelected()) {
							param.setSDSize(Integer.parseInt(txtStdDevSize.getText()));
							param.setSDNum(Integer.parseInt(txtNumStdDev.getText()));
							param.setCondWindow(Integer.parseInt(txtCondWindow.getText()));
							param.setTrans("condpos");
						}
						
						if(rdbtnCombined.isSelected()) param.setStrand("C");
						else if(rdbtnForward.isSelected()) param.setStrand("F");
						else param.setStrand("R");
													
						param.reset();
						gui.printToGui(param.printStats());
						System.out.println("Parameters Loaded\n");
						gui.printToGui("Parameters Loaded\n");
						param.generateStats();
						
						GenCorrLoad cloader = new GenCorrLoad(gui, param);
						for(int i = 0; i < param.getInput().size(); i++) {
							for(int j = 0; j < param.getInput().size(); j++) {
								if(i != j && (i - j) >= 1) {
									param.setPrimeExp(i);
									param.setSecExp(j);
									cloader.run();
									frame.dispose();
								}
							}
						}
						addPanes();
					}
			} catch(NumberFormatException nfe){
						JOptionPane.showMessageDialog(null, "Input Fields Must Contain Integers");
			}
			}
		});
	}
	
	public void addPanes() {
		CreateChartPanel panel = new CreateChartPanel();
		gui.addGenCorr("Genome Correlations", panel.createBarChart(param.getCorrelation(), param.getCorNames()));
		RawDataDisplayPanel raw = new RawDataDisplayPanel(param.getCorrelation(), param.getInputNames());
		gui.addRawGenCorr("Genome Correlations", raw);
		gui.showOnlyGenCorr();
	}
	
	public File getBamFile(){
		File bamFile = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			bamFile = fc.getSelectedFile();
		}
		return bamFile;
	}
	
	public File getCoordFile(){
		File coordFile = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			coordFile = fc.getSelectedFile();
		}
		if(coordFile != null) lblCoordinateFileName.setText(coordFile.getName());
		return coordFile;
	}
	
	public String getName(Vector<File> file) {
		String name = "";
		for(int x = 0; x < file.size(); x++) {
			name += file.get(x).getName() + "\n";
		}
		return name;
	}
}