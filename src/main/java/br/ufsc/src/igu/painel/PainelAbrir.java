package br.ufsc.src.igu.painel;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.prompt.PromptSupport;

import br.ufsc.src.controle.ServicosControle;
import br.ufsc.src.controle.Utils;
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


public class PainelAbrir extends PainelAbstrato {

	private static final long serialVersionUID = 1L;
	private JLabel diretorioLabel, igLinhaLabel, separadorLabel,
			formatoDataLabel, formatoHoraLabel, igArqLabel, extLabel,
			igDirLabel, sridAtualLabel, sridNovoLabel, tabelaLabel;
	private JTextField diretorioTf, igLinhaTf, separadorTf, formatoDataTf,
			igArqTf, igDirTf, extTf, formatoHoraTf, tabelaBancoTf, sridAtualTf,
			sridNovoTf, novaColunaTf, posicaoTf, typeSizeTf ;
	private JButton diretorioBtn, addColunaBtn;
	private JCheckBox incluirMetadados, igExt, tid, gid;
	private JTable table1;
	private JScrollPane table;
	private JComboBox tiposCb;
	
	public PainelAbrir(ServicosControle controle) {
		super("Carregar documento", controle, new JButton("Carregar"));
		definaComponentes();
		ajusteComponentes();
	}

