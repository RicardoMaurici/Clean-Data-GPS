package br.ufsc.src.control.dataclean;

public class ConfigTrajBroke {
	private Object[][] tableData;
	private String tableNameOrigin;
	private String accuracy;
	private String speed;
	private int sample;
	private double distanceMax;
	private String columnTID;

	private boolean status;
	
	public ConfigTrajBroke(Object[][] tableData, String tableNameOrigin, int sample, double distanceMax, boolean status){
		this.tableData = tableData;
		this.tableNameOrigin = tableNameOrigin;
		this.sample = sample;
		this.status = status;
		this.distanceMax = distanceMax;
	}

	public Object[][] getTableData() {
		return tableData;
	}

	public void setTableData(Object[][] tableData) {
		this.tableData = tableData;
	}

	public String getTableNameOrigin() {
		return tableNameOrigin;
	}

	public void setTableNameOrigin(String tableNameOrigin) {
		this.tableNameOrigin = tableNameOrigin;
	}

	public String getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public double getSample() {
		return sample;
	}

	public void setSample(int sample) {
		this.sample = sample;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public double getDistanceMax() {
		return distanceMax;
	}

	public void setDistanceMax(double distanceMax) {
		this.distanceMax = distanceMax;
	}
	
	public String getColumnName(String column){
		for (int i = 0; i <= tableData.length - 1; i++) {
			String colName = (String)tableData[i][0];
			String colKind = tableData[i].length > 1 ? (String)tableData[i][1] : null;
			if(colKind != null && colKind.equalsIgnoreCase(column))
				return colName;
		}
		return "";
	}
	
	public String getColumnTID() {
		return columnTID;
	}

	public void setColumnTID(String columnTID) {
		this.columnTID = columnTID;
	}
}