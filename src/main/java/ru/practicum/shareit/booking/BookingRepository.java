package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph("Booking.eager")
    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    @EntityGraph("Booking.eager")
    List<Booking> findByBookerIdAndStatusIn(Long bookerId, Set<BookingStatus> status, Sort sort);

    @EntityGraph("Booking.eager")
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    boolean existsByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime currentTime);

    @EntityGraph("Booking.eager")
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime currentTime, Sort sort);

    @Query("select b from Booking b join fetch b.item join fetch b.booker " +
            "where b.end > ?2 and b.start < ?2 " +
            "and b.booker.id = ?1 order by b.start desc")
    List<Booking> findByBookerIdCurrent(Long bookerId, LocalDateTime currentTime);

    @EntityGraph("Booking.eager")
    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime currentTime, Sort sort);

    @EntityGraph("Booking.eager")
    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    @EntityGraph("Booking.eager")
    List<Booking> findByItemOwnerIdAndStatusIn(Long ownerId, Set<BookingStatus> status, Sort sort);

    @EntityGraph("Booking.eager")
    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    @EntityGraph("Booking.eager")
    List<Booking> findByItemOwnerIdAndEndIsBefore(Long bookerId, LocalDateTime currentTime, Sort sort);

    @Query("select b from Booking b join fetch b.item join fetch b.booker " +
            "where b.end > ?2 and b.start < ?2 " +
            "and b.item.owner.id = ?1 order by b.start")
    List<Booking> findByItemOwnerIdCurrent(Long bookerId, LocalDateTime currentTime);

    @EntityGraph("Booking.eager")
    List<Booking> findByItemOwnerIdAndStartIsAfter(Long bookerId, LocalDateTime currentTime, Sort sort);

    @Query("select b from Booking b join b.item as i join b.booker " +
            " where b.start < ?2 and i.id = ?1 and b.status != 'REJECTED' " +
            " order by b.start desc")
    List<Booking> findLastBookingsOfItem(Long itemId, LocalDateTime currentTime, Pageable pageable);

    @Query("select b from Booking b join b.item as i join b.booker " +
            " where b.start > ?2 and i.id = ?1 and b.status != 'REJECTED' " +
            " order by b.start")
    List<Booking> findNextBookingsOfItem(Long itemId, LocalDateTime currentTime, Pageable pageable);

    @Query("select b from Booking b join fetch b.item i join fetch b.booker " +
            " where i.id in ?1 " +
            "   and b.start = (select MAX(b2.start) from Booking b2 where b2.item = b.item" +
            "   and b2.status != 'REJECTED' and b2.start < ?2)"
    )
    List<Booking> findLastBookingOfItems(List<Long> itemIds, LocalDateTime currentTime);

    @Query("select b from Booking b join fetch b.item i join fetch b.booker " +
            " where  i.id in ?1 " +
            "  and b.start = (select MIN(b2.start) from Booking b2 where b2.item = b.item " +
            "   and b2.start > ?2 and b2.status != 'REJECTED')"
    )
    List<Booking> findNextBookingOfItems(List<Long> itemIds, LocalDateTime currentTime);
}
