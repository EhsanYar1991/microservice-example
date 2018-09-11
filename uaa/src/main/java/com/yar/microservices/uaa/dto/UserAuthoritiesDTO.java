package com.yar.microservices.uaa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserAuthoritiesDTO extends GeneralResponse {

    private Set<String> authorityDTOs;

}
