package com.pojo.adapter.impl;

import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameter;
import com.citahub.cita.protocol.core.methods.response.AppBlock;
import com.citahub.cita.protocol.core.methods.response.AppSendTransaction;
import com.citahub.cita.protocol.core.methods.response.Transaction;
import com.pojo.adapter.ChainCrossAdapter;
import com.pojo.client.ChainClientBase;
import com.pojo.client.ChainType;
import com.pojo.client.CitaChainClient;
import com.pojo.state.BlockState;
import com.pojo.state.TransactionState;
import com.pojo.util.*;
import jdk.nashorn.internal.ir.Block;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.pojo.util.CITAUtil.getChainId;
import static com.pojo.util.CITAUtil.hexStringToBigInteger;


@Slf4j
public class CitaChainAdapter implements ChainCrossAdapter {
    @Override
    public ResponseData<ChainClientBase> initClient(String configPath) {
        ChainClientBase clientBase = new CitaChainClient(configPath, "cita");
        CitaChainClient citaChainClient = (CitaChainClient) clientBase;
        if(citaChainClient.isInitialized())
            return new ResponseData<>(Code.SUCCESS, clientBase);
        return new ResponseData<>(Code.FAILURE, null);
    }

    @Override
    public ResponseData<List<Evidence>> getEvidence(ChainClientBase client, String evidenceId) {
        CitaChainClient citaChainClient = (CitaChainClient) client;
        if(!((CitaChainClient) client).isInitialized())
            return new ResponseData<>(Code.FAILURE, null);
        Account account = citaChainClient.getAdminAccount();
        List<Evidence> evidences = new ArrayList<>();
        try{
            Object object = account.callContract(citaChainClient.getEvidenceContractAddress(), "getEvidence", CITAUtil.getNonce(), citaChainClient.defaultQuotaDeployment, citaChainClient.version, citaChainClient.citaChainId, citaChainClient.value, stringToBytes32(evidenceId));
            log.info(String.valueOf(object.getClass()));
            ArrayList<String> list = (ArrayList<String>) object;
            for(int i = 0; i < list.size(); i += 5){
                Evidence evidence = new Evidence(list.get(0), list.get(1), list.get(2), list.get(3), list.get(4));
                evidences.add(evidence);
            }

        }catch(Exception e){

            log.error(e.getMessage());
            return new ResponseData<>(Code.FAILURE, null);
        }

        return new ResponseData<>(Code.SUCCESS, evidences);
    }

    @Override
    public ResponseData<String> createEvidence(ChainClientBase client, Evidence evidence) {
        CitaChainClient citaChainClient = (CitaChainClient) client;
        if(!((CitaChainClient) client).isInitialized())
            return new ResponseData<>(Code.FAILURE, null);
        Account account = citaChainClient.getAdminAccount();
        String txHash;
        try{
            Object object = account.callContract(citaChainClient.getEvidenceContractAddress(), "createEvidence", CITAUtil.getNonce(), citaChainClient.defaultQuotaDeployment, citaChainClient.version, citaChainClient.citaChainId, citaChainClient.value, stringToBytes32(evidence.getUuId()), evidence.getBusinessType(), evidence.getBusinessId(), evidence.getBusinessTime(), evidence.getBusinessData());
            AppSendTransaction transaction = (AppSendTransaction) object;
            txHash = transaction.getSendTransactionResult().getHash();
            log.info("交易信息哈希:" + txHash);
        }catch(Exception e){
            log.error(e.getMessage());
            return new ResponseData<>(Code.FAILURE, null);
        }

        return new ResponseData<>(Code.SUCCESS, txHash);
    }

    @Override
    public ResponseData<Boolean> updateEvidence(ChainClientBase client, Evidence evidence) {
        CitaChainClient citaChainClient = (CitaChainClient) client;
        if (!citaChainClient.isInitialized()) return new ResponseData<>(Code.FAILURE, null);
        return null;
    }

