import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.tx.response.PollingTransactionReceiptProcessor;
import config.CITAConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import service.Account;
import service.BillManagementService;
import service.BusinessManagementService;
import service.impl.BillManagementServiceImpl;
import service.impl.BusinessManagementServiceImpl;
import util.CITAUtil;
import util.ContractUtil;
import util.StringUtil;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static util.ContractUtil.deployContract;

@Slf4j
public class ContractUtilTest {

    private static CITAConfig config;
    private static CITAj service;
    private static Account adminAccount;
    private static PollingTransactionReceiptProcessor txProcessor;

    static {
        config = CITAConfig.getInstance();
        service = config.service;
        adminAccount = new Account(config.adminPrivateKey, service);
        txProcessor = config.txProcessor;
    }

    @Test
    void TestDeployBillManagementContract() throws Exception {
        String contractPath = config.billManagementContractSolidity;
        System.out.println(contractPath);
        File contractFile = new File(contractPath);
        String nonce = CITAUtil.getNonce();
        long quota = Long.parseLong(config.defaultQuotaDeployment);
        BigInteger chainId = config.chainId;
        int version = config.version;
        String value = "0";
        String contractAddress = deployContract(contractFile, nonce, quota, version, chainId, value);
        config.update("BillManagementContractAddress", contractAddress);
        log.info("[TestDeployContract]contractPath: {}", contractPath);
        log.info("[TestDeployContract]contractAddress: {}", contractAddress);

        ContractUtil.storeAbiToBlockchain(contractAddress, contractPath);
        log.info("[TestDeployContract]:storeAbiTpBlockChain");
    }

    @Test
    void TestCallContractSetBill(){
        String contractAddress = config.billManagementContractAddress;
        int count = 1;
        for(int i = 0; i < count; i ++){
            // 创建账单
            String description = StringUtil.getRandomString(20);
            String sender = StringUtil.getRandomString(4);
            String receiver = StringUtil.getRandomString(4);
            BigInteger amount = BigInteger.valueOf(1000);

            Map<String, String> bill = new HashMap<>();
            bill.put("description", description);
            bill.put("sender", sender);
            bill.put("receiver", receiver);
            bill.put("amount", String.valueOf(amount));

            BillManagementService billManagementService = new BillManagementServiceImpl();
            billManagementService.callContractSetBill(contractAddress, bill);
        }
    }

    @Test
    void TestCallContractGetBill(){
        String contractAddress = config.billManagementContractAddress;
        System.out.println(contractAddress);
        BigInteger billId = BigInteger.valueOf(1);
        BillManagementService billManagementService = new BillManagementServiceImpl();
        billManagementService.callContractGetBill(contractAddress, billId);
    }

    @Test
    void TestDeployBusinessContract() throws Exception {
        String contractPath = config.businessManagementContractSolidity;
        File contractFile = new File(contractPath);
        String nonce = CITAUtil.getNonce();
        long quota = Long.parseLong(config.defaultQuotaDeployment);
        BigInteger chainId = config.chainId;
        int version = config.version;
        String value = "0";

        String contractAddress = deployContract(contractFile, nonce, quota, version, chainId, value);
        config.update("BusinessManagementContractAddress", contractAddress);
        log.info("[TestDeployBusinessContract]contractPath: {}", contractPath);
        log.info("[TestDeployBusinessContract]contractAddress: {}", contractAddress);

        ContractUtil.storeAbiToBlockchain(contractAddress, contractPath);
        log.info("[TestDeployBusinessContract]:storeAbiTpBlockChain");
    }

    @Test
    void TestEvidenceContract(){
        String contractAddress = config.businessManagementContractAddress;
        String uuId = "1";
        String businessType = "AAAAAA";
        String businessId = "123456";
        String businessTime = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));;
        String businessData = "A tx B";

        Map<String, String> business = new HashMap<>();
        business.put("uuId", uuId);
        business.put("businessType", businessType);
        business.put("businessId", businessId);
        business.put("businessTime", businessTime);
        business.put("businessData", businessData);

        log.info("[TestEvidenceContract] createEvidence");
        BusinessManagementService businessManagementService = new BusinessManagementServiceImpl();
        businessManagementService.createEvidence(contractAddress, business);

        log.info("[TestEvidenceContract] getEvidence");
        String queryId = "1";
        log.info("queryId:" + queryId);
        businessManagementService.getEvidence(contractAddress, queryId);

    }
}
