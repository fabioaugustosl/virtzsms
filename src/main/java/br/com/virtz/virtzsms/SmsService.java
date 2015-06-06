package br.com.virtz.virtzsms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.com.virtz.beans.Sms;
import br.com.virtz.beans.Usuario;
import br.com.virtz.excetions.NumeroTelefoneInvalido;
import br.com.virtz.virtzsms.repositorio.SmsRepositorio;
import br.com.virtz.virtzsms.repositorio.UsuarioRepositorio;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;

@Api(	name = "sms", 
		version = "v1")
public class SmsService {

	
	@ApiMethod(name = "sms.enviar", 
				path = "enviar", 
				httpMethod = ApiMethod.HttpMethod.POST)
	public void enviar(@Named("tokenUsuario") String token, @Named("telefone") String telefone, @Named("mensagem") String mensagem, @Named("dataEnvio") @Nullable Date dataEnvio) throws Exception {
		validacaoBasica(token, telefone, mensagem);
		
		UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();
		
		Usuario usuario = usuarioRepositorio.recuperar(token);
		if(usuario == null){
			throw new Exception("5 : usuário não encontrado com token : "+token);
		}

		Sms sms = criaSms(token, telefone, mensagem, dataEnvio);
		sms.setValorCobrado(usuario.getValorPorMensagem());

		// debita primeiro para validar se o usuario possui crédido
		UsuarioService usuarioService = new UsuarioService();
		usuarioService.debitarCredito(token, sms.getValorCobrado());
		
		// envia o sms
		EnviadorSms enviador = new EnviadorSms();
		enviador.enviar(sms);
		
		enviador = null;
		usuarioService = null;
		usuarioRepositorio = null;
	}
	
	
	private void enviarAgendado(Sms sms) throws Exception {
		
		// debita primeiro para validar se o usuario possui crédido
		UsuarioService usuarioService = new UsuarioService();
		usuarioService.debitarCredito(sms.getTokenUsuario(), sms.getValorCobrado());
		
		// envia o sms
		EnviadorSms enviador = new EnviadorSms();
		enviador.enviar(sms);
		
		enviador = null;
		usuarioService = null;
	}
	
	
	@ApiMethod(
	        name = "sms.todos",
	        path = "todos/{tokenUsuario}",
	        httpMethod = ApiMethod.HttpMethod.GET)
	public List<Sms> recuperarTodos(@Named("tokenUsuario") String tokenUsuario) throws Exception {
		 SmsRepositorio smsRepositorio = new SmsRepositorio();
		 List<Sms> todos = smsRepositorio.recuperarTodos(tokenUsuario);
		 return todos;
	}
	
	
	@ApiMethod(
	        name = "sms.todosfone",
	        path = "todosfone/{telefone}",
	        httpMethod = ApiMethod.HttpMethod.GET)
	public List<Sms> recuperarTodosFone(@Named("tokenUsuario") String token, @Named("telefone") String telefone) throws Exception {
		 SmsRepositorio smsRepositorio = new SmsRepositorio();
		 List<Sms> todos = smsRepositorio.recuperarPorFone(token, telefone);
		 return todos;
	}
	
	
	@ApiMethod(
	        name = "sms.agendados",
	        path = "agendados/{tokenUsuario}",
	        httpMethod = ApiMethod.HttpMethod.GET)
	public List<Sms> listarAgendados(@Named("tokenUsuario") String tokenUsuario) throws Exception {
		 SmsRepositorio smsRepositorio = new SmsRepositorio();
		 List<Sms> agendados = new ArrayList<Sms>();
		 List<Sms> todos = smsRepositorio.recuperarTodos(tokenUsuario);
		 
		 Calendar dataAtual = Calendar.getInstance();
		 
		 if(todos != null && !todos.isEmpty()){
			 for(Sms sms : todos){
				 if(sms.getDataAgendada() != null && dataAtual.before(sms.getDataAgendada()) 
						 && sms.getDataEnvio() == null){
					 agendados.add(sms);
				 }
			 }
		 }
		 
		 return agendados; 
	}
	
	
	@ApiMethod(
	        name = "sms.enviados",
	        path = "enviados/{tokenUsuario}",
	        httpMethod = ApiMethod.HttpMethod.GET)
	public List<Sms> listarEnviados(@Named("tokenUsuario") String tokenUsuario) throws Exception {
		 SmsRepositorio smsRepositorio = new SmsRepositorio();
		 List<Sms> enviados = new ArrayList<Sms>();
		 List<Sms> todos = smsRepositorio.recuperarTodos(tokenUsuario);
		 
		 if(todos != null && !todos.isEmpty()){
			 for(Sms sms : todos){
				 if(sms.getDataEnvio() != null){
					 enviados.add(sms);
				 }
			 }
		 }
		 
		 return enviados; 
	}
	
	
	
