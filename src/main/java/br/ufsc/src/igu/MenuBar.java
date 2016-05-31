package br.ufsc.src.igu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;

	public MenuBar(MainWindow mainWindow) {
		organizeMenu(mainWindow);
	}

	private void organizeMenu(MainWindow mainWindow) {
		JMenu menu;
		JMenuItem item;

		menu = new JMenu("File");
		add(menu);
		
		item = new JMenuItem("Open");
		menu.add(item);
		item.setActionCommand(EnumMenuOption.OPTIONOPEN.name());
		item.addActionListener(mainWindow);
		
		menu = new JMenu("Data Clean");
		add(menu);
		
		item = new JMenuItem("Clean Trajectories");
		menu.add(item);
		item.setActionCommand(EnumMenuOption.OPTIONDATACLEAN.name());
		item.addActionListener(mainWindow);
		
		menu = new JMenu("Configuration");
		add(menu);

		item = new JMenuItem("Connection");
		menu.add(item);
		item.setActionCommand(EnumMenuOption.OPTIONCONECTION.name());
		item.addActionListener(mainWindow);		
	}
}