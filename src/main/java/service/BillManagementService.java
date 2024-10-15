package service;

import config.CITAConfig;

import java.math.BigInteger;
import java.util.Map;

public interface BillManagementService {

    Map<String, String> callContractSetBill(String contractAddress, Map<String,String> bill);
    Map<String, String> callContractGetBill(String contractAddress, BigInteger billId);

    // 加入 CITAConfig 参数
    Map<String, String> callContractSetBill(CITAConfig config, String contractAddress, Map<String,String> bill);
    Map<String, String> callContractGetBill(CITAConfig config, String contractAddress, BigInteger billId);

}
