package com.imooc.enums;

public enum VideoOperEnum {

    ACTIVE(1 ),
    FORBIDDEN (2);

    public final Integer type;

    VideoOperEnum (Integer type) {
        this.type = type;
    }


}
