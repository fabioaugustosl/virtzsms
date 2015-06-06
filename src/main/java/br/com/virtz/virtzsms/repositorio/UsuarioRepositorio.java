package br.com.virtz.virtzsms.repositorio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.virtz.beans.Usuario;

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


public class UsuarioRepositorio {
	
	
	private static final String USUARIO_TABELA = "Usuario";


	public Usuario recuperar(String token){
		Entity usuario = getUsuario(token);
	    
	    if(usuario != null){
	    		return entityToUsuario(usuario);
	    }
	    return null;
	}


	private Entity getUsuario(String token) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Key keyUsuario = KeyFactory.createKey(USUARIO_TABELA, token);
	    Query query = new Query(USUARIO_TABELA, keyUsuario); //.addSort("date", Query.SortDirection.DESCENDING);
	    Entity usuario = datastore.prepare(query).asSingleEntity();
		return usuario;
	}

	
	public Usuario recuperarPorEmail(String email){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter filterEmailIgual = new FilterPredicate("email", FilterOperator.EQUAL, email);
		
	    Query query = new Query(USUARIO_TABELA).setFilter(filterEmailIgual);
	    
	    List<Entity> usuarios = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1));
	   
	    if(usuarios != null && !usuarios.isEmpty()){
	    	Entity e = usuarios.get(0);
	    	return entityToUsuario(e);
	    }
	    
	    return null;
	}

	
	
	public List<Usuario> recuperarTodos() throws Exception{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Query query = new Query(USUARIO_TABELA); //.addSort("date", Query.SortDirection.DESCENDING);
	    List<Entity> usus = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(1000));
	    
	    List<Usuario> usuarios = new ArrayList<>();
	    if(usus != null){
	    	for (Entity u : usus) {
	    		Usuario usuario = entityToUsuario(u);
	    		usuarios.add(usuario);
	    	}
	    }
	    return usuarios;
	}
	
	
	public void salvar(Usuario usuario){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Entity usu = getUsuario(usuario.getToken());
		
		if(usu == null){
			Key keyUsuario = KeyFactory.createKey(USUARIO_TABELA, usuario.getToken());
			usu = new Entity(USUARIO_TABELA, keyUsuario);
		}

		usu.setProperty("email", usuario.getEmail());
		usu.setProperty("token", usuario.getToken());
		usu.setProperty("dataCriacao", usuario.getDataCriacao());
		usu.setProperty("saldo", usuario.getSaldo());
		usu.setProperty("valorPorMensagem", usuario.getValorPorMensagem());
		    
		datastore.put(usu);
	}
	
	
	public void remover(String token){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key keyUsuario = KeyFactory.createKey(USUARIO_TABELA, token);
		datastore.delete(keyUsuario);
	}
	
	
	private Usuario entityToUsuario(Entity e) {
		Usuario usuario = new Usuario();
		usuario.setDataCriacao(e.getProperty("dataCriacao") != null ? ((Date) e.getProperty("dataCriacao")) : null);
		usuario.setEmail(e.getProperty("email") != null ? String.valueOf(e.getProperty("email")) : null);
		usuario.setToken(e.getProperty("token") != null ? String.valueOf(e.getProperty("token")) : null);
		usuario.setSaldo(e.getProperty("saldo") != null ? Double.valueOf(e.getProperty("saldo").toString()) : null);
		usuario.setValorPorMensagem(e.getProperty("valorPorMensagem") != null ? Double.valueOf(e.getProperty("valorPorMensagem").toString()) : null);
		return usuario;
	}
}
