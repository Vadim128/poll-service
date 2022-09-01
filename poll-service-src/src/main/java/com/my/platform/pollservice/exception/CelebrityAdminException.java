package com.my.platform.pollservice.exception;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class CelebrityAdminException extends RestException {

    public CelebrityAdminException(@NonNull UUID keycloakUserId, @NonNull UUID celebrityId) {
        super("User with keycloakUserId=" + keycloakUserId + " is not admin of celebrity = " + celebrityId, HttpStatus.UNAUTHORIZED);
    }
}
