package br.ufsc.src.controle;

import br.ufsc.src.persistencia.InterfacePersistencia;
import br.ufsc.src.persistencia.Persistencia;
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

public class ServicosControle {
	
	InterfacePersistencia persistencia;
	
	public ServicosControle(InterfacePersistencia persistencia){
		this.persistencia = persistencia;
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
	
	public boolean isConnectionOpen(){
		try {
			return this.persistencia.testaConexao();
		} catch (DBConnectionException e) {
			return false;
		}
	}

	public void carregaArquivo(TrajetoriaBruta tb, Diretorio dir) throws TimeStampException, LoadDataFileException, GetSequenceException, CreateSequenceException, UpdateGeomException, CreateStatementException, AddBatchException, FileNFoundException, ExecuteBatchException, DBConnectionException {
		persistencia.carregaArquivo(dir, tb);
	}
	
	public void createTable(TrajetoriaBruta tb) throws SyntaxException, CreateTableException, DBConnectionException{
		persistencia.createTable(tb.getTabelaBanco(), tb.getTableData(), tb.isGID(), tb.isTID(), tb.isMetaData());
	}
	
}
