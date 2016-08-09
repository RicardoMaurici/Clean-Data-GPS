package br.ufsc.src.control;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

import br.ufsc.src.control.dataclean.ConfigTraj;
import br.ufsc.src.control.dataclean.RemoveNoise;
import br.ufsc.src.control.dataclean.TrajBroke;
import br.ufsc.src.control.exception.BrokeTrajectoryException;
import br.ufsc.src.persistencia.InterfacePersistencia;
import br.ufsc.src.persistencia.Persistencia;
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
import br.ufsc.src.persistencia.fonte.TrajetoriaBruta;

public class ServiceControl {
	
	InterfacePersistencia persistencia;
	
	public ServiceControl(InterfacePersistencia persistencia){
		this.persistencia = persistencia;
		this.persistencia = new Persistencia("org.postgresql.Driver", "jdbc:postgresql://localhost/", "greece_trucks", "rogerjames", "raisa");
	}
	
	public boolean testarBanco(String drive, String url, String usuario, String senha, String banco){
		this.criaConexao(drive, url, usuario, senha, banco);
		try {
			return this.persistencia.testaConexao();
		} catch (DBConnectionException e) {
			return false;
		}
	}
	
	public void criaConexao(String drive, String url, String usuario, String senha, String banco){
		this.persistencia = new Persistencia(drive, url, banco, usuario, senha);
	}
	
	public boolean testConnection(){
		try {
			return persistencia.testaConexao();
		} catch (DBConnectionException e) {
			return false;
		}
	}

	public void loadData(TrajetoriaBruta tb, Diretorio dir) throws TimeStampException, LoadDataFileException, GetSequenceException, CreateSequenceException, UpdateGeomException, CreateStatementException, AddBatchException, FileNFoundException, ExecuteBatchException, DBConnectionException {
		persistencia.carregaArquivo(dir, tb);
	}
	
	public void createTable(TrajetoriaBruta tb) throws SyntaxException, CreateTableException, DBConnectionException{
		persistencia.createTable(tb.getTabelaBanco(), tb.getTableData(), tb.isGID(), tb.isTID(), tb.isMetaData());
	}
	
	public ArrayList<String> getTableColumns(String tableName) throws DBConnectionException, GetTableColumnsException{
			return persistencia.getTableColumns(tableName);
	}

	public void brokeTraj(ConfigTraj configTrajBroke) throws DBConnectionException, AddColumnException, SQLException, BrokeTrajectoryException {
		TrajBroke trajBroke = new TrajBroke(persistencia, configTrajBroke);
		persistencia.addColumn(configTrajBroke.getTableNameOrigin(), "old_tid", "numeric");
		persistencia.createIndex(configTrajBroke.getTableNameOrigin(), configTrajBroke.getColumnName("gid"), "btree");
		cleanColumns(configTrajBroke);
		Set<Integer> tids = null;
		String columnNewTID = configTrajBroke.getColumnName("TID");
		try{
			tids = persistencia.fetchTIDS(configTrajBroke.getColumnName("TID"), configTrajBroke.getTableNameOrigin());

			if(configTrajBroke.isStatus()){
				persistencia.addColumn(configTrajBroke.getTableNameOrigin(), "status_tid", "numeric");
				columnNewTID = "status_tid";
				trajBroke.splitByStatus(tids);
			}
			if(configTrajBroke.getSample() != 0){
				persistencia.addColumn(configTrajBroke.getTableNameOrigin(), "sample_tid", "numeric");
				if(configTrajBroke.isStatus())
					tids = persistencia.fetchTIDS("status_tid", configTrajBroke.getTableNameOrigin());
				columnNewTID = "sample_tid";
				trajBroke.splitBySample(tids);
			}
			if(configTrajBroke.getDistanceMax() != 0){
				persistencia.addColumn(configTrajBroke.getTableNameOrigin(), "distance_tid", "numeric");
				if(configTrajBroke.getSample() != 0)
					tids = persistencia.fetchTIDS("sample_tid", configTrajBroke.getTableNameOrigin());
				else if(configTrajBroke.isStatus())
					tids = persistencia.fetchTIDS("status_tid", configTrajBroke.getTableNameOrigin());
				columnNewTID = "distance_tid";
				trajBroke.splitByDistance(tids);
			}
		}catch(Exception e){
			persistencia.dropIndex(configTrajBroke.getTableNameOrigin(), configTrajBroke.getColumnName("gid"));
			persistencia.dropColumn(configTrajBroke.getTableNameOrigin(), "old_tid");
			if(configTrajBroke.isStatus())
				persistencia.dropColumn(configTrajBroke.getTableNameOrigin(), "status_tid");
			if(configTrajBroke.getSample() != 0)
				persistencia.dropColumn(configTrajBroke.getTableNameOrigin(), "sample_tid");
			if(configTrajBroke.getDistanceMax() != 0)
				persistencia.dropColumn(configTrajBroke.getTableNameOrigin(), "distance_tid");
			throw new BrokeTrajectoryException(e.getMessage());
		}
		persistencia.moveDataFromColumnToColumn(configTrajBroke.getColumnName("TID"),"old_tid", configTrajBroke.getTableNameOrigin());
		persistencia.moveDataFromColumnToColumn(columnNewTID, configTrajBroke.getColumnName("TID"), configTrajBroke.getTableNameOrigin());
		if(configTrajBroke.isStatus())
			persistencia.dropColumn(configTrajBroke.getTableNameOrigin(), "status_tid");
		if(configTrajBroke.getSample() != 0)
			persistencia.dropColumn(configTrajBroke.getTableNameOrigin(), "sample_tid");
		if(configTrajBroke.getDistanceMax() != 0)
			persistencia.dropColumn(configTrajBroke.getTableNameOrigin(), "distance_tid");
		persistencia.dropIndex(configTrajBroke.getTableNameOrigin(), configTrajBroke.getColumnName("gid"));
	}

	private void cleanColumns(ConfigTraj configTrajBroke) throws SQLException, DBConnectionException {
		double accuracy = 0.0;
		double speed = 0.0;
		if(configTrajBroke.getAccuracy() != null){
			accuracy = Double.parseDouble(configTrajBroke.getAccuracy());
			persistencia.deletePointWhere(configTrajBroke.getTableNameOrigin(), configTrajBroke.getColumnName("ACCURACY"), "<=", accuracy);
		}
		if(configTrajBroke.getSpeed() != null){
			speed = Double.parseDouble(configTrajBroke.getSpeed());
			persistencia.deletePointWhere(configTrajBroke.getTableNameOrigin(), configTrajBroke.getColumnName("SPEED"), ">=", speed);
		}	
	}

	public void removeNoise(ConfigTraj configTraj) {
		double speed = Double.parseDouble(configTraj.getSpeed());
		Set<Integer> tids = null;
		RemoveNoise removeNoise = new RemoveNoise(persistencia, configTraj);
		try {
			tids = persistencia.fetchTIDS(configTraj.getColumnName("TID"), configTraj.getTableNameOrigin());
			System.out.println("controle");
			removeNoise.findRemoveNoise(tids, speed);
		} catch (DBConnectionException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		
	}
	
}