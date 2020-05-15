package com.may.routeplansystem.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tupple<T, R> {
    private T t;
    private R r;
}
