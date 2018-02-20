package gui.base;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

public class ListenMenuAbout implements ActionListener {
	public void actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(null, "ArchTEx : Version 1.0.5"+"\n Jonathan E. Bard, William KM. Lai");
		
	}
}
