package service;

import java.math.BigInteger;
import java.util.Map;

public interface BillManagementService {

    public  Map<String, String> callContractSetBill(String contractAddress, Map<String,String> bill);
    public Map<String, String> callContractGetBill(String contractAddress, BigInteger billId);

}
