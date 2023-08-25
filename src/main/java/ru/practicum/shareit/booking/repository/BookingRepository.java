package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    //past
    List<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime current);

    //all
    List<Booking> findByBooker_idOrderByStartDesc(Long bookerId);

    //future
    List<Booking> findByBooker_idAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime current);

    //current
    List<Booking> findByBooker_idAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime current,
                                                                               LocalDateTime current2);

    //waiting, rejected
    List<Booking> findByBooker_idAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    //itemAvailableValidation
    List<Booking> findByItem_id(Long itemId);

    List<Booking> findByItem_idAndItem_ownerAndStartIsAfterAndStatus(Long itemId, Long userId, LocalDateTime start,
                                                                     BookingStatus bookingStatus);

    List<Booking> findByItem_idAndItem_ownerAndStatusAndStartIsBefore(Long itemId, Long userId, BookingStatus status,
                                                                      LocalDateTime start);

    //allOwnerBookings
    List<Booking> findByItem_ownerOrderByStartDesc(Long ownerId);

    //pastOwner
    List<Booking> findByItem_ownerAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime current);

    //futureOwner
    List<Booking> findByItem_ownerAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime current);

    //currentOwner
    List<Booking> findByItem_ownerAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime current,
                                                                                LocalDateTime current2);

    //waiting, rejected owner
    List<Booking> findByItem_ownerAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    //checkUserTakeItemToCommit
    List<Booking> findByItem_idAndBooker_idAndStatusAndStartIsBefore(Long itemId, Long userId, BookingStatus bookingStatus,
                                                                     LocalDateTime start);
}
