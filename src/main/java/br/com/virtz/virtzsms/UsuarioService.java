package br.com.virtz.virtzsms;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.virtz.beans.CobrancaMensagem;
import br.com.virtz.beans.HistoricoSaldo;
import br.com.virtz.beans.TipoEventoSaldo;
import br.com.virtz.beans.Usuario;
import br.com.virtz.virtzsms.repositorio.CobrancaMensagemRepositorio;
import br.com.virtz.virtzsms.repositorio.HistoricoSaldoRepositorio;
import br.com.virtz.virtzsms.repositorio.UsuarioRepositorio;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;

@Api(	name = "usuarios", 
		version = "v1")
public class UsuarioService {

	
	@ApiMethod(name = "usuario.novo", 
				path = "usuario", 
				httpMethod = ApiMethod.HttpMethod.POST)
	public Usuario novoUsuario(@Named("email") String email) throws Exception {
		UsuarioRepositorio repositorio = new UsuarioRepositorio();
		
		validarNovoCadastro(email, repositorio);
		
		Usuario u = new Usuario(); 
		u.setEmail(email);
		u.setDataCriacao(new Date());
		u.setSaldo(0d);
		
		String token = null;
		try {
			token = getTokenHash(email);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception(" 4 : erro ao gerar token para usuário");
		}
		 
		u.setToken(token);
		
		// recupera o valor default por mensagem
		CobrancaMensagemRepositorio repositorioCobranca = new CobrancaMensagemRepositorio();
		CobrancaMensagem cobranca = repositorioCobranca.recuperar(CobrancaMensagem.CHAVE);
		if(cobranca != null && cobranca.getValor() != null){
			u.setValorPorMensagem(cobranca.getValor());
		}
		
		repositorio.salvar(u);
		 
		return u;
	}
	
	
	@ApiMethod(name = "usuario.alterarvalormensagem", 
			path = "alterarvalormensagem", 
			httpMethod = ApiMethod.HttpMethod.PUT)
	public Usuario alterarValorMensagem(@Named("token") String token, @Named("valor") Double valor) throws Exception {
		UsuarioRepositorio repositorio = new UsuarioRepositorio();
		
		Usuario usuario = repositorio.recuperar(token);	
		if(usuario == null){
			throw new Exception("[ERRO] 1 : usuário inexistente");
		}
		
		if(valor == null || valor < 0){
			throw new Exception("[ERRO] 2 : valor inválido");
		}

		usuario.setValorPorMensagem(valor);		
		repositorio.salvar(usuario);
		
		return usuario;
	}
	
	
	@ApiMethod(name = "usuario.comprarcredito", 
			path = "creditar", 
			httpMethod = ApiMethod.HttpMethod.POST)
	public Usuario comprarCredito(@Named("token") String token, @Named("valor") Double valor) throws Exception {
		UsuarioRepositorio repositorio = new UsuarioRepositorio();
		
		validarOperacaoSaldo(token, valor);
		
		// Exceção 3 : usuário inexistente
		Usuario usuario = repositorio.recuperar(token);	
		if(usuario == null){
			throw new Exception("3 : usuário inexistente");
		}

		BigDecimal saldo = new BigDecimal(usuario.getSaldo() != null ? usuario.getSaldo() : 0d);
		BigDecimal valorCredito = new BigDecimal(valor);

		usuario.setSaldo(saldo.add(valorCredito).doubleValue());
		
		repositorio.salvar(usuario);
		
		gravaHistorico(token, valor, TipoEventoSaldo.CREDITO);
		
		return usuario;
	}
	
	
	@ApiMethod(name = "usuario.debitarcredito", 
			path = "debitar", 
			httpMethod = ApiMethod.HttpMethod.POST)
	public Usuario debitarCredito(@Named("token") String token, @Named("valor") Double valor) throws Exception {
		UsuarioRepositorio repositorio = new UsuarioRepositorio();
		
		validarOperacaoSaldo(token, valor);
		
		// Exceção 3 : usuário inexistente
		Usuario usuario = repositorio.recuperar(token);		
		if(usuario == null){
			throw new Exception("3 : usuário inexistente");
		}
		
		BigDecimal saldo = new BigDecimal(usuario.getSaldo());
		BigDecimal valorCredito = new BigDecimal(valor);
		usuario.setSaldo(saldo.subtract(valorCredito).doubleValue());
		if(usuario.getSaldo() < 0d){
			throw new Exception("4 : saldo insuficiente");
		}
				
		repositorio.salvar(usuario);
		
		gravaHistorico(token, valor, TipoEventoSaldo.DEBITO);
		
		return usuario;
	}



