package components;

import gui.base.GUI;

import java.awt.Color;
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
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import data.param.BamFilter;
import data.param.CorrParameter;
import data.param.ExParameter;
import javax.swing.JRadioButton;

import analysis.corr.CorrLoad;

@SuppressWarnings("serial")
public class CorrelationParametersWindow extends JDialog {
	public CorrParameter param;
	
	private JPanel contentPane;
	protected JFileChooser fc;
	private GUI gui;
	
	private JDialog frame = this;
	
	private JTextField txtWind;
	private JTextField txtSample;
	private JTextField txtThread;
	
	final DefaultListModel<String> expList;
	final DefaultListModel<String> indList;

	//Create the frame.
	public CorrelationParametersWindow(CorrParameter cParam, GUI gu, JFileChooser f) {
		gui = gu;
		fc = f;
		param = cParam;
		this.setModal(true);
				
		setTitle("Correlation Parameters");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 537, 564);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblBamData = new JLabel("BAM Data:");
		lblBamData.setBounds(56, 45, 103, 14);
		contentPane.add(lblBamData);
		
		JLabel lblInputFiles = new JLabel("Input Files:");
		lblInputFiles.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblInputFiles.setBounds(238, 11, 77, 14);
		contentPane.add(lblInputFiles);		
		
		 //Frame to contain all experiments
      	JScrollPane scrlPane_Input = new JScrollPane();
      	scrlPane_Input.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
      	scrlPane_Input.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      	scrlPane_Input.setBounds(56, 91, 303, 194);
      	contentPane.add(scrlPane_Input);	
      	expList = new DefaultListModel<String>();
      	
      	String[] exp_names = param.getInputNames();
      	for(int x = 0; x < exp_names.length; x++) expList.addElement(exp_names[x]);
      	final JList<String> lstExp = new JList<String>(expList);
      	lstExp.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      	scrlPane_Input.setViewportView(lstExp);
		indList = new DefaultListModel<String>();
      	String[] ind_names = param.getIndexNames();
      	for(int x = 0; x < ind_names.length; x++) indList.addElement(ind_names[x]);
		