	@ApiMethod(
	        name = "sms.disparar",
	        path = "disparar",
	        httpMethod = ApiMethod.HttpMethod.GET)
	public void dispararAgendados() throws Exception {
		 SmsRepositorio smsRepositorio = new SmsRepositorio();
		 List<Sms> agendados = smsRepositorio.recuperarNaoEnviados();
		 List<Sms> enviar = smsRepositorio.recuperarNaoEnviados();
		 
		 Calendar dataAtual = Calendar.getInstance();
		 
		 if(agendados != null && !agendados.isEmpty()){
			 for(Sms sms : agendados){
				 if(sms.getDataAgendada() != null && dataAtual.before(sms.getDataAgendada()) ){
					 enviar.add(sms);
				 }
			 }
		 }
		 
		 for(Sms sms : enviar){
			 try{
				 enviarAgendado(sms);
				 // TODO: log de sucesso
			 }catch(Exception e){
				 // TODO: log de falha
			 }
		 }
		  
	}
	
	
	@ApiMethod(
	        name = "sms.remover",
	        path = "remover",
	        httpMethod = ApiMethod.HttpMethod.DELETE)
	public void removerSms(@Named("tokenUsuario") String tokenUsuario, @Named("telefone") String telefone, @Named("dataCriacao") Double dataCriacao) throws Exception {
	
		throw new Exception("[ERRO][1] Funcionalidade não implementada.");
		
		/*if(tokenUsuario == null || telefone == null || dataCriacao == null){
			throw new Exception("[ERRO][1] Token, telefone e data de criação são obrigatórios.");
		}
		
		SmsRepositorio smsRepositorio = new SmsRepositorio(); */
	}
	
	
	
	
	// MÉTODOS AUXILIARES
	
	private void validacaoBasica(String token, String telefone, String mensagem) throws NumeroTelefoneInvalido, Exception {
		// Exceção 1 : token não enviado
		if(token == null || token.trim() == ""){
			throw new Exception("1 : token não enviado");
		}
		
		
		// Exceção 2 : numero inválido
		if(telefone != null){
			String fone = telefone.replaceAll("[^0-9]", "");
			if(fone.length() < 10 || fone.length() > 11){
				throw new NumeroTelefoneInvalido("2 : telefone inválido");
			}
		} else {
			throw new Exception("3 : telefone não enviado");
		}
		
		// Exceção 4 : mensagem não enviado
		if(mensagem == null || mensagem.trim() == ""){
			throw new Exception("4 : mensagem não enviado");
		}
		
	}
	
	
	private Sms criaSms(String token, String telefone, String mensagem, Date dataEnvio) {
		Sms sms = new Sms();
		sms.setDataCriacao(new Date());
		sms.setMensagem(mensagem);
		sms.setTokenUsuario(token);
		
		telefone = telefone.replaceAll("[^0-9]", "");
		sms.setTelefone(new Long(telefone));
		
		sms.setOkOperadora(false);
		
		if(dataEnvio != null){
			sms.setDataAgendada(dataEnvio);
		}
		
		return sms;
	}
	
}
