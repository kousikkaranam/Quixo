package org.quixo.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SharedFileRepository extends MongoRepository<SharedFile, String> {
    SharedFile findByCode(String code);
}
