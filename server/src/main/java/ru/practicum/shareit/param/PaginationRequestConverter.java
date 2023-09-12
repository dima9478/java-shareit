package ru.practicum.shareit.param;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PaginationRequestConverter {
    public static Pageable toPageable(PaginationRequest pagRequest, Sort sort) {
        return PageRequest.of(pagRequest.getFrom() / pagRequest.getSize(), pagRequest.getSize(), sort);
    }
}
