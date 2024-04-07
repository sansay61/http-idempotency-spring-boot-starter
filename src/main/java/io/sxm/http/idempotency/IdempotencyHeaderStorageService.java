package io.sxm.http.idempotency;

import java.io.Serializable;

public interface IdempotencyHeaderStorageService {

    void checkIfNotExist(Serializable idempotencyKey) throws IllegalArgumentException;

    void storeIdempotencyKey(Serializable idempotencyKey);
}
