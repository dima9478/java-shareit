package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ObjectUnavailableException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.param.PaginationRequest;
import ru.practicum.shareit.param.PaginationRequestConverter;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto addBooking(@Valid BookingCreateDto bookingDto, long userId) {
        validate(bookingDto);
        User user = getBookingUser(userId);
        Item item = getBookingItem(bookingDto.getItemId());
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Not found users that don't own item");
        }

        return BookingMapper.toDto(bookingRepository.save(BookingMapper.createDtoToBooking(bookingDto, user, item)));
    }

    @Transactional
    @Override
    public BookingDto finalizeBookingStatus(long bookingId, long userId, boolean approved) {
        Booking booking = getBooking(bookingId);
        getBookingUser(userId);

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new IllegalArgumentException("Status was already considered");
        }
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("No booking for owner with user id " + userId);
        }
        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(status);
        bookingRepository.save(booking);

        return BookingMapper.toDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        Booking booking = getBooking(bookingId);
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("No available bookings for user " + userId);
        }

        return BookingMapper.toDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getBookingsByState(long userId, String state, @Valid PaginationRequest pagRequest) {
        BookingFilterState filterState = getFilterState(state);
        getBookingUser(userId);
        List<Booking> bookings;
        Pageable pageable = PaginationRequestConverter.toPageable(pagRequest, Sort.by(Sort.Direction.DESC, "start"));
        LocalDateTime currentTime = LocalDateTime.now();

        switch (filterState) {
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdCurrent(userId, currentTime, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, currentTime, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, currentTime, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId,
                        BookingStatus.WAITING,
                        pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusIn(userId,
                        Set.of(BookingStatus.REJECTED, BookingStatus.CANCELLED),
                        pageable);
                break;
            default:
                bookings = Collections.emptyList();
        }
        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getOwnerBookingsByState(long userId, String state, @Valid PaginationRequest pagRequest) {
        BookingFilterState filterState = getFilterState(state);
        getBookingUser(userId);
        List<Booking> bookings;
        Pageable pageable = PaginationRequestConverter.toPageable(pagRequest, Sort.by(Sort.Direction.DESC, "start"));
        LocalDateTime currentTime = LocalDateTime.now();

        switch (filterState) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerId(userId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdCurrent(userId, currentTime, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, currentTime, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, currentTime, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId,
                        BookingStatus.WAITING,
                        pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatusIn(userId,
                        Set.of(BookingStatus.CANCELLED, BookingStatus.REJECTED),
                        pageable);
                break;
            default:
                bookings = Collections.emptyList();
        }
        return bookings.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }


    private User getBookingUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User doesn't exist"));
    }

    private Item getBookingItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item doesn't exist"));
        if (!item.isAvailable()) {
            throw new ObjectUnavailableException("Item not available for booking");
        }
        return item;
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking doesn't exist"));
    }

    private void validate(BookingCreateDto dto) {
        if (dto.getEnd().isBefore(dto.getStart()) || dto.getEnd().isEqual(dto.getStart())) {
            throw new ValidationException("end time must be after start time");
        }
    }

    private BookingFilterState getFilterState(String state) {
        try {
            return BookingFilterState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }
}
