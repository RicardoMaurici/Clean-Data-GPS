package br.ufsc.src.persistencia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import org.postgresql.util.PSQLException;

import br.ufsc.src.controle.Utils;
import br.ufsc.src.persistencia.conexao.DBConfig;
import br.ufsc.src.persistencia.conexao.DBConnectionProvider;
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

public class Persistencia implements InterfacePersistencia {

	protected static DBConnectionProvider DB_CONN;
	private int folder_id;
	private String path;
	
	public Persistencia(){}

	public Persistencia(String driverPostgres, String url, String banco, String usuario, String senha){
		DBConfig.driverPostgres = driverPostgres;
		DBConfig.url = url;
		DBConfig.senha = senha;
		DBConfig.usuario = usuario;
		DBConfig.banco = banco;
		DB_CONN = DBConnectionProvider.getInstance();
		this.folder_id = 0;
		this.path = "";
	}

	public void leiaTabela() throws DBConnectionException{
		this.abraConexao();
		try {
			ResultSet rs = DB_CONN.quickQuery("truck",null,null,"order by truckid, time");
			while (rs.next()) {
				int truck = rs.getInt("truckid");
				System.out.println(truck);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.fechaConexao();
	}

	public void abraConexao() throws DBConnectionException{
		try {
			DBConnectionProvider.getInstance().open();
		} catch (SQLException e) {
			throw new DBConnectionException(e.getMessage());
		}
	}

	public void fechaConexao() throws DBConnectionException{
		try {
			DBConnectionProvider.getInstance().close();
		} catch (SQLException e) {
			throw new DBConnectionException(e.getMessage());
		}
	}
	
	public boolean testaConexao() throws DBConnectionException{
		abraConexao();
		boolean test = DBConnectionProvider.isConnectionOpen();
		fechaConexao();
		return test;
	}
	
	public boolean carregaArquivo(Diretorio dir, TrajetoriaBruta tb) throws TimeStampException, LoadDataFileException, GetSequenceException, CreateSequenceException, UpdateGeomException, CreateStatementException, AddBatchException, FileNFoundException, ExecuteBatchException, DBConnectionException{
		try {
			folder_id = 0;
			path = "";
			if(tb.isTID())
				createSequence(tb.getTabelaBanco(), "tid");
			leiaCarregaDiretorios(dir.getUrl(), dir.getIgArquivo(), dir.getIgDiretorio(), dir.getExtensao(), dir.isIgExtensao(), tb);
			updateGeom(tb.getSridNovo(), tb.getSridAtual(), tb.getTabelaBanco());
		} catch (IOException e) {
			throw new LoadDataFileException(e.getMessage());
		}
		return true;
	}
	
	public void createTable(String tableName, Object[][] tableData, boolean isGID, boolean isTID, boolean isMetaData) throws SyntaxException, CreateTableException, DBConnectionException{
		String q = "CREATE TABLE "+tableName+" ( ";
		Object[][] td = tableData;
		String gid = "gid serial,";
		String tid = "tid serial,";
		String metadata = "path varchar(150), folder_id integer,";
		boolean t = false, gi = false;
		String q1 = "";
		for (Object[] objects : td) {
			String x = (String)objects[0];
			if(x.equalsIgnoreCase("gid"))
				gi = true;
			if(x.equalsIgnoreCase("tid"))
				t = true;
			
			q1 += objects[0]+" "+objects[2]+""+(!objects[3].equals("") ? "("+objects[3]+")," : ",");
		}
		if(isGID && !gi)
			q += gid;
		if(isTID && !t)
			q += tid;
		q += q1;

		if(isMetaData)
			q += metadata;
		q = q.trim().toLowerCase();
		q = q.substring(0, q.length()-1);
		q += ");";

		this.abraConexao();
		try {
			DB_CONN.execute(q);
		}catch(PSQLException e){
			throw new SyntaxException(e.getMessage());
		}catch (SQLException e) {
			throw new CreateTableException(e.getMessage());
		}
		this.fechaConexao();
		
	}
	
	public void leiaCarregaDiretorios(String dir, ArrayList<String> igFiles, ArrayList<String> igDir, ArrayList<String> ext, boolean aceitaExtensao, TrajetoriaBruta trajBruta) throws IOException, TimeStampException, GetSequenceException, CreateStatementException, AddBatchException, FileNFoundException, ExecuteBatchException, DBConnectionException {

		File folder = new File(dir);
		if (!folder.isFile()) {
			File[] listOfFiles = folder.listFiles();
			
			for (File file : listOfFiles) {
				if (file.isFile() && !igFiles.contains(file.getName().split("\\.")[0])) {
					if (file.getName().lastIndexOf(".") != 0 && (ext.isEmpty() || (ext.contains(getFileExtension(file)) == !aceitaExtensao))) {
						if(!folder.getParent().equalsIgnoreCase(path)){
							path = folder.getParent();
							folder_id++;
						}
						this.leiaArquivo(file, trajBruta, folder_id);
					}	
				} else if (file.isDirectory() && !igDir.contains(file.getName()))	
					this.leiaCarregaDiretorios(file.getAbsolutePath(), igFiles, igDir, ext, aceitaExtensao, trajBruta);
			}
		} else if (!igFiles.contains(folder.getName().split("\\.")[0])) {
			if (folder.getName().lastIndexOf(".") != 0 && (ext.isEmpty() || (ext.contains(getFileExtension(folder)) == !aceitaExtensao))){
				if(!folder.getParent().equalsIgnoreCase(path)){
					path = folder.getParent();
					folder_id++;
				}
				this.leiaArquivo(folder, trajBruta, folder_id);
			}
		}
	
	}

	private void leiaArquivo(File file, TrajetoriaBruta tb, int folder_id) throws TimeStampException, GetSequenceException, CreateStatementException, AddBatchException, FileNFoundException, ExecuteBatchException, DBConnectionException {
		Scanner scanner;
		int posDate = -1;
		int posTime = -1;
		int posLon = -1;
		int posLat = -1;
		int colPos = -1;
		Object[][] tableData = tb.getTableData();
		
		for (int i = 0; i <= tableData.length - 1; i++) {
			String colName = (String)tableData[i][0];
			String cs = (String)tableData[i][1];
			try{
				colPos = Integer.parseInt(cs);
			}catch(NumberFormatException e){
				colPos = -1;
			}
			if(colName.equalsIgnoreCase("date"))
				posDate = colPos;
			else if(colName.equalsIgnoreCase("time"))
				posTime = colPos;
			else if(colName.equalsIgnoreCase("lat"))
				posLat = colPos;
			else if(colName.equalsIgnoreCase("lon"))
				posLon = colPos;
		}
		
		String date = "";
		String time = "";
		String lon = "";
		String lat = "";
		String timestamp = "";
		String sql = "";
		this.abraConexao();
		try {
			DB_CONN.createStatement();
		} catch (SQLException e1) {
			throw new CreateStatementException(e1.getMessage());
		}
		int seq = 0;
		if(tb.isTID())
			seq = getSequence(tb.getTabelaBanco(), "tid");
		try {
			scanner = new Scanner(new FileReader(file.getAbsolutePath()));
			for (int i = 0; i < tb.getNroLinhasIgnorar(); i++) { //ignore initial lines
				if (scanner.hasNextLine())
					scanner.nextLine();
			}
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] linha = line.split(tb.getSeparador());
				if(posDate >= 0)
					date = linha[posDate-1];
				if(posTime >= 0)
					time = linha[posTime-1];
				
				boolean tstamp = (tb.getFormatoData().equalsIgnoreCase("") && tb.getFormatoHorario().equalsIgnoreCase("")); 
				timestamp = Utils.getTimeStamp(date, time, tb.getFormatoData(), tb.getFormatoHorario(), tstamp); 												
				
				sql = "insert into "+tb.getTabelaBanco()+" (";
				String sql1 = ") values (";
				
				if(posLon >= 0 && posLat >= 0 ){
					lon = linha[posLon-1];
					lat = linha[posLat-1];
					sql += "geom,";
					sql1 += "ST_SetSRID(ST_MakePoint("+lon+","+lat+"),"+tb.getSridAtual()+"),";
				}
				sql += "timestamp,";
				sql1 += "'"+timestamp+"',";
				
				for (int i = 0; i <= tableData.length - 1; i++) {
					String aux = (String)tableData[i][0];
					if(!aux.equalsIgnoreCase("geom") && !aux.equalsIgnoreCase("timestamp")){
						sql += (String)tableData[i][0]+",";
						String cs = (String)tableData[i][1];
						int crs = -1;
						try{
							crs = Integer.parseInt(cs);
						}catch(NumberFormatException e){
							crs = -1;
						}
						if(crs != -1){
							if(aux.equalsIgnoreCase("time"))
								sql1 += "'"+linha[crs-1]+"',";
							else if(aux.equalsIgnoreCase("date"))
								sql1 += "'"+linha[crs-1]+"',";
							else
								sql1 += linha[crs-1]+",";
						}
					}
					
				}
				if(tb.isMetaData()){
					sql += "path,folder_id,";
					sql1 += "'"+file.getAbsolutePath()+"',"+folder_id+",";
				}
				if(tb.isTID()){
					sql += "tid,";
					sql1 += seq+",";
				}
				
				sql = sql.trim().substring(0, sql.length()-1);
				sql1 = sql1.trim().substring(0,sql1.length()-1);
				sql += sql1+")";
				try {
					DB_CONN.addBatch(sql);
				} catch (SQLException e) {
					throw new AddBatchException(e.getMessage());
				}
			
			}
		} catch (FileNotFoundException e) {
			throw new FileNFoundException(e.getMessage());
		}
		try {
			DB_CONN.executeBatch();
			DB_CONN.closeStatement();
		} catch (SQLException e) {
			throw new ExecuteBatchException(e.getMessage());
		}
		this.fechaConexao();
		
	}

