package br.ufsc.src.igu.panel;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import br.ufsc.src.control.ServiceControl;
import br.ufsc.src.control.Utils;
import br.ufsc.src.control.dataclean.ConfigTraj;
import br.ufsc.src.control.exception.BrokeTrajectoryException;
import br.ufsc.src.persistencia.exception.AddColumnException;
import br.ufsc.src.persistencia.exception.DBConnectionException;
import br.ufsc.src.persistencia.exception.GetTableColumnsException;

public class RemoveNoisePanel extends AbstractPanel{
	
	private static final long serialVersionUID = 1L;
	private JLabel tableLabel, speedLabel;
	private JTextField tableTF, speedTF;
	private JButton tableBtn;
	private JTable table1;
	private JScrollPane table;
	private JCheckBox fromFirst, fromSecondLookingBackward ;

	public RemoveNoisePanel(ServiceControl controle) {
		
		super("Data Clean - Remove Noise", controle, new JButton("Start"));
		defineComponents();
		adjustComponents();
	}

	@Override
	public void defineComponents() {
		
		processButton.setBackground(Color.DARK_GRAY);
		
		tableLabel = new JLabel("Table name");
		tableTF = new JTextField();
		tableBtn = new JButton("Find");
		tableBtn.addActionListener(this);
		
		fromFirst = new JCheckBox("From First Looking Forward");
		fromSecondLookingBackward = new JCheckBox("From Second Looking Backward");
		
		speedLabel = new JLabel("Ignore speed up in m/s");
		speedTF = new JTextField();
		
		Object [] columnNames = new Object[]{ "Column", "Kind" };
        Object [][] data        = new Object[][]{};
        
        DefaultTableModel tab = new MyTableModel( data,columnNames, true );
        table1 = new JTable(tab);
        table = new JScrollPane(table1);
        table1.setRowHeight( 25 );
        setUpColumnComboBox(table1, table1.getColumnModel().getColumn(1));
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
						.addComponent(tableLabel))
				.addGroup(layout.createParallelGroup(LEADING)
						.addComponent(tableTF)
						.addComponent(table)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(speedLabel)
								)
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(speedTF)
								)
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(fromFirst)
										.addComponent(fromSecondLookingBackward)
								)		
						)
				)
				.addGroup(layout.createParallelGroup(LEADING)
						.addComponent(tableBtn)
						.addComponent(processButton)
						)

		);

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(tableLabel)
						.addComponent(tableTF)
						.addComponent(tableBtn))
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(table))
				.addGroup(layout.createParallelGroup(BASELINE)
						.addGroup(layout.createParallelGroup()
								.addComponent(speedLabel)
								.addComponent(speedTF)
								.addComponent(fromFirst)	
						)
				)
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(fromSecondLookingBackward)
				)
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(processButton))
		);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == tableBtn){
			try {
				if(tableTF.getText().length() != 0){
					ArrayList<String> columns = control.getTableColumns(tableTF.getText());
					DefaultTableModel model = (DefaultTableModel) table1.getModel();
					for (String col : columns) {
						String cl = isKind(col) ? col.toUpperCase() : null; 
						model.addRow(new Object[]{col,cl,null,null});
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
			ConfigTraj configTraj = getDataFromWindow();
			if(configTraj != null){
				try {
					long startTime = System.currentTimeMillis();
					control.removeNoise(configTraj);   
					long endTime   = System.currentTimeMillis();
					long totalTime = endTime - startTime;
					JOptionPane.showMessageDialog(null, "Remove Noise \n"+Utils.getDurationBreakdown(totalTime),
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
	
	 private ConfigTraj getDataFromWindow() {
		Object[][] tableData = getTableData();
		
		if(Utils.isStringEmpty(tableTF.getText())){
			JOptionPane.showMessageDialog(null,"Table name is empty","Data Clean", JOptionPane.ERROR_MESSAGE);
			tableTF.requestFocus(true);
			return null;
		}
		if(tableData.length == 0){
			JOptionPane.showMessageDialog(null,"You should click Find to list the table's columns","Data Clean", JOptionPane.ERROR_MESSAGE);
			tableBtn.requestFocus(true);
			return null;
	 	}
	
		if(!Utils.isStringEmpty(speedTF.getText()) && !Utils.isNumeric(speedTF.getText())){
			JOptionPane.showMessageDialog(null,"Speed should be a number","Data Clean", JOptionPane.ERROR_MESSAGE);
			speedTF.requestFocus(true);
			return null;
		}
		
		if(Utils.isStringEmpty(speedTF.getText())){
			JOptionPane.showMessageDialog(null,"You should set a speed value","Data Clean", JOptionPane.ERROR_MESSAGE);
			speedTF.requestFocus(true);
			return null;
		}
		
		if(fromFirst.isSelected() && fromSecondLookingBackward.isSelected()){
			JOptionPane.showMessageDialog(null,"Choice only one method to remove noise","Data Clean", JOptionPane.ERROR_MESSAGE);
			fromFirst.requestFocus(true);
			return null;
		}
		
		if(!fromFirst.isSelected() && !fromSecondLookingBackward.isSelected()){
			JOptionPane.showMessageDialog(null,"You should select a method to remove noise","Data Clean", JOptionPane.ERROR_MESSAGE);
			fromFirst.requestFocus(true);
			return null;
		}
		
		String tableName = tableTF.getText();

		String speed = Utils.isNumeric(speedTF.getText()) ? speedTF.getText() : null;
		
		ConfigTraj configTraj= new ConfigTraj(tableData, tableName);
		configTraj.setRemoveNoiseFromFirst(fromFirst.isSelected());
		configTraj.setRemoveNoiseFromSecond(fromSecondLookingBackward.isSelected());
		configTraj.setSpeed(speed);
		
		return configTraj;
	}
	 
	private Object[][] getTableData () {
		DefaultTableModel dtm = (DefaultTableModel) table1.getModel();
		int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
		Object[][] tableData = new Object[nRow][nCol];
		for (int i = 0 ; i < nRow ; i++)
			for (int j = 0 ; j < nCol ; j++)
				tableData[i][j] = dtm.getValueAt(i,j);
		return tableData;
	}

	public void setUpColumnComboBox(JTable table, TableColumn column) {
			//Set up the editor for the sport cells.
		 		
			JComboBox comboBox = new JComboBox(getKinds());
			column.setCellEditor(new DefaultCellEditor(comboBox));
			//Set up tool tips for the cells.
			DefaultTableCellRenderer renderer =
			new DefaultTableCellRenderer();
			renderer.setToolTipText("Click for combo box");
			column.setCellRenderer(renderer);
		}

	private String[] getKinds() {
		String[] kinds = new String[] {
				"GID"
				,"TID" 			 	
			 	,"TIMESTAMP"
			 	,"LAT"
			 	,"LON"
			 	,"GEOM"
			 	,"BOOLEAN STATUS"
			 	,"ACCURACY"
			 	,"SPEED"
			 	,""
		};
		return kinds;
	}
	
	private boolean isKind(String kind){
		String[] kinds = getKinds();
		for (String kd : kinds) {
			if(kd.equalsIgnoreCase(kind))
				return true;
		}
		return false;
	}

}