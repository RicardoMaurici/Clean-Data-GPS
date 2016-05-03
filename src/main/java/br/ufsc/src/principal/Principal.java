package br.ufsc.src.principal;

import javax.swing.SwingUtilities;

import br.ufsc.src.controle.ServicosControle;
import br.ufsc.src.igu.JanelaPrincipal;
import br.ufsc.src.persistencia.InterfacePersistencia;
import br.ufsc.src.persistencia.Persistencia;

public class Principal {
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			InterfacePersistencia persistencia = new Persistencia();
			ServicosControle controle = new ServicosControle(persistencia);
			JanelaPrincipal janelaPrincipal = new JanelaPrincipal(controle);
			public void run() {
				janelaPrincipal.interaja();
			}
		});
	}
}
