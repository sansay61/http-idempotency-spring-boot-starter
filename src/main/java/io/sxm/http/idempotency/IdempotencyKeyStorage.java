package io.sxm.http.idempotency;

import java.io.Serializable;

public interface IdempotencyKeyStorage {

    boolean existsByKey(Serializable idempotencyKey);

    void save(Serializable idempotencyKey);
}
