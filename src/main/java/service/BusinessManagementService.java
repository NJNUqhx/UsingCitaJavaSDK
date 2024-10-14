package service;

import java.math.BigInteger;
import java.util.Map;

public interface BusinessManagementService {
    public Map<String, String> createEvidence(String contractAddress, Map<String,String> business);
    public Map<String, String> getEvidence(String contractAddress, String uuId);
}
