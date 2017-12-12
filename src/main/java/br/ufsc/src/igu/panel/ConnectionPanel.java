package br.ufsc.src.igu.panel;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXSearchField.LayoutStyle;

import javax.swing.GroupLayout.Alignment;

import br.ufsc.src.control.ServiceControl;

public class ConnectionPanel extends AbstractPanel {

	private static final long serialVersionUID = 1L;
	private JTextField passwordTf, userTf, bancoTf;
	private JLabel senhaLabel, usuarioLabel, bancoLabel,t1;
	private JButton testeBtn;

	public ConnectionPanel(ServiceControl controle) {
		super("Database Connection", controle, new JButton("Connect"));
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
				
				.addGroup(layout.createParallelGroup(LEADING)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(usuarioLabel, 0, GroupLayout.DEFAULT_SIZE, 90))
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(userTf))
							)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(senhaLabel, 0, GroupLayout.DEFAULT_SIZE, 90))
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(passwordTf))
							)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(bancoLabel, 0, GroupLayout.DEFAULT_SIZE, 90))
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(bancoTf))
							)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(t1, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(testeBtn))
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(processButton))
							))		
				);

		layout.setVerticalGroup(layout
				.createSequentialGroup()
						.addGroup(layout.createParallelGroup(BASELINE)
								.addComponent(usuarioLabel)
								.addComponent(userTf))
						.addGroup(layout.createParallelGroup(BASELINE)
								.addComponent(senhaLabel)
								.addComponent(passwordTf))
						.addGroup(layout.createParallelGroup(BASELINE)
								.addComponent(bancoLabel)
								.addComponent(bancoTf))
						.addGroup(layout.createParallelGroup(BASELINE)
								.addComponent(t1)
								.addComponent(testeBtn)
								.addComponent(processButton))
			);
	}

	@Override
	public void defineComponents() {
		processButton.setBackground(Color.DARK_GRAY);
		t1 =  new JLabel("");
		senhaLabel 	= new JLabel("Password");
		usuarioLabel =new JLabel("User");
		bancoLabel = new  JLabel("Database");
		passwordTf  = new JTextField();
		userTf = new JTextField();
		bancoTf = new JTextField();
		testeBtn = new JButton("Test connection");
		testeBtn.addActionListener(this);

		bancoTf.setToolTipText("Defina o nome do album");
		processButton.setToolTipText("Clique para processar");
		passwordTf.setToolTipText("Defina o diretorio do Album");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String drive = "org.postgresql.Driver";
		String url = "jdbc:postgresql://localhost/";
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