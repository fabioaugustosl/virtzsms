package br.com.virtz.virtzsms;

import java.util.Date;
import java.util.List;

import br.com.virtz.beans.Sms;
import br.com.virtz.virtzsms.repositorio.SmsRepositorio;

public class EnviadorSms {

	
	public void enviar(Sms sms) {
		SmsRepositorio smsRepositorio = new SmsRepositorio();
		
		boolean enviar = true;
		
		// se o cara agendou o envio 
		if(sms.getDataAgendada() != null && sms.getDataAgendada().after(new Date())){
			enviar = false;
		}
		
		
		if(enviar) {
			sms.setDataEnvio(new Date());
			
			// TODO : enviar o sms
		}
		
		smsRepositorio.salvar(sms);
	}
	
	
	public void enviar(List<Sms> sms) {
		SmsRepositorio smsRepositorio = new SmsRepositorio();
		
		// TODO : enviar o sms
		
		smsRepositorio.salvar(sms);
	}
	

}
