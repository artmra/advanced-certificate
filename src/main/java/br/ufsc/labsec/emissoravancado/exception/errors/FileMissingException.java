package br.ufsc.labsec.emissoravancado.exception.errors;

public class FileMissingException extends RuntimeException {
    public FileMissingException(String errorMessage) {
        super(errorMessage);
    }
}
