package com.hsl.prompt_be.entities.requests;

import lombok.Data;

import java.util.Map;

@Data
public class SearchOrderRequest {

    Map<String, String> specifications;
}
