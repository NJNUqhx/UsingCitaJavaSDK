package com.pojo.client;

/**
 * 链客户端基类
 */
public class ChainClientBase {
    public String configFile;
    public ChainType chainType;
    public String chainId;

    public ChainClientBase(String _configFile, ChainType _chainType,String _chainId) {
        configFile = _configFile;
        chainType = _chainType;
        chainId = _chainId;
    }

    public String getConfigFile() {
        return configFile;
    }

    public ChainType getChainType() {
        return chainType;
    }

    public String getChainId(){return chainId;}
}
