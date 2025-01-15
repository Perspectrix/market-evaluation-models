package com.perspectrix.market.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class Coordinate {
    @NotNull
    private Double lat;

    @NotNull
    private Double lng;

}
