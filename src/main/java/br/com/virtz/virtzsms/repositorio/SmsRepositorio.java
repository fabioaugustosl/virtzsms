package br.com.virtz.virtzsms.repositorio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.virtz.beans.Sms;

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


public class SmsRepositorio {
	
	private static final String SMS_TABELA = "Mensagem";

	/**
	 * 
	 * @param token
	 * @param telefone 3188512273 - só os números com ddd
	 * @return
	 */
	private Entity getSms(String token, Long telefone, Date dataCriacao) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Key keySms = criarKeyParaSms(token, telefone, dataCriacao);
	    
	    Query query = new Query(SMS_TABELA, keySms); 
	    Entity sms = datastore.prepare(query).asSingleEntity();
		return sms;
	}

	
	public List<Sms> recuperarTodos(String token) throws Exception{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Filter filterTokenIgual = new FilterPredicate("tokenUsuario", FilterOperator.EQUAL, token);
		
	    Query query = new Query(SMS_TABELA).setFilter(filterTokenIgual).addSort("dataCriacao", Query.SortDirection.DESCENDING);
	    List<Entity> lista = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(9999));
	    
	    List<Sms> todosSms = new ArrayList<>();
	    if(lista != null){
	    	for (Entity s : lista) {
	    		Sms sms = entityToSms(s);
	    		todosSms.add(sms);
	    	}
	    }
	    return todosSms;
	}
	
	
	public List<Sms> recuperarPorFone(String token, String fone) throws Exception{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter filterTokenIgual = new FilterPredicate("tokenUsuario", FilterOperator.EQUAL, token);
		Filter filterFoneIgual = new FilterPredicate("telefone", FilterOperator.EQUAL, fone);
		
	    Query query = new Query(SMS_TABELA).setFilter(filterFoneIgual).setFilter(filterTokenIgual).addSort("dataCriacao", Query.SortDirection.DESCENDING);
	    
	    List<Entity> lista = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(9999));
	    List<Sms> todosSms = new ArrayList<>();
		for (Entity entity : lista) {
			Sms sms = entityToSms(entity);
    		todosSms.add(sms);
		}
		
		return todosSms;

	}
	
	
	public List<Sms> recuperarNaoEnviados() throws Exception{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter filterTokenIgual = new FilterPredicate("dataEnvio", FilterOperator.EQUAL, null);
		
	    Query query = new Query(SMS_TABELA).setFilter(filterTokenIgual);
	    
	    List<Entity> lista = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(9999));
	    List<Sms> todosSms = new ArrayList<>();
		for (Entity entity : lista) {
			Sms sms = entityToSms(entity);
    		todosSms.add(sms);
		}
		
		return todosSms;

	}
	
	
	public void salvar(Sms smsSalvar){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity sms = montarSmsEntityParaSalvar(smsSalvar);
		
		datastore.put(sms);
	}

	
	public void salvar(List<Sms> smsSalvar){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		List<Entity> sms = new ArrayList<Entity>();
		
		for(Sms s : smsSalvar){
			Entity smsEntity = montarSmsEntityParaSalvar(s);
			sms.add(smsEntity);
		}
		
		datastore.put(sms);
	}
	
	
	public void remover(Sms smsRemover){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key keySms = criarKeyParaSms(smsRemover.getTokenUsuario(), smsRemover.getTelefone(), smsRemover.getDataCriacao());
		datastore.delete(keySms);
	}


	private Entity montarSmsEntityParaSalvar(Sms smsSalvar) {
		Entity sms = getSms(smsSalvar.getTokenUsuario(), smsSalvar.getTelefone(), smsSalvar.getDataCriacao());
		
		if(sms == null){
			Key keySms = criarKeyParaSms(smsSalvar.getTokenUsuario(), smsSalvar.getTelefone(), smsSalvar.getDataCriacao());
			sms = new Entity(SMS_TABELA, keySms);
		}

		sms.setProperty("tokenUsuario", smsSalvar.getTokenUsuario());
		sms.setProperty("telefone", smsSalvar.getTelefone());
		sms.setProperty("mensagem", smsSalvar.getMensagem());
		sms.setProperty("okOperadora", smsSalvar.getOkOperadora());
		sms.setProperty("dataCriacao", prepararDataCriacao(smsSalvar.getDataCriacao()));
		sms.setProperty("dataAgendada", smsSalvar.getDataAgendada());
		sms.setProperty("dataEnvio", smsSalvar.getDataEnvio());
		sms.setProperty("valorCobrado", smsSalvar.getValorCobrado());
		return sms;
	}
	
	
	private Sms entityToSms(Entity e) {
		Sms sms = new Sms();
		
		sms.setDataCriacao(e.getProperty("dataCriacao") != null ? ((Date) e.getProperty("dataCriacao")) : null);
		sms.setDataAgendada(e.getProperty("dataAgendada") != null ? ((Date) e.getProperty("dataAgendada")) : null);
		sms.setDataEnvio(e.getProperty("dataEnvio") != null ? ((Date) e.getProperty("dataEnvio")) : null);
		
		sms.setTelefone(e.getProperty("telefone") != null ? Long.valueOf(e.getProperty("telefone").toString()) : null);
		sms.setTokenUsuario(e.getProperty("tokenUsuario") != null ? String.valueOf(e.getProperty("tokenUsuario")) : null);
		sms.setMensagem(e.getProperty("mensagem") != null ? String.valueOf(e.getProperty("mensagem")) : null);
		sms.setOkOperadora(e.getProperty("okOperadora") != null ? Boolean.valueOf(e.getProperty("okOperadora").toString()) : false);
		sms.setValorCobrado((e.getProperty("valorCobrado") != null ? Double.valueOf(String.valueOf(e.getProperty("valorCobrado"))) : null));
		
		return sms;
	}
	
	
	private Date prepararDataCriacao(Date dataCriacao){
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataCriacao);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
        
	}
	
	
	private Key criarKeyParaSms(String token, Long telefone, Date dataCriacao) {
		
		dataCriacao = prepararDataCriacao(dataCriacao);
		
		StringBuilder sb = new StringBuilder();
		sb.append(token).append(telefone.toString()).append(dataCriacao.toString());
		
		Key k = KeyFactory.createKey(SMS_TABELA, sb.toString());
		
		return k;
	}
	
		
}
