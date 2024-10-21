import com.citahub.cita.protocol.account.CompiledContract;
import config.CITAConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import service.BillManagementService;
import service.BusinessManagementService;
import service.impl.BillManagementServiceImpl;
import service.impl.BusinessManagementServiceImpl;
import com.pojo.util.CITAUtil;
import util.ContractUtil;
import util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class ContractUtilTest {

    private static CITAConfig config;

    private static ContractUtil contractUtil;

    static {
        String configPath = "src/main/resources/cita_school.properties";
        config = new CITAConfig(configPath);
        contractUtil = new ContractUtil(config);
    }
    @Test
    void TestDeployEvidenceContract() throws Exception {
        String contractPath = "src/main/java/com/solidity/contract/Evidence.sol";
        File contractFile = new File(contractPath);
        if(!contractFile.exists()){
            throw new Exception("文件不存在");
        }
        String nonce = CITAUtil.getNonce();
        long quota = Long.parseLong(config.defaultQuotaDeployment);
        BigInteger chainId = config.chainId;
        int version = config.version;
        String value = "0";
        String contractAddress = contractUtil.deployContract(contractFile, nonce, quota, version, chainId, value);
        contractUtil.storeAbiToBlockchain(contractAddress, contractPath);
        System.out.println(contractAddress);
    }
    @Test
    void TestGenerateABIFile() throws CompiledContract.ContractCompileError, IOException, InterruptedException {
        String contractPath = "src/solidity/contracts/Evidence.sol";
        File contractFile = new File(contractPath);
        CompiledContract contract = new CompiledContract(contractFile);
        System.out.println(contract.getAbi());
    }

    @Test
    void TestDeployBillManagementContract() throws Exception {
        String contractPath = config.billManagementContractSolidity;
        //String contractPath = "src/main/java/com/solidity/contract/Evidence.sol";
        System.out.println(contractPath);
        File contractFile = new File(contractPath);
        String nonce = CITAUtil.getNonce();
        long quota = Long.parseLong(config.defaultQuotaDeployment);
        BigInteger chainId = config.chainId;
        int version = config.version;
        String value = "0";

        String contractAddress = contractUtil.deployContract(contractFile, nonce, quota, version, chainId, value);

        config.update("BillManagementContractAddress", contractAddress);
        log.info("[TestDeployContract]contractPath: {}", contractPath);
        log.info("[TestDeployContract]contractAddress: {}", contractAddress);

        contractUtil.storeAbiToBlockchain(contractAddress, contractPath);
        log.info("[TestDeployContract]:storeAbiTpBlockChain");
    }

    @Test
    void TestCallContractSetBill(){
        String contractAddress = config.billManagementContractAddress;
        int count = 10;
        for(int i = 0; i < count; i ++){
            // 创建账单
            String description = StringUtil.getRandomString(20);
            String sender = StringUtil.getRandomString(4);
            String receiver = StringUtil.getRandomString(4);
            BigInteger amount = BigInteger.valueOf(1000);

            log.info("[TestCallContractSetBill] setBill");
            log.info(description + " " + sender + " " + receiver + " " + amount);

            Map<String, String> bill = new HashMap<>();
            bill.put("description", description);
            bill.put("sender", sender);
            bill.put("receiver", receiver);
            bill.put("amount", String.valueOf(amount));

            BillManagementService billManagementService = new BillManagementServiceImpl();
            billManagementService.callContractSetBill(config, contractAddress, bill);
        }
    }

    @Test
    void TestCallContractGetBill(){
        String contractAddress = config.billManagementContractAddress;
        System.out.println(contractAddress);
        BigInteger billId = BigInteger.valueOf(5);
        BillManagementService billManagementService = new BillManagementServiceImpl();
        billManagementService.callContractGetBill(config, contractAddress, billId);
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

        String contractAddress = contractUtil.deployContract(contractFile, nonce, quota, version, chainId, value);
        config.update("BusinessManagementContractAddress", contractAddress);
        log.info("[TestDeployBusinessContract]contractPath: {}", contractPath);
        log.info("[TestDeployBusinessContract]contractAddress: {}", contractAddress);

        contractUtil.storeAbiToBlockchain(contractAddress, contractPath);
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