	private void updateGeom(int newSrid, int currentSrid, String tableName) throws UpdateGeomException, DBConnectionException{
		try {
			if(newSrid != currentSrid){
				abraConexao();
				DB_CONN.execute("update "+tableName+" set geom = ST_Transform(geom,"+newSrid+");");
				fechaConexao();
			}
		} catch (SQLException e) {
			throw new UpdateGeomException(e.getMessage());
		}
	}
	
	private void createSequence(String tableName, String id) throws CreateSequenceException, DBConnectionException{
		abraConexao();
		try {
			try{
				DB_CONN.execute("DROP SEQUENCE "+tableName+"_"+id+"_seq CASCADE;");
			}catch(SQLException e){
				throw new CreateSequenceException(e.getMessage());
			}
			DB_CONN.execute("CREATE SEQUENCE "+tableName+"_"+id+"_seq START 1;");
		} catch (SQLException e) {
			throw new CreateSequenceException(e.getMessage());
		}
		fechaConexao();
	}
	
	private int getSequence (String tableName, String id) throws GetSequenceException{
		try{
			return DB_CONN.getSequenceNextValue(tableName+"_"+id+"_seq");
		}catch(SQLException e){
			throw new GetSequenceException(e.getMessage());
		}
	}

	private static String getFileExtension(File file) {
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		else
			return "";
	}
	
}
