package com.crewmeister.cmcodingchallenge.currency.exception;

public class ConversionRateNotFoundException extends RuntimeException  {

    public ConversionRateNotFoundException(String message){
        super(message);
    }
}
