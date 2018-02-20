package gui.base;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import components.CorrelationParametersWindow;

public class ListenMenuRunCorrelation implements ActionListener {
	GUI gui;
	
	public ListenMenuRunCorrelation(GUI gu){
		gui = gu;
	}
	
	public void actionPerformed(ActionEvent e) {
		gui.setCorrCount(gui.getCorrCount() + 1);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CorrelationParametersWindow window = new CorrelationParametersWindow(gui.correlationParameters, gui, gui.fc);
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		 });
	}
}