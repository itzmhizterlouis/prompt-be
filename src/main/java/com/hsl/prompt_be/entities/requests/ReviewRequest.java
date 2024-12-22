package com.hsl.prompt_be.entities.requests;

import lombok.Data;

@Data
public class ReviewRequest {

    private int rating;
    private String comment;
}
