package com.hsl.prompt_be.entities.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KorapayBank {

    private String bank;
    private String account;
}
