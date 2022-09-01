package com.my.platform.pollservice.exception;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ItemNotFoundException extends RestException {

    public ItemNotFoundException(@NonNull Class<?> itemClass, @NonNull UUID id) {
        super(itemClass.getSimpleName() + " with Id=" + id + " not found", HttpStatus.NOT_FOUND);
    }
}
