package service.impl;

import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.methods.response.AppSendTransaction;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.protocol.exceptions.TransactionException;
import com.citahub.cita.tx.response.PollingTransactionReceiptProcessor;
import config.CITAConfig;
import lombok.extern.slf4j.Slf4j;
import service.*;
import util.CITAUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class BillManagementServiceImpl implements BillManagementService {
    private static CITAConfig config;
    private static CITAj service;
    private static Account adminAccount;
    private static PollingTransactionReceiptProcessor txProcessor;

    static {
        config = CITAConfig.getInstance();
        service = config.service;
        txProcessor = config.txProcessor;
        adminAccount = new Account(config.adminPrivateKey, service);
    }
    /**
     * 调用创建账单合约
     * @param contractAddress
     * @param bill
     * @return
     */
    @Override
    public Map<String, String> callContractSetBill(String contractAddress, Map<String,String> bill){
        String nonce = CITAUtil.getNonce();
        long quota = Long.parseLong(config.defaultQuotaDeployment);
        BigInteger chainId = config.chainId;
        int version = config.version;
        String value = "0";

        // 创建账单参数
        String description = bill.get("description");
        String sender = bill.get("sender");
        String receiver = bill.get("receiver");
        BigInteger amount = new BigInteger(bill.get("amount"));

        try{
            log.info("调用创建账单合约方法");
            log.info("contractAddress: " + contractAddress);
            // 调用创建账单合约方法
            Object object = adminAccount.callContract(contractAddress, "setBill", nonce, quota, version, chainId, value, description, sender, receiver, amount);

            AppSendTransaction transaction = (AppSendTransaction) object;

            // 获取本次调用合约的交易哈希 callHash
            String callHash = transaction.getSendTransactionResult().getHash();
            log.info("交易信息哈希: " + callHash);

            // 根据交易哈希，查询交易回执
            TransactionReceipt receipt = txProcessor.waitForTransactionReceipt(callHash);
            log.info("回执块哈希: " + receipt.getBlockHash());

            // 解析回执
            BaseContractService.extractFromTransactionReceipt(receipt);
        }catch(Exception e){
            log.error("Exception occurred while calling callContractSetBill: {}", e.getMessage(), e);
        }


        // 返回调用合约结果
        Map<String, String> result = new HashMap<>();
        return result;
    }

    /**
     * 调用查询账单合约
     * @param contractAddress
     * @param billId
     * @return
     */
    @Override
    public Map<String, String> callContractGetBill(String contractAddress, BigInteger billId){
        String nonce = CITAUtil.getNonce();
        long quota = Long.parseLong(config.defaultQuotaDeployment);
        BigInteger chainId = config.chainId;
        int version = config.version;
        String value = "0";

        try{
            log.info("调用查询账单合约方法");
            // 调用查询账单合约方法
            Object object = adminAccount.callContract(contractAddress, "getBill", nonce, quota, version, chainId, value, billId);

            log.info("调用查询账单合约-返回类型:" + object.getClass());

            ArrayList<BigInteger> txResult = (ArrayList<BigInteger>) object;
            for (int i = 0; i < txResult.size(); i++) {
                System.out.println(txResult.get(i));
            }

//            AppSendTransaction transaction = (AppSendTransaction) object;
//
//            // 获取本次调用合约的交易哈希 callHash
//            String callHash = transaction.getSendTransactionResult().getHash();
//            log.info("交易信息哈希: " + callHash);
//
//            // 根据交易哈希，查询交易回执
//            TransactionReceipt receipt = txProcessor.waitForTransactionReceipt(callHash);
//            log.info("回执块哈希: " + receipt.getBlockHash());

        }catch (Exception e){
            log.error("Exception occurred while calling callContractGetBill: {}", e.getMessage(), e);
        }

        // 返回调用合约结果
        Map<String, String> result = new HashMap<>();
        return result;
    }

}
