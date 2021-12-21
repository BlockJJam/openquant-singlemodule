package com.tys.openquant.util.builder;

import org.springframework.util.StringUtils;

public class StringAppendBuilder {
    public static String getStrMultipliedByLength(String inputStr, int inputSize){
        StringBuilder sb = new StringBuilder("");
        for(int i=0; i<inputSize; i++){
            sb.append(inputStr);
        }

        return sb.toString();
    }
}
