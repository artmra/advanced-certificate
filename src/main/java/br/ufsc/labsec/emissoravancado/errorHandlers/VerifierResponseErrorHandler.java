package br.ufsc.labsec.emissoravancado.errorHandlers;

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
            // todo: tratar error de maneira mais elegante
            throw new RuntimeException("Erro interno");
        } else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            // todo: tratar error de maneira mais elegante
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                // todo: tratar error de maneira mais elegante
                throw new RuntimeException("Falha ao se comunicar com o verificador");
            }
        }
    }
}
