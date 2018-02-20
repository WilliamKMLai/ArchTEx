package gui.base;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import components.SiteParameterWindow;

public class ListenMenuRunSingleSiteExtraction implements ActionListener {
	GUI gui;
	
	public ListenMenuRunSingleSiteExtraction(GUI gu) {
		gui = gu;
	}

	public void actionPerformed(ActionEvent e) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SiteParameterWindow window = new SiteParameterWindow(gui.extractionParameters, gui, gui.fc);
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		 });
	}
}
