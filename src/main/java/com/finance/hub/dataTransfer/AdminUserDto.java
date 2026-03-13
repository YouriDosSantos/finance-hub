package com.finance.hub.dataTransfer;

import java.util.List;

public record AdminUserDto (Long id,
                            String name,
                            String email,
                            List<String> roles){

}
