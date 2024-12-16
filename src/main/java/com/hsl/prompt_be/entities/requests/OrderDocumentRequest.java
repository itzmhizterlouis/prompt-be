package com.hsl.prompt_be.entities.requests;

import lombok.Data;

@Data
public class OrderDocumentRequest {

    private String name;
    private String uri;
    private int copies;
    private int pages;
    private boolean coloured;
}
