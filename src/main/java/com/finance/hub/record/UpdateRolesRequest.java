package com.finance.hub.record;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UpdateRolesRequest (@NotEmpty List<String> roles){
}
