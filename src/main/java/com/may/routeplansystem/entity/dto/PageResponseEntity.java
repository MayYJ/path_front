package com.may.routeplansystem.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseEntity<T> {
    private T object;
    private long total;
    private int status;
    private String msg;
}
