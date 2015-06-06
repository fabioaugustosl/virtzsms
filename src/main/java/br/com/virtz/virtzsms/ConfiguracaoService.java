package br.com.virtz.virtzsms;

import br.com.virtz.beans.CobrancaMensagem;
import br.com.virtz.virtzsms.repositorio.CobrancaMensagemRepositorio;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;

@Api(	name = "configuracao", 
		version = "v1")
public class ConfiguracaoService {

	
	@ApiMethod(name = "configuracao.inserirvalorcobranca", 
			path = "configuracao", 
			httpMethod = ApiMethod.HttpMethod.POST)
	public void inserirValorCobranca(@Named("senha") String senha, @Named("valor") Double valor) throws Exception {
		if(senha == null && valor == null){
			throw new Exception("[ERRO] 1 - Senha e valor são obrigatórios");
		}
		
		if(!CobrancaMensagem.CHAVE.equals(senha)){
			throw new Exception("[ERRO] 2 - Senha inválida");
		}
		
		CobrancaMensagemRepositorio repositorio = new CobrancaMensagemRepositorio();
		
		CobrancaMensagem cobranca = new CobrancaMensagem();
		cobranca.setSenhaPermissao(CobrancaMensagem.CHAVE);
		cobranca.setValor(valor);
		
		repositorio.salvar(cobranca);
	}
	
	@ApiMethod(name = "configuracao.atualizarvalorcobranca", 
				path = "configuracao", 
				httpMethod = ApiMethod.HttpMethod.PUT)
	public void atualizarValorCobranca(@Named("senha") String senha, @Named("valor") Double valor) throws Exception {
		if(senha == null && valor == null){
			throw new Exception("[ERRO] 1 - Senha e valor são obrigatórios");
		}
		
		if(!CobrancaMensagem.CHAVE.equals(senha)){
			throw new Exception("[ERRO] 2 - Senha inválida");
		}
		
		CobrancaMensagemRepositorio repositorio = new CobrancaMensagemRepositorio();
		
		CobrancaMensagem cobranca = new CobrancaMensagem();
		cobranca.setSenhaPermissao(CobrancaMensagem.CHAVE);
		cobranca.setValor(valor);
		
		repositorio.salvar(cobranca);
	}
	
	
	@ApiMethod(
	        name = "configuracao.recuperar",
	        path = "configuracao",
	        httpMethod = ApiMethod.HttpMethod.GET)
	public CobrancaMensagem recuperar() {
		CobrancaMensagemRepositorio repositorio = new CobrancaMensagemRepositorio();
		return repositorio.recuperar(CobrancaMensagem.CHAVE);
	}
	
	
}
