package com.pojo.state;

import com.pojo.client.ChainType;

import java.math.BigInteger;
import java.util.List;


public class BlockState {
    ChainType type;
    BigInteger chainId;
    String preHash;
    BigInteger height;
    String hash;
    BigInteger timestamp;
    BigInteger number;
    List<TransactionState> transactions;

    public BlockState(ChainType type, BigInteger chainId, String preHash, BigInteger height, String hash, BigInteger timestamp, BigInteger number, List<TransactionState> transactions) {
        this.type = type;
        this.chainId = chainId;
        this.preHash = preHash;
        this.height = height;
        this.hash = hash;
        this.timestamp = timestamp;
        this.number = number;
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "BlockState{" +
                "type=" + type.name() +
                ", chainId=" + chainId +
                ", preHash='" + preHash + '\'' +
                ", height=" + height +
                ", hash='" + hash + '\'' +
                ", timestamp=" + timestamp +
                ", number=" + number +
                ", transactions=" + transactions +
                '}';
    }

    public List<TransactionState> getTransactions() {
        return transactions;
    }
}
