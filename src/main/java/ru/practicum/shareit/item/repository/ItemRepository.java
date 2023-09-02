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

    @Query(value = "select it " +
            "from Item as it " +
            "where (lower(it.name) like %?1% or lower(it.description) like %?1%) and it.available = true ")
    Page<Item> findByQueryIgnoreCase(String querySearch, Pageable pageable);

    Page<Item> findByOwner(Long userId, Pageable pageable);

    List<Item> findByRequestId(Long requestId);
}
