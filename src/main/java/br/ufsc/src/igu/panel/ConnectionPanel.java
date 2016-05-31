package br.ufsc.src.igu.panel;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import br.ufsc.src.control.ServiceControl;

public class ConnectionPanel extends AbstractPanel {

	private static final long serialVersionUID = 1L;
	private JTextField driverTf, urlTf, passwordTf, userTf, bancoTf;
	private JLabel driverLabel, urlLabel, senhaLabel, usuarioLabel, bancoLabel;
	private JButton testeBtn;

	public ConnectionPanel(ServiceControl controle) {
		super("Connection to DB", controle, new JButton("Connect"));
		defineComponents();
		adjustComponents();
	}
	
	@Override
	public void adjustComponents() {
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
								.addComponent(userTf)
								.addComponent(passwordTf)
								.addComponent(bancoTf)
								.addComponent(processButton))
								
				);

		layout.linkSize(SwingConstants.HORIZONTAL,processButton);

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
										.addComponent(userTf))
								.addGroup(
										layout.createParallelGroup(BASELINE)
										.addComponent(senhaLabel)
										.addComponent(passwordTf))
												.addGroup(
														layout.createParallelGroup(BASELINE)
														.addComponent(bancoLabel)
														.addComponent(bancoTf))
														.addGroup(
																layout.createParallelGroup(BASELINE)
																.addComponent(testeBtn)
																.addComponent(processButton)));
	}

	@Override
	public void defineComponents() {
		driverLabel = new JLabel("Driver Postgres:");
		urlLabel = new JLabel("URL:");
		senhaLabel = new JLabel("Password");
		usuarioLabel = new JLabel("User");
		bancoLabel = new JLabel("Database");
		driverTf = new JTextField();
		urlTf = new JTextField();
		passwordTf  = new JTextField();
		userTf = new JTextField();
		bancoTf = new JTextField();
		testeBtn = new JButton("Test connection");
		testeBtn.addActionListener(this);
		driverTf.setText("org.postgresql.Driver");
		urlTf.setText("jdbc:postgresql://localhost/");

		bancoTf.setToolTipText("Defina o nome do album");
		processButton.setToolTipText("Clique para processar");
		passwordTf.setToolTipText("Defina o diretorio do Album");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String drive = driverTf.getText();
		String url = urlTf.getText();
		String usuario = userTf.getText();
		String senha = passwordTf.getText();
		String banco = bancoTf.getText();
		if (e.getSource() == testeBtn) {
			if(this.control.testarBanco(drive, url, usuario, senha, banco))
				JOptionPane.showMessageDialog(null, "Connection established", "Connection to DB", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(null, "Connection to DB failed",  "Connection to DB",0);
		} else if (e.getSource() == processButton) {
			this.control.criaConexao(drive, url, usuario, senha, banco);
			if(this.control.testarBanco(drive, url, usuario, senha, banco)){
				JOptionPane.showMessageDialog(null, "Connection established", "Connection to DB", JOptionPane.INFORMATION_MESSAGE);
				clearWindow();
			}else
				JOptionPane.showMessageDialog(null, "Connection to DB failed",  "Connection to DB",0);
		}
		
	}

}