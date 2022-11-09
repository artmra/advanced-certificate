package br.ufsc.labsec.emissoravancado.exception.errors;

public class InternalErrorException extends RuntimeException {
    public InternalErrorException(String errorMessage) {
        super(errorMessage);
    }
}
