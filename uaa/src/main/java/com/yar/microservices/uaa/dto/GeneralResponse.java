package com.yar.microservices.uaa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by EhsanYar on 8/6/2017.
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class GeneralResponse implements Serializable {

    private static final long serialVersionUID = -2724934676696597738L;

    private String actionCode;
    private String actionMessage;


}
