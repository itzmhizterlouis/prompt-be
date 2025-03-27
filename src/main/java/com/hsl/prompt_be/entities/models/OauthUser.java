package com.hsl.prompt_be.entities.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "auth_users")
public class OauthUser {

    @Id
    @Builder.Default
    private UUID oauthUserId = UUID.randomUUID();
    private String email;
}
