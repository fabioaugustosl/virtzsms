package br.com.virtz.excetions;

public class NumeroTelefoneInvalido extends Exception {

	public NumeroTelefoneInvalido(String message, Throwable cause) {
		super(message, cause);
	}

	public NumeroTelefoneInvalido(String message) {
		super(message);
	}
	
}
