package ru.practicum.shareit.param;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginationRequest {
    private int from;
    private int size;
}
