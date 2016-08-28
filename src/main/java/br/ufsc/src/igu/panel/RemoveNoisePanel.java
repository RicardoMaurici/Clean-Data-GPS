package br.ufsc.src.igu.panel;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
	private JLabel tableLabel, speedLabel, minPointsLabel, distancePointsLabel, dbscanLabel, meanFilterLabel, medianFilterLabel;
	private JTextField tableTF, speedTF, minPointsTF, distancePointsTF;
	private JButton tableBtn;
	private JTable table1;
	private JScrollPane table;
	private JRadioButton fromFirst, fromSecondLookingBackward, dbscanRB, meanFilterRB, medianFilterRB;
	private JSeparator sep1, sep2, sep3, sep4, sep5, sep6, sep7, sep8, sep9;
	

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
		
		tableTF.setText("traj_ricardo3");
		
		fromFirst = new JRadioButton("From First Looking Forward");
		fromSecondLookingBackward = new JRadioButton("From Second Looking Backward");
		dbscanRB = new JRadioButton("DBSCAN");
		meanFilterRB = new JRadioButton("Mean Filter");
		medianFilterRB = new JRadioButton("Median Filter");
		ButtonGroup group = new ButtonGroup();
		group.add(fromFirst);
		group.add(fromSecondLookingBackward);
		group.add(dbscanRB);
		group.add(meanFilterRB);
		group.add(medianFilterRB);
		
		speedLabel = new JLabel("Ignore speed up in m/s");
		speedTF = new JTextField();
		
		dbscanLabel = new JLabel("-- DBSCAN --");
		minPointsLabel = new JLabel("Min. Points");
		minPointsTF = new JTextField();
		minPointsTF.setToolTipText("Minimum points to DBSCAN");
		distancePointsLabel = new JLabel("Dist. points");
		distancePointsTF = new JTextField();
		distancePointsTF.setToolTipText("Distance between points in meters");
		
		medianFilterLabel = new JLabel("-- Median Filter --");
		
		meanFilterLabel = new JLabel("-- Mean Filter --");
		
		
		Object [] columnNames = new Object[]{ "Column", "Kind" };
        Object [][] data        = new Object[][]{};
        
        DefaultTableModel tab = new MyTableModel( data,columnNames, true );
        table1 = new JTable(tab);
        table = new JScrollPane(table1);
        table1.setRowHeight( 25 );
        setUpColumnComboBox(table1, table1.getColumnModel().getColumn(1));
        
        sep1 = new JSeparator(SwingConstants.HORIZONTAL);
		sep2 = new JSeparator(SwingConstants.HORIZONTAL);
		sep3 = new JSeparator(SwingConstants.HORIZONTAL);
		sep4 = new JSeparator(SwingConstants.HORIZONTAL);
		sep5 = new JSeparator(SwingConstants.HORIZONTAL);
		sep6 = new JSeparator(SwingConstants.HORIZONTAL);
		sep7 = new JSeparator(SwingConstants.HORIZONTAL);
		sep8 = new JSeparator(SwingConstants.HORIZONTAL);
		sep9 = new JSeparator(SwingConstants.HORIZONTAL);
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
										.addComponent(sep1)
										.addComponent(dbscanLabel)
										.addComponent(minPointsLabel)
										.addComponent(distancePointsLabel)
										.addComponent(sep4)
										.addComponent(meanFilterLabel)
										.addComponent(sep7)
										.addComponent(medianFilterLabel)
								)
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(speedTF)
										.addComponent(sep2)
										.addComponent(minPointsTF)
										.addComponent(distancePointsTF)
										.addComponent(sep5)
										.addComponent(sep8)
								)
								.addGroup(layout.createParallelGroup(LEADING)
										.addComponent(fromFirst)
										.addComponent(fromSecondLookingBackward)
										.addComponent(sep3)
										.addComponent(dbscanRB)
										.addComponent(sep6)
										.addComponent(meanFilterRB)
										.addComponent(sep9)
										.addComponent(medianFilterRB)
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
						.addComponent(sep1)
						.addComponent(sep2)
						.addComponent(sep3))
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(dbscanLabel))
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(minPointsLabel)
						.addComponent(minPointsTF)
						.addComponent(dbscanRB))
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(distancePointsLabel)
						.addComponent(distancePointsTF))
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(sep4)
						.addComponent(sep5)
						.addComponent(sep6))
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(meanFilterLabel))
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(meanFilterRB))
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(sep7)
						.addComponent(sep8)
						.addComponent(sep9))
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(medianFilterLabel))
				.addGroup(layout.createParallelGroup(BASELINE)
						.addComponent(medianFilterRB))
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
		
		if(!(fromFirst.isSelected() || fromSecondLookingBackward.isSelected() || dbscanRB.isSelected() || meanFilterRB.isSelected() || medianFilterRB.isSelected())){
			JOptionPane.showMessageDialog(null,"A method should be selected","Data Clean", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	
		if((fromFirst.isSelected() || fromSecondLookingBackward.isSelected()) && !Utils.isStringEmpty(speedTF.getText()) && !Utils.isNumeric(speedTF.getText())){
			JOptionPane.showMessageDialog(null,"Speed should be a number","Data Clean", JOptionPane.ERROR_MESSAGE);
			speedTF.setText("");
			speedTF.requestFocus(true);
			return null;
		}
		
		if((fromFirst.isSelected() || fromSecondLookingBackward.isSelected()) && Utils.isStringEmpty(speedTF.getText())){
			JOptionPane.showMessageDialog(null,"You should set a speed value","Data Clean", JOptionPane.ERROR_MESSAGE);
			speedTF.requestFocus(true);
			return null;
		}
		
		if(dbscanRB.isSelected() && (Utils.isStringEmpty(minPointsTF.getText()) || !Utils.isNumeric(minPointsTF.getText()))){
			JOptionPane.showMessageDialog(null,"You should set a NUMBER to minimum value of points","Data Clean", JOptionPane.ERROR_MESSAGE);
			minPointsTF.setText("");
			minPointsTF.requestFocus(true);
			return null;
		}
		
		if(dbscanRB.isSelected() && (Utils.isStringEmpty(distancePointsTF.getText()) || !Utils.isNumeric(distancePointsTF.getText()))){
			JOptionPane.showMessageDialog(null,"You should set a NUMBER to distance between points","Data Clean", JOptionPane.ERROR_MESSAGE);
			distancePointsTF.setText("");
			distancePointsTF.requestFocus(true);
			return null;
		}
		
		String tableName = tableTF.getText();

		String speed = Utils.isNumeric(speedTF.getText()) ? speedTF.getText() : null;
		int minPoints = Utils.isNumeric(minPointsTF.getText()) ? Integer.parseInt(minPointsTF.getText()) : null;
		double distPoints = Utils.isNumeric(distancePointsTF.getText()) ? Double.parseDouble(distancePointsTF.getText()) : null;
		
		ConfigTraj configTraj= new ConfigTraj(tableData, tableName);
		configTraj.setRemoveNoiseFromFirst(fromFirst.isSelected());
		configTraj.setRemoveNoiseFromSecond(fromSecondLookingBackward.isSelected());
		configTraj.setDbscan(dbscanRB.isSelected());
		configTraj.setMeanFilter(meanFilterRB.isSelected());
		configTraj.setMedianFilter(medianFilterRB.isSelected());
		
		configTraj.setSpeed(speed);
		configTraj.setMinPoints(minPoints);
		configTraj.setDistancePoints(distPoints);
		
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