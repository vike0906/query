package com.vike.query.common;

/**
 * @author: lsl
 * @createDate: 2019/11/6
 */
public class QueryException extends RuntimeException {

    private QueryException(String message){
        super(message);
    }

    public static QueryException fail(String message){
        return new QueryException(message);
    }
}
