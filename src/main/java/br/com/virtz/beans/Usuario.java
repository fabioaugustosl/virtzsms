package br.com.virtz.beans;

import java.util.Date;

import com.google.appengine.api.datastore.Key;


public class Usuario {
	
	private Key id;
	public String email;
	public String token;
	public Date dataCriacao;
	public Double saldo;
	private Double valorPorMensagem;

	
	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Double getSaldo() {
		return saldo;
	}

	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}

	public Double getValorPorMensagem() {
		return valorPorMensagem;
	}

	public void setValorPorMensagem(Double valorPorMensagem) {
		this.valorPorMensagem = valorPorMensagem;
	}
	
	
}
