package br.ufsc.labsec.emissoravancado.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
public class SimpleMessageResponse {
    @Getter private final String message;

    public static SimpleMessageResponse getFormatedMessage(String template) {
        return SimpleMessageResponse.builder().message(template).build();
    }
}
