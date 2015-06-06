package br.com.virtz.beans;

import com.google.appengine.api.datastore.Key;

public class CobrancaMensagem {
	
	public static final String CHAVE = "virtzz";

	private Key id;
	// campo senhaPermissao será usado para permitir alteração do valor apenas para quem tenha a senha. Senha fixa: virtzz  
	private String senhaPermissao;
	private Double valor;
	
	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getSenhaPermissao() {
		return senhaPermissao;
	}

	public void setSenhaPermissao(String senhaPermissao) {
		this.senhaPermissao = senhaPermissao;
	}
	
	

}
