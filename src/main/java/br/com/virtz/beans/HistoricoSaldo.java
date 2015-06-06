package br.com.virtz.beans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.appengine.api.datastore.Key;

public class HistoricoSaldo {

	private Key id;
	public String tokenUsuario;
	public Date dataEvento;
	public Double valor;
	public TipoEventoSaldo tipoEvento;

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public String getTokenUsuario() {
		return tokenUsuario;
	}

	public void setTokenUsuario(String tokenUsuario) {
		this.tokenUsuario = tokenUsuario;
	}

	public Date getDataEvento() {
		return dataEvento;
	}

	public void setDataEvento(Date dataEvento) {
		this.dataEvento = dataEvento;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public TipoEventoSaldo getTipoEvento() {
		return tipoEvento;
	}

	public void setTipoEvento(TipoEventoSaldo tipoEvento) {
		this.tipoEvento = tipoEvento;
	}
	
	public String getChave(){
		StringBuilder sb = new StringBuilder();
		if(this.getTokenUsuario() != null){
			sb.append(this.getTokenUsuario());
		}
		if(this.getDataEvento() != null){
			sb.append(this.getDataEvento().toString());
		}
		return sb.toString();
	}

}
