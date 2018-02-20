package components;

import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import data.param.BaiFilter;
import data.param.CorrParameter;
import data.param.ExParameter;
import data.param.GenParameter;

import javax.swing.JLabel;
import javax.swing.JButton;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class IndexBuilderWindow extends JDialog {
	private JDialog frame = this;
	public JFileChooser fc;
	
	private File BAMIndex = null;
	
	public IndexBuilderWindow(GenParameter param, JFileChooser filechooser) {
		fc = filechooser;
		setBounds(300,300, 369, 140);
		setTitle("BAM Index File");
		getContentPane().setLayout(null);
		this.setModal(true);
		
		JLabel lblPleaseLoadBam = new JLabel("Please Load BAM Index File:");
		lblPleaseLoadBam.setBounds(98, 11, 198, 30);
		getContentPane().add(lblPleaseLoadBam);

		JButton btnNewButton = new JButton("Select File");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileFilter(new BaiFilter());
				BAMIndex = getBamIndexFile();
				//param.addIndex(getBamIndexFile());
				frame.dispose();
			}
		});
		btnNewButton.setBounds(47, 52, 109, 23);
		getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("No Index File");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BAMIndex = new File("NONE");
				//param.addIndex(new File("NONE"));
				frame.dispose();
			}
		});
		btnNewButton_1.setBounds(190, 52, 119, 23);
		getContentPane().add(btnNewButton_1);
		
	}
	
	public IndexBuilderWindow(ExParameter param, JFileChooser filechooser) {
		fc = filechooser;
		setBounds(300,300, 369, 140);
		setTitle("BAM Index File");
		getContentPane().setLayout(null);
		this.setModal(true);
		
		JLabel lblPleaseLoadBam = new JLabel("Please Load BAM Index File:");
		lblPleaseLoadBam.setBounds(98, 11, 198, 30);
		getContentPane().add(lblPleaseLoadBam);

		JButton btnNewButton = new JButton("Select File");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileFilter(new BaiFilter());
				BAMIndex = getBamIndexFile();
				//param.addIndex(getBamIndexFile());
				frame.dispose();
			}
		});
		btnNewButton.setBounds(47, 52, 109, 23);
		getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("No Index File");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BAMIndex = new File("NONE");
				//param.addIndex(new File("NONE"));
				frame.dispose();
			}
		});
		btnNewButton_1.setBounds(190, 52, 119, 23);
		getContentPane().add(btnNewButton_1);
		
	}
	
	public IndexBuilderWindow(CorrParameter templateparam, JFileChooser filechooser){
		
		fc = filechooser;
		setBounds(300,300, 369, 140);
		setTitle("BAM Index File");
		getContentPane().setLayout(null);
		this.setModal(true);
		
		JLabel lblPleaseLoadBam = new JLabel("Please Load BAM Index File:");
		lblPleaseLoadBam.setBounds(100, 11, 169, 30);
		getContentPane().add(lblPleaseLoadBam);

		JButton btnNewButton = new JButton("Select File");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fc.setFileFilter(new BaiFilter());
				BAMIndex = getBamIndexFile();
				//param.addIndex(getBamIndexFile());
				frame.dispose();
			}
		});
		btnNewButton.setBounds(47, 52, 109, 23);
		getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("No Index File");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BAMIndex = new File("NONE");
				//param.addIndex(new File("NONE"));
				frame.dispose();
			}
		});
		btnNewButton_1.setBounds(190, 52, 119, 23);
		getContentPane().add(btnNewButton_1);
	}
	
	public File getBamIndexFile(){
		File bamIndexFile = null;
		int returnVal = fc.showOpenDialog(fc);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			bamIndexFile = fc.getSelectedFile();
		}
		return bamIndexFile;
	}
	
	public File getIndexFile() {
		return BAMIndex;
	}
}
