package br.ufsc.src.igu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import br.ufsc.src.controle.ServicosControle;
import br.ufsc.src.igu.painel.PainelAbrir;
import br.ufsc.src.igu.painel.PainelAbstrato;
import br.ufsc.src.igu.painel.PainelConexao;


public class JanelaPrincipal extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	ServicosControle controle;

	public JanelaPrincipal(ServicosControle controle) {
		super("Clean Data GPS");
		this.controle = controle;
		configure();
	}

	private void configure() {
		this.setInterfaceLayout();
		JLabel texto;
		texto = new JLabel();
		//texto.setText("-- Data GPS --");
		texto.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 40));
		texto.setHorizontalAlignment(NORMAL);
		add(texto);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setJMenuBar(new BarraDeMenu(this));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(640, 580));
		pack();
		setLocationRelativeTo(null);
	}

	public void interaja() {
		setVisible(true);
	}
	
	private void setInterfaceLayout() {
		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		EnumOpcaoMenu opcao = EnumOpcaoMenu.valueOf(e.getActionCommand());
		PainelAbstrato painel = null;

		switch (opcao) {
		case OPCAOCONEXAO:
			painel = new PainelConexao(controle);
			break;
		case OPCAOABRIR:
			painel = new PainelAbrir(controle);
			break;
		} 
		setContentPane(painel);
		pack(); 
	}
}

