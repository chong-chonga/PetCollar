package com.example.service.impl;

import com.example.request.RequestInfoFormat;

/**
 * @author Lexin Huang
 */
public class TooLongIntroductionException extends RequestInfoFormat.FormatException {

    public TooLongIntroductionException(String msg) {
        super(msg);
    }
}
