package br.ufsc.src.persistencia;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

import br.ufsc.src.control.dataclean.ConfigTrajBroke;
import br.ufsc.src.control.entities.Trajectory;
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

public interface InterfacePersistencia {
	
	public boolean testaConexao() throws DBConnectionException;
	
	public boolean carregaArquivo(Diretorio dir, TrajetoriaBruta tj) throws TimeStampException, LoadDataFileException, GetSequenceException, CreateSequenceException, UpdateGeomException, CreateStatementException, AddBatchException, FileNFoundException, ExecuteBatchException, DBConnectionException;
	
	public void createTable(String tableName, Object[][] tableData, boolean isGID, boolean isTID, boolean isMetaData) throws SyntaxException, CreateTableException, DBConnectionException;

	public ArrayList<String> getTableColumns(String tableName) throws DBConnectionException, GetTableColumnsException;
	
	public void addColumn(String tableName, String ColumnName, String columnType) throws DBConnectionException, AddColumnException;
	
	public void dropColumn(String tableName, String ColumnName);

	public void createIndex(String tableName, String columnName, String indexType) throws DBConnectionException, SQLException;

	public Set<Integer> fetchTIDS(String columnName, String tableNameOrigin) throws DBConnectionException, SQLException;
	
	public void createSequence(String tableName, String id) throws CreateSequenceException, DBConnectionException;
	
	public int getSequence (String tableName, String id) throws GetSequenceException;
	
	public int getSeq (String tableName, String id) throws GetSequenceException, DBConnectionException, CreateStatementException, SQLException;

	public void moveDataFromColumnToColumn(String columnName, String string, String tableName) throws DBConnectionException, SQLException;

	public void dropIndex(String tableNameOrigin, String columnName) throws SQLException, DBConnectionException;

	public Trajectory fetchTrajectory(Integer tid, ConfigTrajBroke configTrajBroke, String columnTID) throws DBConnectionException, SQLException;

	public void updateTID(String string) throws SQLException, DBConnectionException;
	
	public void deletePointWhere(String tableName, String columnName, String operator, double condition) throws SQLException, DBConnectionException;
	
}