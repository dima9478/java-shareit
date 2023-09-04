package ru.practicum.shareit.param;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
public class PaginationRequest {
    @Min(0)
    private int from;
    @Min(1)
    private int size;
}
