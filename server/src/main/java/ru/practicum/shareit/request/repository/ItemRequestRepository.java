package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequester_idOrderByCreatedDesc(Long requesterId);

    /*@Query(value = "select ir " +
            "from ItemRequest as ir " +
            "where ir.requester_id != ?1")*/
    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requester.id != ?1")
    List<ItemRequest> findNotByRequester_Id(Long requesterId, Pageable pageable);
}
