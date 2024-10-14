package service.impl;

import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.methods.response.AppSendTransaction;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.tx.response.PollingTransactionReceiptProcessor;
import config.CITAConfig;
import lombok.extern.slf4j.Slf4j;
import service.Account;
import service.BusinessManagementService;
import util.CITAUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BusinessManagementServiceImpl implements BusinessManagementService {
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

    @Override
    public Map<String, String> createEvidence(String contractAddress, Map<String, String> business){
        String nonce = CITAUtil.getNonce();
        long quota = Long.parseLong(config.defaultQuotaDeployment);
        BigInteger chainId = config.chainId;
        int version = config.version;
        String value = "0";

        String uuId = business.get("uuId");
        String businessType = business.get("businessType");
        String businessId = business.get("businessId");
        String businessTime = business.get("businessTime");
        String businessData = business.get("businessData");

        try{
            log.info("调用创建存证合约方法");
            log.info("contractAddress: " + contractAddress);
            // 调用创建账单合约方法
            Object object = adminAccount.callContract(contractAddress, "createBusiness", nonce, quota, version, chainId, value, uuId, businessType, businessId, businessTime, businessData);

            AppSendTransaction transaction = (AppSendTransaction) object;

            // 获取本次调用合约的交易哈希 callHash
            String callHash = transaction.getSendTransactionResult().getHash();
            log.info("交易信息哈希: " + callHash);

            // 根据交易哈希，查询交易回执
            TransactionReceipt receipt = txProcessor.waitForTransactionReceipt(callHash);
            log.info("回执块哈希: " + receipt.getBlockHash());
        }catch(Exception e){
            log.error("Exception occurred while calling createEvidence: {}", e.getMessage(), e);
        }


        // 返回调用合约结果
        Map<String, String> result = new HashMap<>();
        return result;
    }

    @Override
    public Map<String, String> getEvidence(String contractAddress, String uuId) {
        String nonce = CITAUtil.getNonce();
        long quota = Long.parseLong(config.defaultQuotaDeployment);
        BigInteger chainId = config.chainId;
        int version = config.version;
        String value = "0";

        try{
            log.info("调用查询存证合约方法");
            // 调用查询账单合约方法
            Object object = adminAccount.callContract(contractAddress, "getBusiness", nonce, quota, version, chainId, value, uuId);
            log.info("调用查询存证合约方法-返回类型:" + object.getClass());
            ArrayList<BigInteger> txResult = (ArrayList<BigInteger>) object;
            for (int i = 0; i < txResult.size(); i++) {
                System.out.println(txResult.get(i));
            }
        }catch (Exception e){
            log.error("Exception occurred while calling getEvidence: {}", e.getMessage(), e);
        }

        // 返回调用合约结果
        Map<String, String> result = new HashMap<>();
        return result;
    }
}
