package com.example.ebearrestapi.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WishListPageDto {
    private List<WishListResultDto> content;
    private boolean hasNext;
    private Long nextCursor;
}
