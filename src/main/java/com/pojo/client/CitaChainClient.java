package com.pojo.client;

import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.http.HttpService;
import com.citahub.cita.tx.response.PollingTransactionReceiptProcessor;
import com.pojo.util.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Slf4j
public class CitaChainClient extends ChainClientBase{
    private String adminAddress; // 超级管理员地址
    private String adminPrivateKey; // 超级管理员私钥
    private CITAj service; // 建立与区块链的连接
    private Account adminAccount; // 用于部署、调用合约
    private ContractUtil contractUtil; // 用于部署合约
    private String evidenceContractAddress; // 合约地址
    private String evidenceContractPath; // 合约路径
    public BigInteger citaChainId;
    public int version;
    public String value;
    public String ipAddr;
    public long defaultQuotaDeployment;
    public PollingTransactionReceiptProcessor txProcessor; // 用于部署、调用合约



    /**
     * 构造函数，失败抛出异常
     * @param configFile 配置文件路径
     */
    public CitaChainClient(String configFile, String chainId){
        super(configFile, ChainType.CITA, chainId);
        Properties properties = load(configFile);
        if (properties.isEmpty()){
            log.error("Fail to Construct CitaChainClient at path " + configFile);
        }else{
            loadPropsToAttr(properties);
            log.info("Initialize CitaChainClient Successfully!!!");
        }
    }


    public Boolean isInitialized(){
        return service != null;
    }

    public ResponseData<CitaChainClient> initClient(String _configFile){
        configFile = _configFile;
        chainType = ChainType.CITA;

        Properties properties = load(configFile);
        if (properties.isEmpty()){
            // 加载文件失败
            log.error("Fail to Construct CitaChainClient at path " + configFile);
            return new ResponseData<>(Code.FAILURE, null);
        }else{
            try{
                loadPropsToAttr(properties);
            }catch(Exception e){
                // 连接失败
                log.error(e.getMessage());
                return new ResponseData<>(Code.FAILURE, null);
            }

            log.info("Initialize CitaChainClient Successfully!!!");
            return new ResponseData<>(Code.SUCCESS, this);

        }
    }

    /**
     * 从文件路径中加载属性
     * @param path 文件路径
     * @return 属性
     */
    public static Properties load(String path) {
        Properties props = new Properties();
        try {
            props.load(Files.newInputStream(Paths.get(path)));
        } catch (Exception e) {
            log.error("Failed to load properties at path " + path);
        }
        return props;
    }

    /**
     * 配置 Cita 链客户端参数
     * @param props 属性
     */
    private void loadPropsToAttr(Properties props) {
        ipAddr = props.getProperty("CITANetIpAddr");

        service = CITAj.build(new HttpService(this.ipAddr));
        adminPrivateKey = props.getProperty("AdminPrivateKey");
        adminAddress = props.getProperty("AdminAddress");
        adminAccount = new Account(adminPrivateKey, service);

        // 合约地址、合约路径
        evidenceContractAddress = props.getProperty("EvidenceContractAddress");
        evidenceContractPath = props.getProperty("EvidenceContractPath");

        // 交易回执轮询器 1s轮询一次，最多20次
        txProcessor = new PollingTransactionReceiptProcessor(service, 1000, 20);

        citaChainId = CITAUtil.getChainId(service);
        version = CITAUtil.getVersion(service);
        value = "0";
        defaultQuotaDeployment = Long.parseLong(props.getProperty("DefaultQuotaDeployment"));

        // 创建合约工具
        contractUtil = new ContractUtil(this);
    }

    public Account getAdminAccount() {
        return adminAccount;
    }

    public String getAdminAddress() {
        return adminAddress;
    }

    public String getAdminPrivateKey() {
        return adminPrivateKey;
    }

    public CITAj getService() {
        return service;
    }

    public Account getAccount() {
        return adminAccount;
    }

    public String getEvidenceContractAddress() {
        return evidenceContractAddress;
    }


    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "CitaChainClient{" +
                "adminAddress='" + adminAddress + '\'' +
                ", adminPrivateKey='" + adminPrivateKey + '\'' +
                ", service=" + service +
                ", adminAccount=" + adminAccount +
                ", evidenceContractAddress='" + evidenceContractAddress + '\'' +
                ", evidenceContractPath='" + evidenceContractPath + '\'' +
                ", citaChainId=" + citaChainId +
                ", version=" + version +
                ", ipAddr='" + ipAddr + '\'' +
                ", defaultQuotaDeployment='" + defaultQuotaDeployment + '\'' +
                ", txProcessor=" + txProcessor +
                '}';
    }
}
