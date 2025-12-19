// WalletDto.java
package com.local.train.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDto {
    private Long userId;
    private Double balance;
    private String userName;
    private String email;
}