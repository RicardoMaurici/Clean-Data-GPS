package br.ufsc.src.igu.panel;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractButton;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.prompt.PromptSupport;

import br.ufsc.src.control.ServiceControl;
import br.ufsc.src.control.Utils;
import br.ufsc.src.persistencia.exception.AddBatchException;
import br.ufsc.src.persistencia.exception.CreateSequenceException;
import br.ufsc.src.persistencia.exception.CreateStatementException;
import br.ufsc.src.persistencia.exception.CreateTableException;
import br.ufsc.src.persistencia.exception.DBConnectionException;
import br.ufsc.src.persistencia.exception.ExecuteBatchException;
import br.ufsc.src.persistencia.exception.FileNFoundException;
import br.ufsc.src.persistencia.exception.GetSequenceException;
import br.ufsc.src.persistencia.exception.LoadDataFileException;
import br.ufsc.src.persistencia.exception.SyntaxException;
import br.ufsc.src.persistencia.exception.TimeStampException;
import br.ufsc.src.persistencia.exception.UpdateGeomException;
import br.ufsc.src.persistencia.fonte.Diretorio;
import br.ufsc.src.persistencia.fonte.TrajetoriaBruta;


public class LoadPanel extends AbstractPanel {

	private static final long serialVersionUID = 1L;
	private JLabel diretorioLabel, igArqLabel, extLabel, igDirLabel, sridAtualLabel, sridNovoLabel, tabelaLabel;
	private JTextField diretorioTf, igArqTf, igDirTf, extTf, tabelaBancoTf, sridAtualTf, sridNovoTf ;
	private JButton diretorioBtn;
	private JCheckBox incluirMetadados, igExt, tid, gid;
	
	public LoadPanel(ServiceControl controle) {
		super("Load JSON/GPX/KML/WKT files", controle, new JButton("Load"));
		defineComponents();
		adjustComponents();
	}

	public void defineComponents() {
		diretorioLabel = new JLabel("Dir/File");
		sridAtualLabel = new JLabel("SRID");
		sridNovoLabel = new JLabel("new SRID");
		tabelaLabel = new JLabel("Table name");
		igArqLabel = new JLabel("Ig. Files");
		extLabel = new JLabel("Ext.");
		igDirLabel = new JLabel("Ig. dir.");

		diretorioTf = new JTextField();
		tabelaBancoTf = new JTextField();
		sridAtualTf = new JTextField();
		sridNovoTf = new JTextField();
		igArqTf = new JTextField();
		igDirTf = new JTextField();
		extTf = new JTextField();
		
		diretorioBtn = new JButton("Select");

		incluirMetadados = new JCheckBox("Add Metadata");
		igExt = new JCheckBox("Ignore");
		tid = new JCheckBox("Generate TID");
		gid = new JCheckBox("Generate GID");
		
		extTf.setText("pdf,zip,txt,csv,tsv");
		igExt.setSelected(true);

		diretorioBtn.addActionListener(this);

		processButton.setBackground(Color.DARK_GRAY);
		diretorioBtn
				.setToolTipText("Click to select a directory/file");
		processButton.setToolTipText("Click to load");
		
		diretorioTf.setText("/Users/rogerjames/Desktop/testes/");
		sridAtualTf.setText("2100");
		sridNovoTf.setText("900913");
	}

