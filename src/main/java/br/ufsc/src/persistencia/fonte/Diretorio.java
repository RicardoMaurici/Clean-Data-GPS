package br.ufsc.src.persistencia.fonte;

import java.util.ArrayList;

public class Diretorio {
	private String url;
	private boolean igExtensao;
	private ArrayList<String>extensao = new ArrayList<String>();
	private ArrayList<String>igDiretorio = new ArrayList<String>();
	private ArrayList<String>igArquivo = new ArrayList<String>();
	
	public String toString(){
		return "------- Diretorio -------"
				+"\n url: "+url
				+"\n Ignorar extensao: "+igExtensao
				+"\n Extensoes: "+extensao.toString()
				+"\n Ignorar diretorios: "+igDiretorio.toString()
				+"\n Ignorar arquivos: "+igArquivo.toString();
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isIgExtensao() {
		return igExtensao;
	}
	public void setIgExtensao(boolean igExtensao) {
		this.igExtensao = igExtensao;
	}
	public ArrayList<String> getExtensao() {
		return extensao;
	}
	public void setExtensao(ArrayList<String> extensao) {
		this.extensao = extensao;
	}
	public ArrayList<String> getIgDiretorio() {
		return igDiretorio;
	}
	public void setIgDiretorio(ArrayList<String> igDiretorio) {
		this.igDiretorio = igDiretorio;
	}
	public ArrayList<String> getIgArquivo() {
		return igArquivo;
	}
	public void setIgArquivo(ArrayList<String> igArquivo) {
		this.igArquivo = igArquivo;
	}
}