		//Handles the input/index selection event
		JButton bamButton = new JButton("Select File");
		bamButton.setBounds(135, 41, 103, 23);
		contentPane.add(bamButton);
		bamButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fc.setFileFilter(new BamFilter());
				File newBamFile = getBamFile();
				//if user selected a bam file
				if(newBamFile != null) {
					//generate an index file chooser window
					IndexBuilderWindow index = new IndexBuilderWindow(param, fc);
					index.setVisible(true);
					//set the new bam index to whatever they choose
					File newBamIndex = index.getIndexFile();
					//if they choose a real file
					if(index.getIndexFile() != null) {
						//make sure that the bam file is appropriately named
						if(newBamIndex.getName().equals("NONE")) {
							newBamIndex = new ExParameter().generateIndex(newBamFile, newBamFile+".bai",gui);
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
		separator.setBounds(25, 296, 479, 4);
		contentPane.add(separator);				
		
		JLabel lblCorrelationParameters = new JLabel("Correlation Parameters");
		lblCorrelationParameters.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblCorrelationParameters.setBounds(185, 315, 133, 14);
		contentPane.add(lblCorrelationParameters);

		JLabel lblCpusToUse = new JLabel("CPU's to Use:");
		lblCpusToUse.setBounds(151, 378, 99, 14);
		contentPane.add(lblCpusToUse);

		txtThread = new JTextField("1");
		txtThread.setBounds(285, 375, 30, 20);
		contentPane.add(txtThread);
		txtThread.setColumns(10);		
		
		JLabel lblWindowSizebp = new JLabel("Window Size (bp):");
		lblWindowSizebp.setBounds(46, 443, 113, 14);
		contentPane.add(lblWindowSizebp);
		
		JLabel lblTagSamplings = new JLabel("# of Samplings");
		lblTagSamplings.setBounds(267, 443, 113, 14);
		contentPane.add(lblTagSamplings);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(25, 403, 479, 4);
		contentPane.add(separator_1);
		
		JLabel lblSampling = new JLabel("Random Sampling Parameters");
		lblSampling.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSampling.setBounds(166, 418, 185, 14);
		contentPane.add(lblSampling);
		
		//Set Text boxes to contain the Random Sampling Parameters
		txtWind = new JTextField("50000");
		txtWind.setHorizontalAlignment(SwingConstants.LEFT);
		txtWind.setBounds(185, 440, 49, 20);
		contentPane.add(txtWind);
		txtWind.setColumns(10);

		txtSample = new JTextField("10");
		txtSample.setBounds(390, 440, 49, 20);
		contentPane.add(txtSample);
		txtSample.setColumns(10);		

		//Radio Buttons to Control Whole-Genome vs Random Sampling
		JLabel lblCorrelationType = new JLabel("Correlation Type:");
		lblCorrelationType.setBounds(56, 340, 133, 14);
		contentPane.add(lblCorrelationType);
		
		final JRadioButton rdbtnRandom = new JRadioButton("Random Sampling");
		rdbtnRandom.setBounds(166, 336, 142, 23);
		contentPane.add(rdbtnRandom);
		
		final JRadioButton rdbtnGenome = new JRadioButton("Whole Genome");
		rdbtnGenome.setBounds(322, 336, 153, 23);
		contentPane.add(rdbtnGenome);
		
        ButtonGroup GroupStrand = new ButtonGroup();
        GroupStrand.add(rdbtnRandom);
        GroupStrand.add(rdbtnGenome);
        rdbtnRandom.setSelected(true);
        rdbtnGenome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				txtWind.setEditable(false);
				txtSample.setEditable(false);
				txtWind.setForeground(Color.GRAY);
				txtSample.setForeground(Color.GRAY);
			}
        });
        rdbtnRandom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				txtWind.setEditable(true);
				txtSample.setEditable(true);
				txtWind.setForeground(Color.BLACK);
				txtSample.setForeground(Color.BLACK);
			}
        });
		
		//Handles the Cancel event
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(267, 493, 103, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		
		JButton btnRunAnalysis = new JButton("Run Analysis");
		btnRunAnalysis.setBounds(128, 493, 122, 23);
		contentPane.add(btnRunAnalysis);		
		
		JButton btnResetBam = new JButton("Reset BAM");
		btnResetBam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ResetBamWindow reset = new ResetBamWindow();
				reset.setVisible(true);
				if(reset.getConfirm()) {
					param.setIndex(new Vector<File>());
					param.setInput(new Vector<File>());
					//datatemplate.resetTemplate();
					expList.removeAllElements();
					indList.removeAllElements();
				}
			}
		});
		btnResetBam.setBounds(387, 89, 117, 23);
		contentPane.add(btnResetBam);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(25, 478, 479, 4);
		contentPane.add(separator_2);
		
		JButton removeButton = new JButton("Remove BAM");
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(lstExp.getSelectedIndex() > -1) {
					param.getInput().remove(lstExp.getSelectedIndex());
					param.getIndex().remove(lstExp.getSelectedIndex());
					expList.remove(lstExp.getSelectedIndex());
					indList.remove(lstExp.getSelectedIndex());
				}
			}
		});
		removeButton.setBounds(387, 123, 117, 23);
		contentPane.add(removeButton);
		
		//Handles the Start of a run
		btnRunAnalysis.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					System.out.println("Loading Parameters...");
					gui.printToGui("Loading Parameters...");
					if(param.getInput().size() < 1) {
						JOptionPane.showMessageDialog(null, "No BAM Files Loaded!!!");
					} else if(Integer.parseInt(txtWind.getText()) < 0) {
						JOptionPane.showMessageDialog(null, "Window Must Be Larger Than 0!!!");
					} else if(Integer.parseInt(txtSample.getText()) < 0) {
						JOptionPane.showMessageDialog(null, "Iterations Must Be Larger Than 0!!!");
					} else if(Integer.parseInt(txtThread.getText()) < 1) {
						JOptionPane.showMessageDialog(null, "Must Use At Least 1 CPU!!!");
					} else {
						if(rdbtnGenome.isSelected()) { param.setCorrType(true);
						} else { param.setCorrType(false); } 
						param.setWindow(Integer.parseInt(txtWind.getText()));
						param.setIterations(Integer.parseInt(txtSample.getText()));
						param.setThreads(Integer.parseInt(txtThread.getText()));
						System.out.println("Parameters Loaded\n");
						gui.printToGui("Parameters Loaded\n");
						CorrLoad corrloader = new CorrLoad(gui, param);
						
						frame.dispose();
						
						for(int i = 0; i < param.getInput().size();i++) {
							System.out.println("Cross-Correlation: " + param.getInput().get(i).getName());
							gui.printToGui("Cross-Correlation: " + param.getInput().get(i).getName());
							param.setExp(i);
							corrloader.run(i);
						}
					}
				}catch(NumberFormatException nfe){
					JOptionPane.showMessageDialog(null, "Input Fields Must Contain Integers");
				}
			}
		});
	}
	
	public File getBamFile(){
		File bamFile = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			bamFile = fc.getSelectedFile();
		}
		return bamFile;
	}
	
	
	public String getName(Vector<File> file) {
		String name = "";
		for(int x = 0; x < file.size(); x++) {
			name += file.get(x).getName() + "\n";
		}
		return name;
	}
}