package config;

import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.http.HttpService;
import com.citahub.cita.tx.response.PollingTransactionReceiptProcessor;
import lombok.extern.slf4j.Slf4j;
import com.pojo.util.CITAUtil;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Slf4j
public class CITAConfig {
    private String configPath;
    private static final String CITA_NET_ADDR = "CITANetIpAddr";
    private static final String BILL_Management_CONTRACT_SOLIDITY = "BillManagementContractSolidity";
    private static final String BILL_Management_CONTRACT_ADDRESS = "BillManagementContractAddress";
    private static final String BUSINESS_MANAGEMENT_CONTRACT_SOLIDITY = "BusinessManagementContractSolidity";
    private static final String BUSINESS_MANAGEMENT_CONTRACT_ADDRESS = "BusinessManagementContractAddress";
    private static final String DEFAULT_QUOTA_Deployment = "QuotaForDeployment";
    private static final String ADMIN_PRIVATE_KEY = "AdminPrivateKey";
    private static final String ADMIN_ADDRESS = "AdminAddress";
    private static final String CRYPTO_TYPE = "CryptoType";
    private static final String SALT_LENGTH = "SaltLength";
    public BigInteger chainId;
    public int version;
    public String ipAddr;
    public String defaultQuotaDeployment;
    public CITAj service;
    public PollingTransactionReceiptProcessor txProcessor;
    public String cryptoTx;
    public String adminPrivateKey;
    public String adminAddress;
    public String saltLength;

    public String billManagementContractSolidity;
    public String billManagementContractAddress;
    public String businessManagementContractSolidity;
    public String businessManagementContractAddress;

    public CITAConfig(String _configPath){
        try{
            configPath = _configPath;
            Properties properties = load(configPath);
            loadPropsToAttr(properties);
        }catch(Exception e){
            log.error("Failed to load CITAConfig at path: " + configPath);
            log.error(e.getMessage());
            e.printStackTrace();
        }

    }


    public static Properties load(String path) {
        Properties props = new Properties();
        try {
            props.load(Files.newInputStream(Paths.get(path)));
        } catch (Exception e) {
            log.error("Failed to read config at path " + path);
        }
        return props;
    }

    private void loadPropsToAttr(Properties props) {
        ipAddr = props.getProperty(CITA_NET_ADDR);
        service = CITAj.build(new HttpService(this.ipAddr));
        // 交易回执轮询器 1s轮询一次，最多20次
        txProcessor = new PollingTransactionReceiptProcessor(service, 1000, 20);
        chainId = CITAUtil.getChainId(service);
        version = CITAUtil.getVersion(service);

        defaultQuotaDeployment = props.getProperty(DEFAULT_QUOTA_Deployment);
        adminPrivateKey = props.getProperty(ADMIN_PRIVATE_KEY);
        cryptoTx = props.getProperty(CRYPTO_TYPE);
        adminAddress = props.getProperty(ADMIN_ADDRESS);
        saltLength = props.getProperty(SALT_LENGTH);

        // 合约地址
        billManagementContractSolidity = props.getProperty(BILL_Management_CONTRACT_SOLIDITY);
        billManagementContractAddress = props.getProperty(BILL_Management_CONTRACT_ADDRESS);
        businessManagementContractSolidity = props.getProperty(BUSINESS_MANAGEMENT_CONTRACT_SOLIDITY);
        businessManagementContractAddress = props.getProperty(BUSINESS_MANAGEMENT_CONTRACT_ADDRESS);
    }

    public void reload() {
        Properties properties = load(configPath);
        loadPropsToAttr(properties);
    }

    public void update(String key,String value) throws IOException {
        Properties properties = load(configPath);

        properties.setProperty(key, value);

        File file = new File(configPath);



        try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            properties.store(outputStream, null);
            log.info("update cita.properties success. key: " + key + " value: " + value);
        } catch (IOException e) {
            log.error("update properties fail");
        }

        reload();
        log.info("reload CITAConfig");
    }
}

