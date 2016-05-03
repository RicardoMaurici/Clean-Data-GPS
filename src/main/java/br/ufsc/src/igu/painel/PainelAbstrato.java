package br.ufsc.src.igu.painel;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import br.ufsc.src.controle.ServicosControle;

public abstract class PainelAbstrato extends JPanel implements ActionListener{
	private static final long serialVersionUID = 1L;
	private String titulo;
	protected JButton botaoProcessa;
	protected ServicosControle controle;
	protected Container screen;

	public PainelAbstrato(String titulo, ServicosControle controle, JButton botaoProcessa) {
		this.titulo = titulo;
		this.controle = controle;
		this.botaoProcessa = botaoProcessa;
		definaAparencia();
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTitulo() {
		return titulo;
	}

	private void definaAparencia() {
		setBackground(Color.LIGHT_GRAY);
		setBorder(BorderFactory.createTitledBorder(titulo));
		botaoProcessa.addActionListener(this);
	}

	public abstract void definaComponentes();

	public abstract void ajusteComponentes();

	public abstract void actionPerformed(ActionEvent e);

	protected void limpeTela() {
		getRootPane().setBackground(Color.LIGHT_GRAY);
		setVisible(false);
	}
}
