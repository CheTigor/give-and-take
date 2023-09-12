package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT it " +
            "FROM Item AS it " +
            "WHERE (LOWER(it.name) LIKE %?1% OR LOWER(it.description) LIKE %?1%) and it.available = true")
    Page<Item> findByQueryIgnoreCase(String querySearch, Pageable pageable);

    Page<Item> findByOwner_idOrderByIdAsc(Long userId, Pageable pageable);

    List<Item> findByRequest_id(Long requestId);
}
