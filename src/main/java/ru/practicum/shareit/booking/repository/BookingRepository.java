package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    //past
    Page<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime current, Pageable pageable);

    //all
    Page<Booking> findByBooker_idOrderByStartDesc(Long bookerId, Pageable pageable);

    //future
    Page<Booking> findByBooker_idAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime current, Pageable pageable);

    //current
    Page<Booking> findByBooker_idAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end,
                                                                               LocalDateTime start, Pageable pageable);

    //waiting, rejected
    Page<Booking> findByBooker_idAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    //itemAvailableValidation
    List<Booking> findByItem_id(Long itemId);

    List<Booking> findByItem_idAndItem_owner_idAndStartIsAfterAndStatus(Long itemId, Long userId, LocalDateTime start,
                                                                        BookingStatus bookingStatus);

    List<Booking> findByItem_idAndItem_owner_idAndStatusAndStartIsBefore(Long itemId, Long userId, BookingStatus status,
                                                                         LocalDateTime start);

    //allOwnerBookings
    Page<Booking> findByItem_owner_idOrderByStartDesc(Long ownerId, Pageable pageable);

    //pastOwner
    Page<Booking> findByItem_owner_idAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime current, Pageable pageable);

    //futureOwner
    Page<Booking> findByItem_owner_idAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime current, Pageable pageable);

    //currentOwner
    Page<Booking> findByItem_owner_idAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime current,
                                                                                   LocalDateTime current2, Pageable pageable);

    //waiting, rejected owner
    Page<Booking> findByItem_owner_idAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    //checkUserTakeItemToCommit
    Booking findFirstByItem_idAndBooker_idAndStatusAndStartIsBefore(Long itemId, Long userId, BookingStatus bookingStatus,
                                                                    LocalDateTime start);
}
