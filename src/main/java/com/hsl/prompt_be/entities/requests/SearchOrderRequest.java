package com.hsl.prompt_be.entities.requests;

import lombok.Data;

import java.util.List;

@Data
public class SearchOrderRequest {

    List<SpecificationRequest> specifications;
}
