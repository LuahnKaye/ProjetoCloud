package br.edu.ibmec.cartao_credito.exception;

public class TransacaoException extends Exception {
    
    public TransacaoException(String message) {
        super(message);
    }

    public TransacaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
