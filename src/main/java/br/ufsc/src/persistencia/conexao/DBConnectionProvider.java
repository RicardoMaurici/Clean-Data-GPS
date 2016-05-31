package br.ufsc.src.persistencia.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class used to provide the access and communication to on single database.
 * Before any method call it is needed to open a connection with the DB, when is done using
 * DB the connection must be closed. The configuration is still implemented as a class: DBConfig.java.
 * Further versions implementations should consider some alternatives for this.<br>
 * 
 * This is not intended to be used on web, or any concurrency applications, even though part of
 * the necessary treatment was given. For any more advanced applications consider a implementation
 * of a data pool.
 * 
 * @author	Artur Aquino, Vitor Fontes
 * @version	1.0, October 2012
 * @since	2012-10-23
 *
 */
public final class DBConnectionProvider {
	
	private static final DBConnectionProvider instance = new DBConnectionProvider();
	private static Connection conn;
	private static boolean isConnectionOpen = false;
	private static Statement statement;
	private static PreparedStatement preparedStatement;
	
	static{
		try {
			Class.forName(DBConfig.driverPostgres); // Loads class from driver
		} catch (ClassNotFoundException e) {
			DBConfig.LOGGER.severe("Error loading class from driver!");
		}
	}
	
	// Private constructor prevents instantiation from other classes
    private DBConnectionProvider() { }

    /**
     * Opens connection with database according to the settings specified on DBConfig.java.
     * If a connection was already established then the method returns.
     * 
     * @throws ClassNotFoundException if the class cannot be located
     * @throws SQLException if a database access error occurs
     */
	public void open() throws SQLException{
		if(isConnectionOpen) return;
		if(DBConfig.url.length() == 0) return;
		conn = DriverManager.getConnection(DBConfig.url + DBConfig.banco, DBConfig.usuario, DBConfig.senha);
		isConnectionOpen = true;

		DBConfig.LOGGER.fine("Connection Opened Successfully!");
	}
	
	/**
	 * Closes connection if it was established beforehand.
	 * 
	 * @throws SQLException if a database access error occurs
	 */
	public void close() throws SQLException{
		if(!isConnectionOpen) return;
		conn.close();
		isConnectionOpen=false;
		DBConfig.LOGGER.fine("Connection Closed Successfully!");
	}

	/**
	 * Executes the given SQL statement.
	 * 
	 * @param sql	any SQL statement
	 * @return		true if the first result is a ResultSet object; false if it is an update count or there are no results
	 * @throws SQLException if a database access error occurs
	 */
	public boolean execute(String sql) throws SQLException{
		return conn.createStatement().execute(sql);
	}
	
	public ResultSet executeQuery(String sql) throws SQLException{
		return conn.createStatement().executeQuery(sql);
	}

	public void addBatch(String sql) throws SQLException{
		
		statement.addBatch(sql);
	}

	public void createStatement() throws SQLException{
		statement = conn.createStatement();
	}
	
	public void executeBatch() throws SQLException{
		statement.executeBatch();
	}
	
	public void closeStatement() throws SQLException{
		statement.close();
	}
	
	public int getSequenceNextValue(String sequenceName) throws SQLException{
		ResultSet seq = statement.executeQuery("SELECT nextVal('"+sequenceName+"');");
		seq.next();
		return seq.getInt("nextVal");
	}
	
	public void startStatement() throws SQLException{
		this.createStatement();
	}

	/**
	 * Auxiliar internal method to help building SQL queries.
	 * 
	 * @param tabel		any table of database
	 * @param fields	any valid fields of a SQL statement
	 * @param where		any valid WHERE clause
	 * @param addition	anything else to complement the statement
	 * @return			a String representing the whole SQL statement
	 */
	private String buildSQLQuery(String tabel, String fields, String where, String order, String addition){
		if(fields == null)
			fields = "*";

		String sql = "select " + fields + " from " + tabel;

		if(where != null)
			sql = sql + " where " + where;

		if(order != null)
			sql = sql + " order by " + order;
		
		if(addition != null)
			sql = sql + " " + addition;

		return sql;
	}

	/**
	 * Executes a query on the specified database table, according to the parameters.
	 * It is probably more efficient, since it uses a simple statement.
	 * 
	 * @param tabel		any table of database
	 * @param fields	any valid fields of a SQL statement
	 * @param where		any valid WHERE clause
	 * @param addition	anything else to complement the statement
	 * @return			a new default Statement object 
	 * @throws SQLException if a database access error occurs or this method is called on a closed connection
	 */
	public ResultSet quickQuery(String tabel, String fields, String where, String addition) throws SQLException{
		String sql = buildSQLQuery(tabel, fields, where, null, addition);
//		System.out.println(sql);
		return conn.createStatement().executeQuery(sql);
	}

	/**
	 * Executes a query on the specified database table, according to the parameters.
	 * It is probably less efficient, since it uses a more complex statement that allows a better fetching of the ResultSet. 
	 * 
	 * @param tabel		any table of database
	 * @param fields	any valid fields of a SQL statement
	 * @param where		any valid WHERE clause
	 * @param addition	anything else to complement the statement
	 * @return			a new Statement object that will generate ResultSet objects with the given type and concurrency 
	 * @throws SQLException if a database access error occurs or this method is called on a closed connection
	 */
	public ResultSet query(String tabel, String fields, String where, String addition) throws SQLException{
		String sql = buildSQLQuery(tabel, fields, where, null, addition);
		// Allows to walk the ResultSet in any direction
		return conn.createStatement(ResultSet.FETCH_UNKNOWN, ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
	}

	/**
	 * Executes a query on the specified database table, according to fields and ordered by order.
	 * 
	 * @param tabel		any table of the database
	 * @param fields	any valid fields of a SQL statement
	 * @param order		any valid ORDER BY clause
	 * @return			a ResultSet object that contains the data produced by the given query; never null.
	 * @throws SQLException if a database access error occurs or this method is called on a closed connection
	 */
	public ResultSet orderedQuery(String tabel, String fields, String order) throws SQLException{
		String sql = buildSQLQuery(tabel, fields, null, order, null);
		return conn.createStatement(ResultSet.FETCH_UNKNOWN, ResultSet.CONCUR_READ_ONLY).executeQuery(sql);
	}

	/**
	 * Checks whether connection is opened
	 * 
	 * @return true if connection is open, otherwise false
	 */
	public static boolean isConnectionOpen() {
		return DBConnectionProvider.isConnectionOpen;
	}
	
	/**
	 * Returns the Singleton instance of the class which communicates with the database
	 * 
	 * @return instance of DBConnectionProveider.java
	 */
	public static final DBConnectionProvider getInstance(){
		return DBConnectionProvider.instance;
	}

	public void setAutoCommit(boolean bool) throws SQLException {
		conn.setAutoCommit(bool);
	}
	
	public void commit() throws SQLException {
		conn.commit();
	}
	
}