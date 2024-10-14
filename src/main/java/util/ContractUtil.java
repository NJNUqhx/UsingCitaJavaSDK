package util;

import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.account.CompiledContract;
import com.citahub.cita.protocol.core.methods.response.AppSendTransaction;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.protocol.exceptions.TransactionException;
import com.citahub.cita.tx.response.PollingTransactionReceiptProcessor;
import config.CITAConfig;
import lombok.extern.slf4j.Slf4j;
import service.Account;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ContractUtil {
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



    /**
     * /
     * 部署合约
     * @param contractFile
     * @param nonce
     * @param quota
     * @param version
     * @param chainId
     * @param value
     * @return
     * @throws CompiledContract.ContractCompileError
     * @throws IOException
     * @throws InterruptedException
     * @throws TransactionException
     */
    public static String deployContract(File contractFile, String nonce, long quota, int version, BigInteger chainId, String value) throws CompiledContract.ContractCompileError, IOException, InterruptedException, TransactionException {
        AppSendTransaction ethSendTransaction = adminAccount.deploy(contractFile, nonce, quota, version,
                chainId, "0");

        TransactionReceipt receipt =
                txProcessor.waitForTransactionReceipt(ethSendTransaction.getSendTransactionResult().getHash());
        if (receipt.getErrorMessage() != null) {
            log.error("deploy contract failed because of {}", receipt.getErrorMessage());
        }
        String contractAddress = receipt.getContractAddress();
        log.info("deploy contract success and contract address is "
                + receipt.getContractAddress());
       return contractAddress;
    }

    public static void storeAbiToBlockchain(String contractAddress, String contractPath) throws Exception {
        CompiledContract compiledContract = new CompiledContract(new File(contractPath)); // 编译合约，获取ABI
        System.out.println("get abi from " + contractPath + " : " + compiledContract.getAbi());
        AppSendTransaction ethSendTransaction =
                (AppSendTransaction) adminAccount.uploadAbi(
                        contractAddress, compiledContract.getAbi(),
                        CITAUtil.getNonce(), Long.parseLong(config.defaultQuotaDeployment), config.version,
                        config.chainId, "0");
        TransactionReceipt receipt =
                txProcessor.waitForTransactionReceipt(ethSendTransaction.getSendTransactionResult().getHash());
        if (receipt.getErrorMessage() != null) {
            log.error("call upload abi method failed because of "
                    + receipt.getErrorMessage());
            //System.exit(1);
        } else {
            log.info("call upload abi method success. Receipt " + receipt);
        }
        log.info("call upload abi method success and receipt is "
                + receipt.getTransactionHash());
    }


}
