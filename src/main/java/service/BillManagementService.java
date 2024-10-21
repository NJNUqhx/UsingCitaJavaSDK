package service;

import config.CITAConfig;

import java.math.BigInteger;
import java.util.Map;

public interface BillManagementService {
    // 加入 CITAConfig 参数
    Map<String, String> callContractSetBill(CITAConfig config, String contractAddress, Map<String,String> bill);
    Map<String, String> callContractGetBill(CITAConfig config, String contractAddress, BigInteger billId);

}
