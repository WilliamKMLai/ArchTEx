package components;



import javax.swing.JDialog;
import javax.swing.JFileChooser;

import data.param.ExParameter;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class ResetBamWindow extends JDialog {
	private JDialog frame = this;
	public ExParameter param;
	public JFileChooser fc;
		
	private boolean reset = false;
	
	public ResetBamWindow() {
		setBounds(300,300, 369, 140);
		setTitle("Reset BAM Files");
		getContentPane().setLayout(null);
		this.setModal(true);
		
		JLabel lblRemoveAll = new JLabel("Remove All BAM Files?");
		lblRemoveAll.setBounds(118, 22, 147, 14);
		getContentPane().add(lblRemoveAll);

		JButton btnYes = new JButton("Yes");
		btnYes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset = true;
				frame.dispose();
			}
		});
		btnYes.setBounds(47, 52, 109, 23);
		getContentPane().add(btnYes);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset = false;
				frame.dispose();
			}
		});
		btnCancel.setBounds(190, 52, 119, 23);
		getContentPane().add(btnCancel);		
	}
	
	public boolean getConfirm() {
		return reset;
	}
}
