package com.videogenerator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationRequest {

    private int page = 0;
    private int size = 10;
    private String sort = "id";
    private String direction = "DESC";

    public int getPage() {
        return Math.max(page, 0);
    }

    public int getSize() {
        return Math.min(Math.max(size, 1), 100);
    }
}