	public void adjustComponents() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(LEADING)
								.addComponent(diretorioLabel))
				.addGroup(
						layout.createParallelGroup(LEADING)
								.addComponent(diretorioTf)
								.addGroup(
										layout.createSequentialGroup()
												.addGroup(
														layout.createParallelGroup(
																LEADING)													
																.addComponent(
																		sridAtualLabel)
																.addComponent(
																		extLabel)
																.addComponent(
																		igDirLabel)
																.addComponent(
																		tabelaLabel))
												.addGroup(
														layout.createParallelGroup(
																LEADING)
																.addComponent(
																		sridAtualTf)
																.addComponent(
																		extTf)
																.addComponent(
																		igDirTf)
																.addComponent(
																		tabelaBancoTf)
																.addComponent(gid))
												.addGroup(
														layout.createParallelGroup(
																LEADING)
																.addComponent(
																		sridNovoLabel)
																.addComponent(
																		igExt)
																.addComponent(
																		igArqLabel)
																.addComponent(
																		incluirMetadados))
												.addGroup(
														layout.createParallelGroup(
																LEADING)
																.addComponent(
																		sridNovoTf)
																.addComponent(
																		igArqTf)
																.addComponent(tid))
																)
						)
						
				.addGroup(
						layout.createParallelGroup(LEADING)
								.addComponent(diretorioBtn)
								.addComponent(processButton)));

		layout.linkSize(SwingConstants.HORIZONTAL, processButton, diretorioBtn);

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(BASELINE)
								.addComponent(diretorioLabel)
								.addComponent(diretorioTf)
								.addComponent(diretorioBtn))
				.addGroup(
						layout.createParallelGroup(LEADING)
								.addGroup(
										layout.createSequentialGroup()								
												.addGroup(
														layout.createParallelGroup(
																BASELINE)
																.addComponent(
																		sridAtualLabel)
																.addComponent(
																		sridAtualTf)
																.addComponent(
																		sridNovoLabel)
																.addComponent(
																		sridNovoTf))
												.addGroup(
														layout.createParallelGroup(
																BASELINE)
																.addComponent(
																		extLabel)
																.addComponent(
																		extTf)
																.addComponent(
																		igExt))
												.addGroup(
														layout.createParallelGroup(
																BASELINE)
																.addComponent(
																		igDirLabel)
																.addComponent(
																		igDirTf)
																.addComponent(
																		igArqLabel)
																.addComponent(
																		igArqTf))
												.addGroup(
														layout.createParallelGroup(
																BASELINE)
																.addComponent(
																		tabelaLabel)
																.addComponent(
																		tabelaBancoTf)
																.addComponent(
																		incluirMetadados))
												.addGroup(layout.createParallelGroup(BASELINE)
																.addComponent(gid)
																.addComponent(tid))
												)
								.addComponent(processButton))
				);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == diretorioBtn) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fc.setAcceptAllFileFilterUsed(true);
			int returnVal = fc.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION)
				diretorioTf.setText(fc.getSelectedFile().getAbsolutePath());
		}else if (e.getSource() == processButton) {
			if (!control.testConnection())
				JOptionPane.showMessageDialog(null, "PAUUUU",
						"DB connection", JOptionPane.ERROR_MESSAGE);
			else {	
				if (verificaEntradas()) {
					String inLocal = diretorioTf.getText();
					String inTabela = tabelaBancoTf.getText();
					boolean inMetadata = incluirMetadados.isSelected();
					boolean inGID = gid.isSelected();
					boolean inTID = tid.isSelected();
					int inSRIDAtual, inSRIDNovo = 0;
					try{
						inSRIDAtual = Integer.parseInt(sridAtualTf.getText());
					}catch(NumberFormatException ex){
						JOptionPane.showMessageDialog(null,
								"SRID n�o � um n�mero, informe o SRID somente em n�meros",
								"Carregar documento", JOptionPane.ERROR_MESSAGE);
						sridAtualTf.requestFocus(true);
						return;
					}
					if(sridNovoTf.getText().length() != 0){
						try{
							inSRIDNovo = Integer.parseInt(sridNovoTf.getText());
						}catch(NumberFormatException ex){
							JOptionPane.showMessageDialog(null,
									"SRID n�o � um n�mero, informe o SRID somente em n�meros",
									"Carregar documento", JOptionPane.ERROR_MESSAGE);
							sridNovoTf.requestFocus(true);
							return;
						}
					}else
						inSRIDNovo = inSRIDAtual;
					
					String inExt = (extTf.getText().length() > 0 ) ? extTf.getText() : null;
					boolean inIgExt = igExt.isSelected();
					String inIgDir = (igDirTf.getText().length() > 0) ? igDirTf.getText() : null;
					String inIgArq = (igArqTf.getText().length() > 0) ? igArqTf.getText() : null;
					
					Object [][] tableColumns        = new Object[][]{
						{"tid",  "", EnumTypes.NUMERIC.toString(),""}
						, {"lat",  "", EnumTypes.NUMERIC.toString(),""}
						, {"lon",  "", EnumTypes.NUMERIC.toString(), ""}
						, {"timestamp", "", EnumTypes.TIMESTAMP.toString(), ""}
						, {"geom", "", EnumTypes.POINT.toString(), ""} 
					};

					TrajetoriaBruta tb = new TrajetoriaBruta(0, null, null, null, inTabela, inSRIDAtual, inSRIDNovo, inMetadata, tableColumns, inGID, inTID);
					Diretorio dir = definicoesDiretorio(inLocal, inExt, inIgExt, inIgDir, inIgArq);
				
					try {
						control.createTable(tb); 
					} catch (SyntaxException e1){
						JOptionPane.showMessageDialog(null,e1.getMsg(),"Loading data", JOptionPane.ERROR_MESSAGE);
						tabelaBancoTf.requestFocus(true);
						return;
					} catch (CreateTableException e1) {
						JOptionPane.showMessageDialog(null,"Error creating table: "+e1.getMsg(),"Loading data", JOptionPane.ERROR_MESSAGE);
						tabelaBancoTf.requestFocus(true);
						return;
					} catch (DBConnectionException e1) {
						JOptionPane.showMessageDialog(null,"Error connecting to DB: "+e1.getMsg(),"Loading data", JOptionPane.ERROR_MESSAGE);
						tabelaBancoTf.requestFocus(true);
						return;
					}
					
					
					try {
						long startTime = System.currentTimeMillis();
						control.loadData(tb, dir);   
						long endTime   = System.currentTimeMillis();
						long totalTime = endTime - startTime;
						JOptionPane.showMessageDialog(null, "Data loadaded \n"+Utils.getDurationBreakdown(totalTime),
								"Loading data",
								JOptionPane.INFORMATION_MESSAGE);
						clearWindow();
					} catch (TimeStampException e1) {
						JOptionPane.showMessageDialog(null, "Error converting timestamp: "+e1.getMsg(),
								"Loading data", JOptionPane.ERROR_MESSAGE);
					} catch (LoadDataFileException e1) {
						JOptionPane.showMessageDialog(null, "Error loading file: "+e1.getMsg(),
								"Loading data", JOptionPane.ERROR_MESSAGE);
					} catch (GetSequenceException e1) {
						JOptionPane.showMessageDialog(null, "Error getting sequence: "+e1.getMsg(),
								"Loading data", JOptionPane.ERROR_MESSAGE);
					} catch (CreateSequenceException e1) {
						JOptionPane.showMessageDialog(null, "Error creating sequence: "+e1.getMsg(),
								"Loading data", JOptionPane.ERROR_MESSAGE);
					} catch (UpdateGeomException e1) {
						JOptionPane.showMessageDialog(null, "Error updating geom: "+e1.getMsg(),
								"Loading data", JOptionPane.ERROR_MESSAGE);
					} catch (CreateStatementException e1) {
						JOptionPane.showMessageDialog(null, "Error creating statement: "+e1.getMsg(),
								"Loading data", JOptionPane.ERROR_MESSAGE);
					} catch (AddBatchException e1) {
						JOptionPane.showMessageDialog(null, "Error adding batch: "+e1.getMsg(),
								"Loading data", JOptionPane.ERROR_MESSAGE);
					} catch (FileNFoundException e1) {
						JOptionPane.showMessageDialog(null, "Error finding file: "+e1.getMsg(),
								"Loading data", JOptionPane.ERROR_MESSAGE);
					} catch (ExecuteBatchException e1) {
						JOptionPane.showMessageDialog(null, "Error executing batch: "+e1.getMsg(),
								"Loading data", JOptionPane.ERROR_MESSAGE);
					} catch (DBConnectionException e1) {
						JOptionPane.showMessageDialog(null,"Error connecting to DB: "+e1.getMsg(),"Loading data", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	private Diretorio definicoesDiretorio(String url, String inExt, boolean inIgExt,
			String inIgDir, String inIgArq) {
		Diretorio dir = new Diretorio();
		String igExt[] = (inExt == null) ? new String[0] : inExt.split(",");
		String igDir[] = (inIgDir == null) ? new String[0] : inIgDir.split(",");
		String igArq[] = (inIgArq == null) ? new String[0] : inIgArq.split(",");
		dir.setUrl(url);
		dir.setExtension(new ArrayList<String>(Arrays.asList(igExt)));
		dir.setIgFolder(new ArrayList<String>(Arrays.asList(igDir)));
		dir.setIgFile(new ArrayList<String>(Arrays.asList(igArq)));
		dir.setIgExtension(inIgExt);
		return dir;
	}

	private boolean verificaEntradas() {
		if (diretorioTf.getText().length() == 0) {
			JOptionPane.showMessageDialog(null,
					"Informe o diret�rio ou arquivo a ser carregado",
					"Carregar documento", JOptionPane.ERROR_MESSAGE);
			diretorioTf.requestFocus(true);
			return false;
		}else if (tabelaBancoTf.getText().length() == 0) {
			JOptionPane
					.showMessageDialog(
							null,
							"Informe o nome da tabela no banco de dados que os dados ser�o inseridos",
							"Carregar documento", JOptionPane.ERROR_MESSAGE);
			tabelaBancoTf.requestFocus(true);
			return false;
		} else if (sridAtualTf.getText().length() == 0) {
			JOptionPane.showMessageDialog(null,
					"Informe o SRID das coordenadas", "Carregar documento",
					JOptionPane.ERROR_MESSAGE);
			sridAtualTf.requestFocus(true);
			return false;
		}
		return true;
	}
}