package org.quixo.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SharedTextRepository extends MongoRepository<SharedText, String> {
    SharedText findByCode(String code);
}
