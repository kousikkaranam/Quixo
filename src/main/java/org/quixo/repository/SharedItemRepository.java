package org.quixo.repository;

import org.quixo.model.SharedItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedItemRepository extends JpaRepository<SharedItem, Long> {
    SharedItem findByCode(String code);
}
