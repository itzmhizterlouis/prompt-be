package com.hsl.prompt_be.entities.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDetails {

    private String email;
    private String name;
}