    @Override
    public ResponseData<TransactionState> getTransactionState(ChainClientBase client, String transactionHash) {
        CitaChainClient citaChainClient = (CitaChainClient) client;
        if (!citaChainClient.isInitialized()) return new ResponseData<>(Code.FAILURE, null);
        CITAj service = citaChainClient.getService();
        try{
            Transaction transaction = service.appGetTransactionByHash(transactionHash).send().getTransaction();

            ChainType type = ChainType.CITA;
            BigInteger chainId = getChainId(service);
            String blockHash = transaction.getBlockHash();
            AppBlock.Block block = service.appGetBlockByHash(blockHash,false).send().getBlock();
            BigInteger timestamp = BigInteger.valueOf(block.getHeader().getTimestamp());
            BigInteger blockHeight = transaction.getBlockNumber();
            String content = transaction.getContent();
            String from = transaction.getFrom();
            String hash = transaction.getHash();
            TransactionState transactionState = new TransactionState(type, chainId, timestamp, blockHash, blockHeight, content, from, hash);
            
            return new ResponseData<>(Code.SUCCESS, transactionState);
        }catch(Exception e){
            log.info(e.getMessage());
            return new ResponseData<>(Code.FAILURE, null);
        }
    }

    @Override
    public ResponseData<BigInteger> getChainHeight(ChainClientBase client) {
        CitaChainClient citaChainClient = (CitaChainClient) client;
        if(!((CitaChainClient) client).isInitialized())
            return new ResponseData<>(Code.FAILURE, null);
        
        BigInteger height = CITAUtil.getCurrentHeight(citaChainClient.getService());
        
        return new ResponseData<>(Code.SUCCESS, height);
    }

    @Override
    public ResponseData<BlockState> getBlockStateByHash(ChainClientBase client, String hash) {
        CitaChainClient citaChainClient = (CitaChainClient) client;
        if (!citaChainClient.isInitialized()) return new ResponseData<>(Code.FAILURE, null);
    
        CITAj service = citaChainClient.getService();
        try{
            AppBlock.Block block = service.appGetBlockByHash(hash, true).send().getBlock();
            ChainType type = ChainType.CITA;
            BigInteger chainId = getChainId(service);
            String preHash = block.getHeader().getPrevHash();
            BigInteger height = hexStringToBigInteger(block.getHeader().getNumber());
            BigInteger timestamp = BigInteger.valueOf(block.getHeader().getTimestamp());
            List<AppBlock.TransactionObject> transactions = block.getBody().getTransactions();
            BigInteger number = BigInteger.valueOf(transactions.size());

            List<TransactionState> transactionStates = new ArrayList<>();
            for (AppBlock.TransactionObject transaction : transactions) {
                String txHash = transaction.get().getHash();
                ResponseData<TransactionState> responseData = getTransactionState(client, txHash);
                if (responseData.code == Code.SUCCESS.getCode()) {
                    transactionStates.add(responseData.result);
                }
            }
            BlockState blockState = new BlockState(type, chainId, preHash, height, hash, timestamp, number, transactionStates);

            return new ResponseData<>(Code.SUCCESS, blockState);

        }catch(Exception e){
            log.info(e.getMessage());
            return new ResponseData<>(Code.FAILURE, null);
        }
    }

    @Override
    public ResponseData<BlockState> getBlockStateByHeight(ChainClientBase client, BigInteger height) {
        CitaChainClient citaChainClient = (CitaChainClient) client;

        if (!citaChainClient.isInitialized()) return new ResponseData<>(Code.FAILURE, null);

        CITAj service = citaChainClient.getService();
        try{
            AppBlock.Block block = service.appGetBlockByNumber(DefaultBlockParameter.valueOf("latest"), true).send().getBlock();
            ChainType type = ChainType.CITA;
            BigInteger chainId = getChainId(service);
            String preHash = block.getHeader().getPrevHash();
            String hash = block.getHash();
            BigInteger timestamp = BigInteger.valueOf(block.getHeader().getTimestamp());
            List<AppBlock.TransactionObject> transactions = block.getBody().getTransactions();
            BigInteger number = BigInteger.valueOf(transactions.size());

            List<TransactionState> transactionStates = new ArrayList<>();
            for (AppBlock.TransactionObject transaction : transactions) {
                String txHash = transaction.get().getHash();
                ResponseData<TransactionState> responseData = getTransactionState(client, txHash);
                if (responseData.code == Code.SUCCESS.getCode()) {
                    transactionStates.add(responseData.result);
                }
            }
            BlockState blockState = new BlockState(type, chainId, preHash, height, hash, timestamp, number, transactionStates);

            return new ResponseData<>(Code.SUCCESS, blockState);

        }catch(Exception e){
            log.info(e.getMessage());
            return new ResponseData<>(Code.FAILURE, null);
        }
    }

    private static byte[] stringToBytes32(String str) {
        byte[] byteValue = str.getBytes(StandardCharsets.UTF_8);
        byte[] byteValue32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValue32, 0, byteValue.length);
        return byteValue32;
    }
}
