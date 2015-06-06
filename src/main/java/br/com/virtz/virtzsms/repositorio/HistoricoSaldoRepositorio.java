package br.com.virtz.virtzsms.repositorio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.virtz.beans.HistoricoSaldo;
import br.com.virtz.beans.TipoEventoSaldo;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;


public class HistoricoSaldoRepositorio {
	
	
	public List<HistoricoSaldo> recuperar(String tokenUsuario){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter filterEmailIgual = new FilterPredicate("tokenUsuario", FilterOperator.EQUAL, tokenUsuario);
		
	    Query query = new Query("HistoricoSaldo").setFilter(filterEmailIgual);
	    
	    List<Entity> saldos = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
	   
	    List<HistoricoSaldo> historicos = new ArrayList<HistoricoSaldo>();
	    if(saldos != null && !saldos.isEmpty()){
	    	for(Entity e : saldos){
	    		historicos.add(entityToHistoricoSaldo(e));
	    	}
	    }
	    
	    return historicos;
	}


	public void salvarNovo(HistoricoSaldo historico){
		Key keyUsuario = KeyFactory.createKey("HistoricoSaldo", historico.getChave());

		Entity ent = new Entity("HistoricoSaldo", keyUsuario);
		ent.setProperty("valor", historico.getValor());
		ent.setProperty("tokenUsuario", historico.getTokenUsuario());
		ent.setProperty("tipoEvento", historico.getTipoEvento().toString());
		ent.setProperty("dataEvento", historico.getDataEvento());
		    
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(ent);
	}
	
	
	private HistoricoSaldo entityToHistoricoSaldo(Entity e) {
		HistoricoSaldo historico = new HistoricoSaldo();
		historico.setDataEvento(e.getProperty("dataEvento") != null ? ((Date) e.getProperty("dataEvento")) : null);
		historico.setTokenUsuario(e.getProperty("tokenUsuario") != null ? String.valueOf(e.getProperty("tokenUsuario")) : null);
		historico.setValor(e.getProperty("valor") != null ? Double.valueOf(String.valueOf(e.getProperty("valor"))) : null);
		
		if(e.getProperty("tipoEvento") != null){
			TipoEventoSaldo tipo = TipoEventoSaldo.valueOf(String.valueOf(e.getProperty("tipoEvento")));
			historico.setTipoEvento(tipo);
		}
		
		return historico;
	}


}
