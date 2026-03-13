package com.finance.hub.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateNameRequest (
        @NotBlank
        @Size
                (min = 2, max = 50) String name) {


}
