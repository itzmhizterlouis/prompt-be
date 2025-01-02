package com.hsl.prompt_be.entities.requests;

import com.hsl.prompt_be.entities.models.OpenStatus;
import lombok.Data;

@Data
public class UpdateOpenStatusRequest {

    private OpenStatus open;
}
