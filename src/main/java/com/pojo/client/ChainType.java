package com.pojo.client;

public enum ChainType {
    CITA(0),        // CITA链
    CHAINMAKER(1);  // ChainMaker链

    private final int value;

    // 枚举的构造函数
    ChainType(int value) {
        this.value = value;
    }

    // 获取枚举对应的数值
    public int getValue() {
        return value;
    }
}