	public void definaComponentes() {
		diretorioLabel = new JLabel("Local");
		igLinhaLabel = new JLabel("N� linhas ig");
		separadorLabel = new JLabel("Separador");
		formatoDataLabel = new JLabel("Formato data");
		formatoHoraLabel = new JLabel("Formato hora");
		sridAtualLabel = new JLabel("SRID atual");
		sridNovoLabel = new JLabel("SRID novo");
		tabelaLabel = new JLabel("Tabela");
		igArqLabel = new JLabel("Ig. Arq.");
		extLabel = new JLabel("Ext.");
		igDirLabel = new JLabel("Ig. dir.");

		diretorioTf = new JTextField();
		igLinhaTf = new JTextField();
		igLinhaTf.setSize(getMinimumSize());
		separadorTf = new JTextField();
		separadorTf.setSize(getMinimumSize());
		formatoDataTf = new JTextField();
		formatoHoraTf = new JTextField();
		tabelaBancoTf = new JTextField();
		sridAtualTf = new JTextField();
		sridNovoTf = new JTextField();
		igArqTf = new JTextField();
		igDirTf = new JTextField();
		extTf = new JTextField();
		novaColunaTf = new JTextField();
		posicaoTf = new JTextField();
		typeSizeTf = new JTextField();
		
		diretorioBtn = new JButton("Select");
		addColunaBtn = new JButton("Add line");

		PromptSupport.setPrompt("Nome coluna", novaColunaTf);
		PromptSupport.setPrompt("Pos. arquivo", posicaoTf);
		PromptSupport.setPrompt("Size", typeSizeTf);
		typeSizeTf.setHorizontalAlignment(JLabel.CENTER);

		tiposCb = new JComboBox<>(getTypes());
		tiposCb.setRenderer(new MyComboBoxRenderer("TYPE"));
		tiposCb.setSelectedIndex(-1);
		((JLabel)tiposCb.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		incluirMetadados = new JCheckBox("Incluir Metadados");
		igExt = new JCheckBox("Ignorar");
		tid = new JCheckBox("Auto gerar Tid");
		gid = new JCheckBox("Auto gerar Gid");
		
		
		
		Object [] columnNames = new Object[]{ "Column", "Pos.", "Type", "Size" };
        Object [][] data        = new Object[][]{ {"date", "", EnumTipos.CHARACTERVARYING.toString(),""}
        											, {"time", "", EnumTipos.CHARACTERVARYING.toString(),""}
        											, {"lat", "", EnumTipos.NUMERIC.toString(),""}
        											, {"lon", "", EnumTipos.NUMERIC.toString(), ""}
        											, {"timestamp", "", EnumTipos.TIMESTAMP.toString(), ""}
        											, {"geom", "", EnumTipos.POINT.toString(), ""} };
        
        DefaultTableModel tab = new MyTableModel( data,columnNames, true );
        table1 = new JTable(tab);
        table = new JScrollPane(table1);
        //DefaultTableModel model = (DefaultTableModel) table1.getModel();
        table1.setRowHeight( 25 );
        setUpColumnComboBox(table1, table1.getColumnModel().getColumn(2));
		

		extTf.setText("pdf");
		igExt.setSelected(true);

		addColunaBtn.addActionListener(this);
		diretorioBtn.addActionListener(this);

		botaoProcessa.setBackground(Color.DARK_GRAY);
		diretorioBtn
				.setToolTipText("Clique para selecionar o diret�rio/arquivo");
		botaoProcessa.setToolTipText("Clique para carregar");
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
																		igLinhaLabel)
																.addComponent(
																		formatoDataLabel)																
																.addComponent(
																		sridAtualLabel)
																.addComponent(
																		extLabel)
																.addComponent(
																		igDirLabel)
																.addComponent(
																		tabelaLabel)
																.addComponent(novaColunaTf))
												.addGroup(
														layout.createParallelGroup(
																LEADING)
																.addComponent(
																		igLinhaTf)
																.addComponent(
																		formatoDataTf)
																.addComponent(
																		sridAtualTf)
																.addComponent(
																		extTf)
																.addComponent(
																		igDirTf)
																.addComponent(
																		tabelaBancoTf)
																.addComponent(gid)
																.addComponent(posicaoTf))
												.addGroup(
														layout.createParallelGroup(
																LEADING)
																.addComponent(
																		separadorLabel)
																.addComponent(
																		formatoHoraLabel)
																.addComponent(
																		sridNovoLabel)
																.addComponent(
																		igExt)
																.addComponent(
																		igArqLabel)
																.addComponent(
																		incluirMetadados)
																.addComponent(tiposCb))
												.addGroup(
														layout.createParallelGroup(
																LEADING)
																.addComponent(
																		separadorTf)
																.addComponent(
																		formatoHoraTf)
																.addComponent(
																		sridNovoTf)
																.addComponent(
																		igArqTf)
																.addComponent(tid)
																.addComponent(typeSizeTf)
																)
																)
									.addComponent(table)
						)
						
				.addGroup(
						layout.createParallelGroup(LEADING)
								.addComponent(diretorioBtn)
								.addComponent(botaoProcessa)
								.addComponent(addColunaBtn)));

		layout.linkSize(SwingConstants.HORIZONTAL, botaoProcessa, diretorioBtn);

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
																		igLinhaLabel)
																.addComponent(
																		igLinhaTf)
																.addComponent(
																		separadorLabel)
																.addComponent(
																		separadorTf))
												.addGroup(
														layout.createParallelGroup(
																BASELINE)
																.addComponent(
																		formatoDataLabel)
																.addComponent(
																		formatoDataTf)
																.addComponent(
																		formatoHoraLabel)
																.addComponent(
																		formatoHoraTf))												
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
								.addComponent(botaoProcessa))
							.addGroup(layout.createParallelGroup(BASELINE)
												.addComponent(table))
							.addGroup(layout.createParallelGroup(BASELINE)
												.addComponent(novaColunaTf)
												.addComponent(posicaoTf)
												.addComponent(tiposCb)
												.addComponent(typeSizeTf)
												.addComponent(addColunaBtn))
				
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
		}else if(e.getSource() == addColunaBtn){
			if(novaColunaTf.getText().length() == 0){
				JOptionPane.showMessageDialog(null,
						"Column name is empty",
						"Carregar documento", JOptionPane.ERROR_MESSAGE);
				novaColunaTf.requestFocus(true);
				return;
			}
			int inColPos, inTypeSize;
			try{
				inColPos = Integer.parseInt(posicaoTf.getText()); 
			}catch(NumberFormatException ex){
				JOptionPane.showMessageDialog(null,
						"Not a number",
						"Carregar documento", JOptionPane.ERROR_MESSAGE);
				posicaoTf.requestFocus(true);
				return;
			}
			
			if((String)tiposCb.getSelectedItem() == null){
				JOptionPane.showMessageDialog(null,
						"Choose a type",
						"Carregar documento", JOptionPane.ERROR_MESSAGE);
				tiposCb.requestFocus(true);
				return;
			}

			try{
				if(typeSizeTf.getText().length() > 0){
					inTypeSize = Integer.parseInt(typeSizeTf.getText());
					if(!(inTypeSize > 0) ){
						JOptionPane.showMessageDialog(null,
								"Number should be greater than 0",
								"Carregar documento", JOptionPane.ERROR_MESSAGE);
						typeSizeTf.requestFocus(true);
						return;
					}
				}
				
			}catch(NumberFormatException ex){
				JOptionPane.showMessageDialog(null,
						"Not a number",
						"Carregar documento", JOptionPane.ERROR_MESSAGE);
				typeSizeTf.requestFocus(true);
				return;
			}
			
				
			DefaultTableModel model = (DefaultTableModel) table1.getModel();
			model.addRow(new Object[]{novaColunaTf.getText()
										,posicaoTf.getText()
										,(String)tiposCb.getSelectedItem()
										,typeSizeTf.getText()});
			
			DefaultComboBoxModel model1 = new DefaultComboBoxModel( getTypes() );
			tiposCb.setModel( model1 );
			tiposCb.setRenderer(new MyComboBoxRenderer("TYPE"));
			tiposCb.setSelectedIndex(-1);
			((JLabel)tiposCb.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
			novaColunaTf.setText("");
			posicaoTf.setText("");
			typeSizeTf.setText("");
		
		}else if (e.getSource() == botaoProcessa) {
			if (!controle.isConnectionOpen())
				JOptionPane.showMessageDialog(null, "PAUUUU",
						"Conex�o ao banco", JOptionPane.ERROR_MESSAGE);
			else {	
				if (verificaEntradas()) {
					String inLocal = diretorioTf.getText();
					int inNlinhaig = Integer.parseInt(igLinhaTf.getText());
					String inSeparador = separadorTf.getText();
					String inFormatoDate = formatoDataTf.getText();
					String inFormatoTime = formatoHoraTf.getText();
					String inTabela = tabelaBancoTf.getText();
					boolean inMetadata = incluirMetadados.isSelected();
					boolean inGID = gid.isSelected();
					boolean inTID = tid.isSelected();
					Object[][] inTableData = getTableData(table1);
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

					TrajetoriaBruta tb = new TrajetoriaBruta(inNlinhaig, inSeparador, inFormatoDate, inFormatoTime, inTabela, inSRIDAtual, inSRIDNovo, inMetadata, inTableData, inGID, inTID);
					Diretorio dir = definicoesDiretorio(inLocal, inExt, inIgExt, inIgDir, inIgArq);
					
					try {
						controle.createTable(tb);
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
						controle.carregaArquivo(tb, dir);
						long endTime   = System.currentTimeMillis();
						long totalTime = endTime - startTime;
						JOptionPane.showMessageDialog(null, "Data loadaded \n"+Utils.getDurationBreakdown(totalTime),
								"Loading data",
								JOptionPane.INFORMATION_MESSAGE);
						limpeTela();
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
		dir.setExtensao(new ArrayList<String>(Arrays.asList(igExt)));
		dir.setIgDiretorio(new ArrayList<String>(Arrays.asList(igDir)));
		dir.setIgArquivo(new ArrayList<String>(Arrays.asList(igArq)));
		dir.setIgExtensao(inIgExt);
		return dir;
	}

	private boolean verificaEntradas() {
		if (diretorioTf.getText().length() == 0) {
			JOptionPane.showMessageDialog(null,
					"Informe o diret�rio ou arquivo a ser carregado",
					"Carregar documento", JOptionPane.ERROR_MESSAGE);
			diretorioTf.requestFocus(true);
			return false;
		} else if (igLinhaTf.getText().length() == 0) {
			JOptionPane
					.showMessageDialog(
							null,
							"Informe o n�mero de linhas a ser ignorados no inicio do arquivo",
							"Carregar documento", JOptionPane.ERROR_MESSAGE);
			igLinhaTf.requestFocus(true);
			return false;
		} else if (separadorTf.getText().length() == 0) {
			JOptionPane
					.showMessageDialog(
							null,
							"Informe o caracter que separa as colunas no arquivo a ser carregado",
							"Carregar documento", JOptionPane.ERROR_MESSAGE);
			separadorTf.requestFocus(true);
			return false;
		} else if (formatoDataTf.getText().length() == 0 && formatoHoraTf.getText().length() != 0) {
			JOptionPane.showMessageDialog(null,
					"Informe o formato da data no arquivo a ser carregado",
					"Carregar documento", JOptionPane.ERROR_MESSAGE);
			formatoDataTf.requestFocus(true);
			return false;
		} else if (formatoHoraTf.getText().length() == 0 && formatoDataTf.getText().length() != 0) {
			JOptionPane.showMessageDialog(null,
					"Informe o formato da hora no arquivo a ser carregado",
					"Carregar documento", JOptionPane.ERROR_MESSAGE);
			formatoHoraTf.requestFocus(true);
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
	
	 public void setUpColumnComboBox(JTable table, TableColumn column) {
		//Set up the editor for the sport cells.
		JComboBox comboBox = new JComboBox(getTypes());
		column.setCellEditor(new DefaultCellEditor(comboBox));
		//Set up tool tips for the cells.
		DefaultTableCellRenderer renderer =
		new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		column.setCellRenderer(renderer);
	}
	 
	public String[] getTypes(){
		String[] types = new String[] {
				EnumTipos.VARCHAR.toString()
				,EnumTipos.INTEGER.toString() 			 	
			 	,EnumTipos.SMALLINT.toString()
			 	,EnumTipos.SERIAL.toString()
			 	,EnumTipos.DECIMAL.toString()
			 	,EnumTipos.NUMERIC.toString()
			 	,EnumTipos.REAL.toString()
			 	,EnumTipos.CHARACTERVARYING.toString()
			 	,EnumTipos.TIMESTAMP.toString()
			 	,EnumTipos.POINT.toString()
		};
		return types;
	}
	
	public Object[][] getTableData (JTable table) {
	    DefaultTableModel dtm = (DefaultTableModel) table.getModel();
	    int nRow = dtm.getRowCount(), nCol = dtm.getColumnCount();
	    Object[][] tableData = new Object[nRow][nCol];
	    for (int i = 0 ; i < nRow ; i++)
	        for (int j = 0 ; j < nCol ; j++)
	            tableData[i][j] = dtm.getValueAt(i,j);
	    return tableData;
	}

	 class MyComboBoxRenderer extends JLabel implements ListCellRenderer
	    {
	        private String _title;

	        public MyComboBoxRenderer(String title)
	        {
	            _title = title;
	        }

	     
	        public Component getListCellRendererComponent(JList list, Object value,
	                int index, boolean isSelected, boolean hasFocus)
	        {
	            if (index == -1 && value == null) setText(_title);
	            else setText(value.toString());
	            return this;
	        }

	    }
	 
}
