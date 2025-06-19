package com.tablelog.tablelogback.domain.shopping_list.repository;

import com.tablelog.tablelogback.domain.shopping_list.entity.ShoppingList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
    Optional<ShoppingList> findByIdAndUserId(Long id, Long userId);
    Slice<ShoppingList> findAllByUserId(Long id, Pageable pageable);
    Long countAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
}