	@ApiMethod(name = "usuario.listar", 
				path = "listar", 
				httpMethod = ApiMethod.HttpMethod.GET)
	public List listarUsuarios() throws Exception {
		 UsuarioRepositorio repositorio = new UsuarioRepositorio();
		 return repositorio.recuperarTodos();
	}
	
	
	@ApiMethod(name = "usuario.extrato", 
			path = "extrato/{token}", 
			httpMethod = ApiMethod.HttpMethod.GET)
	public List extrato(@Named("token") String token) throws Exception {
		 HistoricoSaldoRepositorio saldoRepositorio = new HistoricoSaldoRepositorio();
		 return saldoRepositorio.recuperar(token);
	}
	
	
	@ApiMethod(
        name = "usuario.delete",
        path = "usuario/{token}",
        httpMethod = ApiMethod.HttpMethod.DELETE)
    public void delete(@Named("token") String token) throws Exception {
		UsuarioRepositorio repositorio = new UsuarioRepositorio();
		
		Usuario temp = repositorio.recuperar(token);		
		if(temp == null){
			throw new Exception("5 : usuário inexistente");
		}
		
		repositorio.remover(token);
    }
	
	
	@ApiMethod(
	        name = "usuario.recuperar",
	        path = "usuario/{token}",
	        httpMethod = ApiMethod.HttpMethod.GET)
	public Usuario recuperar(@Named("token") String token) {
		 UsuarioRepositorio repositorio = new UsuarioRepositorio();
		 return repositorio.recuperar(token);
	}
	
	
	@ApiMethod(
	        name = "usuario.recuperaremail",
	        path = "usuarioemail/{email}",
	        httpMethod = ApiMethod.HttpMethod.GET)
	public Usuario recuperarEmail(@Named("email") String email) {
		 UsuarioRepositorio repositorio = new UsuarioRepositorio();
		 return repositorio.recuperarPorEmail(email);
	}
	
	
	
	
	
	// MÉTODOS AUXILIARES
	
	private void gravaHistorico(String token, Double valor, TipoEventoSaldo tipo) {
		HistoricoSaldo historico = new HistoricoSaldo();
		historico.setDataEvento(new Date());
		historico.setTipoEvento(tipo);
		historico.setTokenUsuario(token);
		historico.setValor(valor);
		
		HistoricoSaldoRepositorio historicoRepositorio = new HistoricoSaldoRepositorio();
		historicoRepositorio.salvarNovo(historico);
	}
	
	
	private void validarNovoCadastro(String email, UsuarioRepositorio repositorio) throws Exception {
		// Exceção 1 : email não enviado
		if(email== null || email.trim() == ""){
			throw new Exception("1 : email não enviado");
		}
		
		// Exceção 2 : email inválido
		if(!validarEmail(email)){
			throw new Exception("2 : email inválido");
		}
		
		// Exceção 3 : email já utilizado
		Usuario temp = repositorio.recuperarPorEmail(email);		
		if(temp != null){
			throw new Exception("3 : email já utilizado");
		}
	}
	
	
	private void validarOperacaoSaldo(String token, Double valor) throws Exception {
		// Exceção 1 : token não enviado
		if(token == null || token.trim() == ""){
			throw new Exception("1 : token não enviado");
		}
		
		// Exceção 2 : valor inválido
		if(valor == null || valor == 0d || valor < 0d){
			throw new Exception("2 : valor inválido");
		}
		
	}
	
	private String getTokenHash(String email) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(email.getBytes());
		byte[] bytes = md.digest();

		StringBuilder s = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
			int parteBaixa = bytes[i] & 0xf;
			if (parteAlta == 0)
				s.append('0');
			s.append(Integer.toHexString(parteAlta | parteBaixa));
		}
		return s.toString();
	}
	
	
	private boolean validarEmail(String email) {
	    Pattern p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$"); 
	    Matcher m = p.matcher(email); 
	    if (m.find()){
	      return true;
	    } else {
	      return false;
	    }  
	 }
}
