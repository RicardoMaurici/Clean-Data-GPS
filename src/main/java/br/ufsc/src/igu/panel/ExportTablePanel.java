package br.ufsc.src.igu.panel;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import br.ufsc.src.control.ServiceControl;
import br.ufsc.src.control.Utils;
import br.ufsc.src.persistencia.exception.DBConnectionException;
import br.ufsc.src.persistencia.exception.GetTableColumnsException;

public class ExportTablePanel extends AbstractPanel{

		
		private static final long serialVersionUID = 1L;
		private JLabel diretorioLabel, tableLabel;
		private JTextField diretorioTf, tableTF;
		private JButton diretorioBtn, tableBtn;
		private JTable table1;
		private JScrollPane table;

		public ExportTablePanel(ServiceControl control) {
			
			super("Export table to CSV", control, new JButton("Export"));
			defineComponents();
			adjustComponents();
		}

		public void defineComponents() {
			
			processButton.setBackground(Color.DARK_GRAY);
			
			diretorioLabel = new JLabel("Local");
			diretorioTf = new JTextField();
			diretorioBtn = new JButton("Select");
			diretorioBtn.addActionListener(this);
			
			tableLabel = new JLabel("Table name");
			tableTF = new JTextField();
			tableBtn = new JButton("Find");
			tableBtn.addActionListener(this);
			
			tableTF.setText("traj_ricardo3");
			
			Object [] columnNames = new Object[]{ "Column"};
	        Object [][] data        = new Object[][]{};
	        
	        DefaultTableModel tab = new MyTableModel( data,columnNames, true );
	        table1 = new JTable(tab);
	        table = new JScrollPane(table1);
	        table1.setRowHeight( 25 );
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
							.addComponent(diretorioLabel)
							.addComponent(tableLabel))
					.addGroup(layout.createParallelGroup(LEADING)
							.addComponent(diretorioTf)
							.addComponent(tableTF)
							.addComponent(table)
					)
					.addGroup(layout.createParallelGroup(LEADING)
							.addComponent(diretorioBtn)
							.addComponent(tableBtn)
							.addComponent(processButton)
							)

			);

			layout.setVerticalGroup(layout
					.createSequentialGroup()
					.addGroup(layout.createParallelGroup(BASELINE)
							.addComponent(diretorioLabel)
							.addComponent(diretorioTf)
							.addComponent(diretorioBtn))
					.addGroup(layout.createParallelGroup(BASELINE)
							.addComponent(tableLabel)
							.addComponent(tableTF)
							.addComponent(tableBtn))
					.addGroup(layout.createParallelGroup(BASELINE)
							.addComponent(table))
					.addGroup(layout.createParallelGroup(BASELINE)
							.addComponent(processButton))
			);
			
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == diretorioBtn) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setAcceptAllFileFilterUsed(true);
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION)
					diretorioTf.setText(fc.getSelectedFile().getAbsolutePath());
			}else if(e.getSource() == tableBtn){
				try {
					if(tableTF.getText().length() != 0){
						ArrayList<String> columns = control.getTableColumns(tableTF.getText());
						DefaultTableModel model = (DefaultTableModel) table1.getModel();
						for (String col : columns) {
							model.addRow(new Object[]{col,null,null,null});
						}
					}
				} catch (DBConnectionException e1) {
					JOptionPane.showMessageDialog(null,"DB connection error: "+e1.getMsg(),"Data Clean", JOptionPane.ERROR_MESSAGE);
					return;
				} catch (GetTableColumnsException e1) {
					JOptionPane.showMessageDialog(null,"Error to get columns name: "+e1.getMsg(),"Data Clean", JOptionPane.ERROR_MESSAGE);
					tableTF.requestFocus(true);
					return;
				}
			}else if(e.getSource() == processButton){
				if (!control.testConnection())
					JOptionPane.showMessageDialog(null, "Connection failed",
							"DB connection", JOptionPane.ERROR_MESSAGE);
				if(checkDataFromWindow()){
					try {
						long startTime = System.currentTimeMillis();
						control.exportTable(diretorioTf.getText(), tableTF.getText());   
						long endTime   = System.currentTimeMillis();
						long totalTime = endTime - startTime;
						JOptionPane.showMessageDialog(null, "Export table\n"+Utils.getDurationBreakdown(totalTime),
								"Data Clean",
								JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "DB connection error: "+e1.getMessage(),
								"Data Clean", JOptionPane.ERROR_MESSAGE);
					} 
					clearWindow();
				}
			}
		}
		
		private boolean checkDataFromWindow(){
			try{
				File file = new File(diretorioTf.getText());
				if(!file.isDirectory()){
					JOptionPane.showMessageDialog(null,"Path should be a folder: ","Export table", JOptionPane.ERROR_MESSAGE);
					diretorioTf.requestFocus(true);
					return false;
				}
			}catch(Exception e){
				JOptionPane.showMessageDialog(null,"Path should be a folder: ","Export table", JOptionPane.ERROR_MESSAGE);
				diretorioTf.requestFocus(true);
			}
			return true;
		}
	
}