package com.hsl.prompt_be.entities.models;

public enum OrderStatus {

    PENDING, // Pending when not tended to yet
    CANCELLED, // Cancelled when rejected by printer or admin
    COMPLETED, // Complete when work is finished
    INVALID // When work goes over the 24 hour mark
}
