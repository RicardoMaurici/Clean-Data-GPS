package br.ufsc.src.igu.painel;

public enum EnumTipos {
	INTEGER ("integer")
	,SMALLINT ("smallint")
	,VARCHAR ("varchar")
	,NUMERIC ("numeric")
	,DECIMAL ("decimal")
	,SERIAL ("serial")
	,REAL ("real")
	,CHARACTERVARYING ("character varying")
	,TIMESTAMP ("timestamp without time zone")
	,POINT ("geometry(Point)");
	
	
	private String tipo;
	
	EnumTipos(String tp){
		this.tipo = tp;
	}
	
	public String toString(){
		return this.tipo;
	}
}
