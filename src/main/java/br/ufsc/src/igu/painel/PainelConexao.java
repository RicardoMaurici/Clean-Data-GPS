package br.ufsc.src.igu.painel;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import br.ufsc.src.controle.ServicosControle;

public class PainelConexao extends PainelAbstrato {

	private static final long serialVersionUID = 1L;
	private JTextField driverTf, urlTf, senhaTf, usuarioTf, bancoTf;
	private JLabel driverLabel, urlLabel, senhaLabel, usuarioLabel, bancoLabel;
	private JButton testeBtn;

	public PainelConexao(ServicosControle controle) {
		super("Conex‹o ao banco de dados", controle, new JButton("Conectar"));
		definaComponentes();
		ajusteComponentes();
	}

	public void ajusteComponentes() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(LEADING)
						.addComponent(driverLabel)
						.addComponent(urlLabel)
						.addComponent(usuarioLabel)
						.addComponent(senhaLabel)
						.addComponent(bancoLabel)
						.addComponent(testeBtn))
						.addGroup(
								layout.createParallelGroup(LEADING)
								.addComponent(driverTf)
								.addComponent(urlTf)
								.addComponent(usuarioTf)
								.addComponent(senhaTf)
								.addComponent(bancoTf)
								.addComponent(botaoProcessa))
								
				);

		layout.linkSize(SwingConstants.HORIZONTAL,botaoProcessa);

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(BASELINE)
						.addComponent(driverLabel)
						.addComponent(driverTf))
						.addGroup(
								layout.createParallelGroup(BASELINE)
								.addComponent(urlLabel)
								.addComponent(urlTf))
								.addGroup(
										layout.createParallelGroup(BASELINE)
										.addComponent(usuarioLabel)
										.addComponent(usuarioTf))
								.addGroup(
										layout.createParallelGroup(BASELINE)
										.addComponent(senhaLabel)
										.addComponent(senhaTf))
												.addGroup(
														layout.createParallelGroup(BASELINE)
														.addComponent(bancoLabel)
														.addComponent(bancoTf))
														.addGroup(
																layout.createParallelGroup(BASELINE)
																.addComponent(testeBtn)
																.addComponent(botaoProcessa)));
	}

	@Override
	public void definaComponentes() {
		driverLabel = new JLabel("Driver Postgres:");
		urlLabel = new JLabel("URL:");
		senhaLabel = new JLabel("Senha");
		usuarioLabel = new JLabel("Usu‡rio");
		bancoLabel = new JLabel("Banco");
		driverTf = new JTextField();
		urlTf = new JTextField();
		senhaTf  = new JTextField();
		usuarioTf = new JTextField();
		bancoTf = new JTextField();
		testeBtn = new JButton("Testar conex‹o");
		testeBtn.addActionListener(this);
		driverTf.setText("org.postgresql.Driver");
		urlTf.setText("jdbc:postgresql://localhost/");
		


		bancoTf.setToolTipText("Defina o nome do album");
		botaoProcessa.setToolTipText("Clique para processar");
		senhaTf.setToolTipText("Defina o diretorio do Album");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String drive = driverTf.getText();
		String url = urlTf.getText();
		String usuario = usuarioTf.getText();
		String senha = senhaTf.getText();
		String banco = bancoTf.getText();
		if (e.getSource() == testeBtn) {
			if(this.controle.testarBanco(drive, url, usuario, senha, banco))
				JOptionPane.showMessageDialog(null, "Conex‹o estabelecida", "Conex‹o ao banco", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(null, "Falha ao conectar ao banco de dados",  "Conex‹o ao banco",0);
		} else if (e.getSource() == botaoProcessa) {
			this.controle.criaConexao(drive, url, usuario, senha, banco);
			if(this.controle.testarBanco(drive, url, usuario, senha, banco)){
				JOptionPane.showMessageDialog(null, "Conex‹o estabelecida", "Conex‹o ao banco", JOptionPane.INFORMATION_MESSAGE);
				limpeTela();
			}else
				JOptionPane.showMessageDialog(null, "Falha ao conectar ao banco de dados",  "Conex‹o ao banco",0);
		}
		
	}

}
