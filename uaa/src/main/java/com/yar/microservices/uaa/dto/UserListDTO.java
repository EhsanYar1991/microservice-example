package com.yar.microservices.uaa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserListDTO extends GeneralResponse {

    private List<UserDTO> userDTOS;

}
