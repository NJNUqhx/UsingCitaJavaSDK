package com.pojo.state;

import com.pojo.client.ChainType;

import java.math.BigInteger;


public class TransactionState {
    ChainType type;
    BigInteger chainId;
    BigInteger timestamp;
    String blockHash;
    BigInteger blockHeight;
    String content;
    String from;
    String hash;

    public TransactionState(ChainType type, BigInteger chainId, BigInteger timestamp, String blockHash, BigInteger blockHeight, String content, String from, String hash) {
        this.type = type;
        this.chainId = chainId;
        this.timestamp = timestamp;
        this.blockHash = blockHash;
        this.blockHeight = blockHeight;
        this.content = content;
        this.from = from;
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "TransactionState{" +
                "type=" + type.name() +
                ", chainId=" + chainId +
                ", timestamp=" + timestamp +
                ", blockHash='" + blockHash + '\'' +
                ", blockHeight=" + blockHeight +
                ", content='" + content + '\'' +
                ", from='" + from + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }
}
