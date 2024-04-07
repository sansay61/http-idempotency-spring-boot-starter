package io.sxm.http.idempotency;

import java.io.Serializable;

public class DefaultIdempotencyHeaderStorageService implements IdempotencyHeaderStorageService {
    private final IdempotencyKeyStorage keyStorage;

    public DefaultIdempotencyHeaderStorageService(IdempotencyKeyStorage keyStorage) {
        this.keyStorage = keyStorage;
    }

    @Override
    public void checkIfNotExist(Serializable idempotencyKey) throws IllegalArgumentException {
        boolean isExist = keyStorage.existsByKey(idempotencyKey);
        if (isExist) {
            throw new IllegalArgumentException("Idempotency key %s already presented in the database".formatted(idempotencyKey.toString()));
        }
    }

    @Override
    public void storeIdempotencyKey(Serializable idempotencyKey) {
        keyStorage.save(idempotencyKey);
    }
}
