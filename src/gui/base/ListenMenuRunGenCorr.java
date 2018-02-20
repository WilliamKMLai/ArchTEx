package gui.base;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import components.GenomeParameterWindow;

public class ListenMenuRunGenCorr implements ActionListener {
	GUI gui;
	
	public ListenMenuRunGenCorr(GUI gu) {
		gui = gu;
	}

	public void actionPerformed(ActionEvent e) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GenomeParameterWindow window = new GenomeParameterWindow(gui.gencorrParameters, gui, gui.fc);
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		 });
	}
}
