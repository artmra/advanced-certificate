package br.ufsc.labsec.emissoravancado.exception.handlers;

import br.ufsc.labsec.emissoravancado.exception.errors.InternalErrorException;
import br.ufsc.labsec.emissoravancado.exception.errors.VerifierUnavailableException;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class VerifierResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                || response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            throw new InternalErrorException("Erro interno");
        } else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new VerifierUnavailableException("Falha ao se comunicar com o verificador");
            }
        }
    }
}
