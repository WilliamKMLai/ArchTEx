package gui.base;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import components.ExtractionParameterWindow;

public class ListenMenuRunExtraction implements ActionListener {
	GUI gui;
	
	public ListenMenuRunExtraction(GUI gu) {
		gui = gu;
	}

	public void actionPerformed(ActionEvent e) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ExtractionParameterWindow window = new ExtractionParameterWindow(gui.extractionParameters, gui, gui.fc);
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		 });
	}
}
