package service.impl;

import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.methods.response.AppSendTransaction;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.tx.response.PollingTransactionReceiptProcessor;
import com.pojo.util.Account;
import config.CITAConfig;
import lombok.extern.slf4j.Slf4j;
import service.*;
import com.pojo.util.CITAUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class BillManagementServiceImpl implements BillManagementService {
    @Override
    public Map<String, String> callContractSetBill(CITAConfig config, String contractAddress, Map<String, String> bill) {
        CITAj service = config.service;
        Account adminAccount = new Account(config.adminPrivateKey, service);
        PollingTransactionReceiptProcessor txProcessor = config.txProcessor;

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

    @Override
    public Map<String, String> callContractGetBill(CITAConfig config, String contractAddress, BigInteger billId) {
        CITAj service = config.service;
        Account adminAccount = new Account(config.adminPrivateKey, service);
        PollingTransactionReceiptProcessor txProcessor = config.txProcessor;

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

        }catch (Exception e){
            log.error("Exception occurred while calling callContractGetBill: {}", e.getMessage(), e);
        }

        // 返回调用合约结果
        Map<String, String> result = new HashMap<>();
        return result;
    }

}
