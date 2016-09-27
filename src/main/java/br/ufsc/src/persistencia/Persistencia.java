package br.ufsc.src.persistencia;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.postgresql.util.PSQLException;

import br.ufsc.src.control.Utils;
import br.ufsc.src.control.dataclean.ConfigTraj;
import br.ufsc.src.control.entities.TPoint;
import br.ufsc.src.control.entities.Trajectory;
import br.ufsc.src.persistencia.conexao.DBConfig;
import br.ufsc.src.persistencia.conexao.DBConnectionProvider;
import br.ufsc.src.persistencia.exception.AddBatchException;
import br.ufsc.src.persistencia.exception.AddColumnException;
import br.ufsc.src.persistencia.exception.CreateSequenceException;
import br.ufsc.src.persistencia.exception.CreateStatementException;
import br.ufsc.src.persistencia.exception.CreateTableException;
import br.ufsc.src.persistencia.exception.DBConnectionException;
import br.ufsc.src.persistencia.exception.ExecuteBatchException;
import br.ufsc.src.persistencia.exception.FileNFoundException;
import br.ufsc.src.persistencia.exception.GetSequenceException;
import br.ufsc.src.persistencia.exception.GetTableColumnsException;
import br.ufsc.src.persistencia.exception.LoadDataFileException;
import br.ufsc.src.persistencia.exception.SyntaxException;
import br.ufsc.src.persistencia.exception.TimeStampException;
import br.ufsc.src.persistencia.exception.UpdateGeomException;
import br.ufsc.src.persistencia.fonte.Diretorio;
import br.ufsc.src.persistencia.fonte.ILoader;
import br.ufsc.src.persistencia.fonte.LoaderDSV;
import br.ufsc.src.persistencia.fonte.LoaderGPX;
import br.ufsc.src.persistencia.fonte.LoaderJSON;
import br.ufsc.src.persistencia.fonte.LoaderKML;
import br.ufsc.src.persistencia.fonte.LoaderWKT;
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
		abraConexao();
		try {
			ResultSet rs = DB_CONN.quickQuery("truck",null,null,"order by truckid, time");
			while (rs.next()) {
				int truck = rs.getInt("truckid");
				System.out.println(truck);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		fechaConexao();
	}

	public static void abraConexao() throws DBConnectionException{
		try {
			DBConnectionProvider.getInstance().open();
		} catch (SQLException e) {
			throw new DBConnectionException(e.getMessage());
		}
	}

	public static void fechaConexao() throws DBConnectionException{
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
			leiaCarregaDiretorios(dir.getUrl(), dir.getIgFile(), dir.getIgFolder(), dir.getExtension(), dir.isIgExtension(), tb);
			updateNewGeom(tb.getSridNovo(), tb.getSridAtual(), tb.getTabelaBanco());
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
			String auxTime = (String)objects[1];
			if(x.equalsIgnoreCase("time") && auxTime.equalsIgnoreCase(""))
				continue;
			else
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
		abraConexao();
		try {
			DB_CONN.execute(q);
		}catch(PSQLException e){
			throw new SyntaxException(e.getMessage());
		}catch (SQLException e) {
			throw new CreateTableException(e.getMessage());
		}
		fechaConexao();
		
	}
	
	public void leiaCarregaDiretorios(String dir, ArrayList<String> igFiles, ArrayList<String> igDir, ArrayList<String> ext, boolean aceitaExtensao, TrajetoriaBruta trajBruta) throws IOException, TimeStampException, GetSequenceException, CreateStatementException, AddBatchException, FileNFoundException, ExecuteBatchException, DBConnectionException {

		File folder = new File(dir);
		if (!folder.isFile()) {
			File[] listOfFiles = folder.listFiles();
			
			for (File file : listOfFiles) {
				if (file.isFile() && !igFiles.contains(file.getName().split("\\.")[0])) {
					if (file.getName().lastIndexOf(".") != 0 && (ext.isEmpty() || (ext.contains(Utils.getFileExtension(file)) == !aceitaExtensao))) {
						if(!folder.getParent().equalsIgnoreCase(path)){
							path = folder.getParent();
							folder_id++;
						}
						this.loadFile(file, trajBruta, folder_id);
					}	
				} else if (file.isDirectory() && !igDir.contains(file.getName()))	
					this.leiaCarregaDiretorios(file.getAbsolutePath(), igFiles, igDir, ext, aceitaExtensao, trajBruta);
			}
		} else if (!igFiles.contains(folder.getName().split("\\.")[0])) {
			if (folder.getName().lastIndexOf(".") != 0 && (ext.isEmpty() || (ext.contains(Utils.getFileExtension(folder)) == !aceitaExtensao))){
				if(!folder.getParent().equalsIgnoreCase(path)){
					path = folder.getParent();
					folder_id++;
				}
				this.loadFile(folder, trajBruta, folder_id);
			}
		}
	
	}
	
	private void loadFile(File file, TrajetoriaBruta tb, int folder_id) throws TimeStampException, GetSequenceException, CreateStatementException, AddBatchException, FileNFoundException, ExecuteBatchException, DBConnectionException{
		ILoader leitor = null;
		switch (Utils.getFileExtension(file).toLowerCase()) {
			case "kml":
				leitor = new LoaderKML();
				break;
			case "gpx":
				leitor = new LoaderGPX();
				break;
			case "wkt":
				leitor = new LoaderWKT();
				break;
			case "json":
				leitor = new LoaderJSON();
				break;
			default:
				leitor = new LoaderDSV();
		}
		leitor.loadFile(file, tb, folder_id);
	}

	private void updateNewGeom(int newSrid, int currentSrid, String tableName) throws UpdateGeomException, DBConnectionException{
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
	
	public void createSequence(String tableName, String id) throws CreateSequenceException, DBConnectionException{
		abraConexao();
		try {
			try{
				DB_CONN.execute("DROP SEQUENCE "+tableName+"_"+id+"_seq CASCADE;");
			}catch(SQLException e){
			}
			DB_CONN.execute("CREATE SEQUENCE "+tableName+"_"+id+"_seq START 1;");
		} catch (SQLException e) {
			throw new CreateSequenceException(e.getMessage());
		}
		fechaConexao();
	}
	
	public static int getSequence (String tableName, String id) throws GetSequenceException{
		try{
			return DB_CONN.getSequenceNextValue(tableName+"_"+id+"_seq");
		}catch(SQLException e){
			throw new GetSequenceException(e.getMessage());
		}
	}
	
	public int getSeq (String tableName, String id) throws GetSequenceException, DBConnectionException, CreateStatementException, SQLException{
		abraConexao();
		int seq = 0;
		try {
			DB_CONN.createStatement();
		} catch (SQLException e1) {
			throw new CreateStatementException(e1.getMessage());
		}
		try{
			 seq = DB_CONN.getSequenceNextValue(tableName+"_"+id+"_seq");
		}catch(SQLException e){
			throw new GetSequenceException(e.getMessage());
		}
		DB_CONN.closeStatement();
		fechaConexao();
		return seq;
	}

	@Override
	public ArrayList<String> getTableColumns(String tableName) throws DBConnectionException, GetTableColumnsException {
		String sql = "select column_name from information_schema.columns where table_name='"+tableName+"';";
		ArrayList<String> columns = new ArrayList<>();
		abraConexao();
			ResultSet rs;
			try {
				rs = DB_CONN.executeQuery(sql);
				while(rs.next()){
					String col = rs.getString(1);
					columns.add(col);
				}
			} catch (SQLException e) {
				throw new GetTableColumnsException(e.getMessage());
			}
		fechaConexao();
		return columns;
		
	}
	
	public void addColumn(String tableName, String ColumnName, String columnType) throws DBConnectionException, AddColumnException{
		String sql = "ALTER TABLE "+tableName+" ADD COLUMN "+ColumnName+" "+columnType+";";
		abraConexao();
		try {
			DB_CONN.execute(sql);
		} catch (SQLException e) {
			throw new AddColumnException(e.getMessage());
		}
		fechaConexao();
	}

	public void createIndex(String tableName, String columnName, String indexType) throws DBConnectionException, SQLException {
		try{
			String s = "DROP INDEX "+tableName+"_"+columnName+"_idx"+";";
			abraConexao();
			DB_CONN.execute(s);
			fechaConexao();
		}catch(Exception e){
		}
		String sql = "CREATE INDEX "+tableName+"_"+columnName+"_idx"+" ON "+tableName+" USING "+indexType+"("+columnName+");";
		abraConexao();
		DB_CONN.execute(sql);
		fechaConexao();
		
	}

	public void dropColumn(String tableName, String ColumnName) {
		String sql = "ALTER TABLE "+tableName+" DROP COLUMN "+ColumnName+";";
		try {
			abraConexao();
			DB_CONN.execute(sql);
			fechaConexao();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (DBConnectionException e1) {
			e1.printStackTrace();
		}
	}

	public Set<Integer> fetchTIDS(String columnName, String tableNameOrigin) throws DBConnectionException, SQLException {

		String sql = "SELECT DISTINCT "+columnName+" from "+tableNameOrigin+";";
		abraConexao();
		ResultSet resultSet = DB_CONN.executeQuery(sql);
		Set<Integer> result = new HashSet<Integer>();
		while(resultSet.next()){
			Integer tid = resultSet.getInt(columnName);
			result.add(tid);
		}
		fechaConexao();
		return result;
		
	}

	public void moveDataFromColumnToColumn(String fromColumn, String toColumn, String tableName) throws DBConnectionException, SQLException {
		String sql = "UPDATE "+tableName+" SET "+toColumn+"="+fromColumn+";";
		abraConexao();
		DB_CONN.execute(sql);
		fechaConexao();
		
	}

	public void dropIndex(String tableNameOrigin, String columnName) throws SQLException, DBConnectionException {
		String sql = "DROP INDEX "+tableNameOrigin+"_"+columnName+"_idx"+";";
		abraConexao();
		DB_CONN.execute(sql);
		fechaConexao();
		
	}

	public Trajectory fetchTrajectory(Integer tid, ConfigTraj configTraj, String columnTID) throws DBConnectionException, SQLException {
	/*	String sql = "SELECT "+configTraj.getColumnName("GID")+" as gid,"+
					columnTID+" as tid,"+
					configTraj.getColumnName("TIMESTAMP")+" as timestamp,st_x("+
					configTraj.getColumnName("GEOM")+") as lon,st_y("+
					configTraj.getColumnName("GEOM")+") as lat";
				sql += configTraj.isStatus() ? ","+configTraj.getColumnName("BOOLEAN STATUS") : "";
				sql += " from "+ configTraj.getTableNameOrigin()+
				" where "+columnTID+"="+tid+" order by "+columnTID+","+configTraj.getColumnName("TIMESTAMP")+";";*/
		String sql = "SELECT gid, tid, timestamp,st_x(GEOM) as lon,st_y(geom) as lat";
			sql += configTraj.isStatus() ? ","+configTraj.getColumnName("BOOLEAN STATUS") : "";
			sql += " from "+ configTraj.getTableNameOrigin()+
			" where "+columnTID+"="+tid+" order by "+columnTID+",timestamp;";

		abraConexao();
		ResultSet resultSet = DB_CONN.executeQuery(sql);
		Trajectory result = new Trajectory(tid);
		while(resultSet.next()){
			Double x = resultSet.getDouble("lon");
			Double y = resultSet.getDouble("lat");
			Timestamp time = resultSet.getTimestamp("timestamp");
			int gid = resultSet.getInt("gid");
			int occupation = 0;
			if(configTraj.isStatus())
				occupation = resultSet.getInt(configTraj.getColumnName("BOOLEAN STATUS"));
			TPoint p= new TPoint(gid,x,y,time,occupation);
			result.addPoint(p);
		}
		fechaConexao();
		return result;
	}

	public void updateTID(List<String> querys) throws SQLException, DBConnectionException {
		abraConexao();
		DB_CONN.createStatement();
		for (String sql : querys) {
			DB_CONN.addBatch(sql);
		}
		DB_CONN.executeBatch();
		DB_CONN.closeStatement();
		fechaConexao();
	}
	
	public void deletePointWhere(String tableName, String columnName, String operator, double condition) throws SQLException, DBConnectionException{
		abraConexao();
		DB_CONN.execute("DELETE FROM "+tableName+" WHERE "+ columnName + operator + condition +";");
		fechaConexao();
	}

	public void deleteByGids(List<Integer> gids, String tableNameOrigin) throws DBConnectionException, SQLException {
		String ids ="";
		for (Integer gid : gids) {
			ids += gid+",";
		}
		ids = ids.substring(0, ids.length()-1);
		abraConexao();
		DB_CONN.execute("DELETE FROM "+tableNameOrigin+" WHERE gid in ("+ ids +");");
		fechaConexao();
	}
	
	public void exportTable(String path, String table) throws DBConnectionException, SQLException {
		String sql = "COPY "+table+" TO '"+path+"/"+table+".csv' DELIMITER ',' CSV HEADER;";
		abraConexao();
		DB_CONN.execute(sql);
		fechaConexao();
	}

	public void updateGIDs(List<TPoint> pointsToUpdate, ConfigTraj configTraj) throws DBConnectionException, AddBatchException, ExecuteBatchException {
		
		String colGeom = configTraj.getColumnName("geom");
		String colTID = configTraj.getColumnName("tid");
		String colGID = configTraj.getColumnName("gid");
		String colLon = configTraj.getColumnName("lon");
		String colLat = configTraj.getColumnName("lat");
		String tableName = configTraj.getTableNameOrigin();
		int srid= 0;
		abraConexao();
		ResultSet resultSet;
		try {
			resultSet = DB_CONN.executeQuery("select ST_SRID("+colGeom+") from "+tableName+" limit 1;");
			resultSet.next();
			srid = resultSet.getInt("st_srid");
			DB_CONN.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		int cont = 0;
		
		for (TPoint tPoint : pointsToUpdate) {
			String sql = "update "+tableName+" set "+colGeom+" = "+"ST_SetSRID(ST_MakePoint(" + tPoint.getX() + "," + tPoint.getY() + ")," + srid + ")"+ " where "+colGID+" = "+tPoint.getGid()+";";
			try {
				DB_CONN.addBatch(sql);
				cont++;
			} catch (SQLException e) {
				throw new AddBatchException(e.getMessage());
			}
			if(cont == 200000){
				try {
					DB_CONN.executeBatch();
				} catch (SQLException e) {
					throw new ExecuteBatchException(e.getMessage());
				}
				cont = 0;
			}
		}
		try {
			DB_CONN.executeBatch();
			DB_CONN.closeStatement();
		} catch (SQLException e) {
			throw new ExecuteBatchException(e.getMessage());
		}
		fechaConexao();
	}
	
	public boolean createTableMoveTrajNearPoint(String sql, String tableName, String tidColumn){
		String hasResult = sql+" limit 1;";
		String sql1 = "create table "+tableName+"_trajsnearpoint as select * from "+tableName+" where "+tidColumn+" in ("+sql+");";
		boolean rt = false;
		try {
			abraConexao();
		} catch (DBConnectionException e) {
			return false;
		}
		ResultSet resultSet;
		try {
			resultSet = DB_CONN.executeQuery(hasResult);
			if(resultSet.next()){
				DB_CONN.execute(sql1);
				rt = true;
			}else
				rt = false;
		} catch (SQLException e1) {
		}
		try {
			fechaConexao();
		} catch (DBConnectionException e) {
		}
		return rt;
	}

	@Override
	public void createTableFromAnother(String tableNameOrigin, String newTableName) throws DBConnectionException, SQLException {
		String sql = "CREATE TABLE "+newTableName+" AS SELECT * FROM "+tableNameOrigin+";";
		abraConexao();
		DB_CONN.execute(sql);
		fechaConexao();
	}
	
}