package com.may.routeplansystem.util;


public class BiTupple<T, U> {
    T t;
    U u;

    public BiTupple(T t, U u) {
        this.t = t;
        this.u = u;
    }

    public BiTupple(){}

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public U getU() {
        return u;
    }

    public void setU(U u) {
        this.u = u;
    }


}
