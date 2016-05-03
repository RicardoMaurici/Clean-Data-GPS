package br.ufsc.src.igu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class BarraDeMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;

	public BarraDeMenu(JanelaPrincipal janelaPrincipal) {
		organizeMenu(janelaPrincipal);
	}

	private void organizeMenu(JanelaPrincipal janelaPrincipal) {
		JMenu menu;
		JMenuItem item;

		menu = new JMenu("Arquivo");
		add(menu);
		
		item = new JMenuItem("Abrir");
		menu.add(item);
		item.setActionCommand(EnumOpcaoMenu.OPCAOABRIR.name());
		item.addActionListener(janelaPrincipal);
		
		menu = new JMenu("Configuração");
		add(menu);

		item = new JMenuItem("Conexão");
		menu.add(item);
		item.setActionCommand(EnumOpcaoMenu.OPCAOCONEXAO.name());
		item.addActionListener(janelaPrincipal);		
	}
}