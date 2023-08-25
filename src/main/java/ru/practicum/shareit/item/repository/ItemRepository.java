package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select new ru.practicum.shareit.item.dto.ItemDto(it.id, it.name, it.description, it.available)" +
            "from Item as it " +
            "where (lower(it.name) like %?1% or lower(it.description) like %?1%) and it.available = true")
    List<ItemDto> findByQueryIgnoreCase(String emailSearch);

    List<Item> findByOwner(Long userId);
}
