package com.zyq.apt.constant;

import com.squareup.javapoet.TypeName;

import java.math.BigDecimal;

/**
 * @author YiQing
 */
public enum ConvertEnum {

    /**
     * 期望的类型
     */
    INTEGER,
    DOUBLE,
    FLOAT,
    SHORT,
    LONG,
    BYTE,
    BOOLEAN,
    STRING,
    BIGDECIMAL,
    DEFAULT,
    ;

    public static TypeName convertType(ConvertEnum type) {
        if (type.equals(ConvertEnum.LONG)) {
            return TypeName.LONG.box();
        } else if (type.equals(ConvertEnum.SHORT)) {
            return TypeName.SHORT.box();
        } else if (type.equals(ConvertEnum.INTEGER)) {
            return TypeName.INT.box();
        } else if (type.equals(ConvertEnum.FLOAT)) {
            return TypeName.FLOAT.box();
        } else if (type.equals(ConvertEnum.DOUBLE)) {
            return TypeName.DOUBLE.box();
        } else if (type.equals(ConvertEnum.BYTE)) {
            return TypeName.BYTE.box();
        } else if (type.equals(ConvertEnum.BOOLEAN)) {
            return TypeName.BOOLEAN.box();
        } else if (type.equals(ConvertEnum.STRING)) {
            return TypeName.get(String.class);
        } else if (type.equals(ConvertEnum.BIGDECIMAL)) {
            return TypeName.get(BigDecimal.class);
        } else {
            return TypeName.OBJECT;
        }
    }
}
