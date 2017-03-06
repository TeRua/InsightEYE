package www.insighteye.zz.am.gui;

import java.awt.Container;

public class Controller {
	private MainFrame MF;
	private MainPanel MP;
	private Container cont;
	
	public Controller() {
		MF = new MainFrame();
		cont = MF.getContentPane();
		MP = new MainPanel();
		MF.setContentPane(MP);
		MP.setVisible(true);
		MF.setVisible(true);
	}
}
