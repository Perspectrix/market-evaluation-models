package com.perspectrix.market.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class MapRequest {
    @NotNull
    @NotEmpty
    private List<Coordinate> coordinates;

}
