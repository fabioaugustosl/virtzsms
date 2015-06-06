package br.com.virtz.virtzsms.repositorio;

import br.com.virtz.beans.CobrancaMensagem;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;


public class CobrancaMensagemRepositorio {
	
	private static final String COBRANCA_TABELA = "CobrancaMensagem";

	public CobrancaMensagem recuperar(String senha){
		Entity cobranca = getCobranca(senha);
	    
	    if(cobranca != null){
	    	return entityToCobranca(cobranca);
	    }
	    return null;
	}
	
	
	public void salvar(CobrancaMensagem cobrancaMensagem){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity cobranca = getCobranca(cobrancaMensagem.getSenhaPermissao());
		
		if(cobranca == null){
			Key keyCobranca = KeyFactory.createKey(COBRANCA_TABELA, cobrancaMensagem.getSenhaPermissao());
			cobranca = new Entity(COBRANCA_TABELA, keyCobranca);
		}

		cobranca.setProperty("senhaPermissao",  cobrancaMensagem.getSenhaPermissao());
		cobranca.setProperty("valor", cobrancaMensagem.getValor());
		    
		datastore.put(cobranca);
		
	}
	

	private Entity getCobranca(String senha) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Key keyCobranca = KeyFactory.createKey(COBRANCA_TABELA, senha);
	    Query query = new Query(COBRANCA_TABELA, keyCobranca); 
	    Entity cobranca = datastore.prepare(query).asSingleEntity();
		return cobranca;
	}
	
	
	private CobrancaMensagem entityToCobranca(Entity e) {
		CobrancaMensagem cobranca = new CobrancaMensagem();
		
		cobranca.setSenhaPermissao(null);
		cobranca.setValor((e.getProperty("valor") != null ? Double.valueOf(String.valueOf(e.getProperty("valor"))) : null));
		
		return cobranca;
	}
	

}
