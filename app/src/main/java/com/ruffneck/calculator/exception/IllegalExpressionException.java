package com.ruffneck.calculator.exception;

/**
 * Created by 佛剑分说 on 2015/11/5.
 */
public class IllegalExpressionException extends RuntimeException {

    public IllegalExpressionException(String msg){
        super(msg);
    }

    public IllegalExpressionException(){
        super("错误");
    }

}
