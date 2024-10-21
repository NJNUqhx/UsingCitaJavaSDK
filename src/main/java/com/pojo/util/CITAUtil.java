package com.pojo.util;

import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameter;
import com.citahub.cita.protocol.core.DefaultBlockParameterName;
import com.citahub.cita.protocol.core.methods.response.AppBlock;
import com.citahub.cita.protocol.core.methods.response.AppMetaData;
import com.citahub.cita.protocol.core.methods.response.Transaction;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.protocol.http.HttpService;
import com.pojo.client.ChainType;
import com.pojo.state.BlockState;
import com.pojo.state.TransactionState;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

@Slf4j
public class CITAUtil {

    public static int getVersion(CITAj service) {
        AppMetaData appMetaData = null;
        try {
            appMetaData = service.appMetaData(DefaultBlockParameterName.PENDING).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appMetaData.getAppMetaDataResult().getVersion();
    }

    public static BigInteger getChainId(CITAj service) {
        AppMetaData appMetaData = null;
        try {
            appMetaData = service.appMetaData(DefaultBlockParameterName.LATEST).send();
        } catch (Exception e) {
            e.printStackTrace();
            //System.exit(1);
        }
        return appMetaData.getAppMetaDataResult().getChainId();
    }

    public static String getNonce() {
        Random random = new Random(System.currentTimeMillis());
        return String.valueOf(Math.abs(random.nextLong()));
    }

    public static BigInteger getCurrentHeight(CITAj service) {
        return getCurrentHeight(service, 3);
    }

    private static BigInteger getCurrentHeight(CITAj service, int retry) {
        int count = 0;
        long height = -1;
        while (count < retry) {
            try {
                height = service.appBlockNumber().send().getBlockNumber().longValue();
            } catch (Exception e) {
                height = -1;
                log.error("getBlockNumber failed retry ..");
                try {
                    Thread.sleep(2000);
                } catch (Exception e1) {
                    log.error("failed to get block number, Exception: " + e1);
                    //System.exit(1);
                }
            }
            count++;
        }
        if (height == -1) {
            log.error("Failed to get block number after " + count + " times.");
            //System.exit(1);
        }
        return BigInteger.valueOf(height);
    }

    public static BigInteger getValidUtilBlock(CITAj service) {
        return getCurrentHeight(service).add(BigInteger.valueOf(88));
    }
    public static BigInteger hexStringToBigInteger(String hex) {
        // 检查输入字符串是否以 "0x" 开头
        if (hex.startsWith("0x")) {
            // 移除 "0x" 前缀，并创建 BigInteger 对象
            return new BigInteger(hex.substring(2), 16);
        } else {
            // 如果没有 "0x" 前缀，则直接处理字符串
            return new BigInteger(hex, 16);
        }
    }

    /**
     *
     * @param args
     * @throws IOException
     */
        public static void main(String[] args) throws IOException {
        String url =  "http://49.52.27.69:1337";
        HttpService httpService = new HttpService(url, false);
        CITAj service = CITAj.build(httpService);
        System.out.println("getVersion: " + getVersion(service));
        System.out.println("getChainId: " + getChainId(service));
        System.out.println("getCurrentHeight: " + getCurrentHeight(service));
        System.out.println("getValidUtilBlock: " + getValidUtilBlock(service));
        AppBlock.Block block =  service.appGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(10000)), false).send().getBlock();
        System.out.println("version: " + block.getVersion());
        System.out.println("hash: " + block.getHash());
        AppBlock.Body body = block.getBody();
        System.out.println(body.getTransactions());


        System.out.println("----------------------------------------------------------------");
        String txHash = "0x299a502654a92ee07b8bcccecdd772c67b5b343328b6d1d7bfb328083efd13cd";
        Transaction transaction = service.appGetTransactionByHash(txHash).send().getTransaction();
        TransactionReceipt transactionReceipt = service.appGetTransactionReceipt(txHash).send().getTransactionReceipt();

        //System.out.println("content: " + content);

        ChainType type = ChainType.CITA;
        BigInteger chainId = getChainId(service);
        String blockHash = transaction.getBlockHash();
        AppBlock.Block block1 =  service.appGetBlockByHash(blockHash, false).send().getBlock();
        BigInteger timestamp = BigInteger.valueOf(block1.getHeader().getTimestamp());
        String blockHash1 = transaction.getBlockHash();
        BigInteger blockHeight = transaction.getBlockNumber();
        String content = transaction.getContent();
        String from = transaction.getFrom();
        String hash = transaction.getHash();
        TransactionState transactionState = new TransactionState(type, chainId, timestamp, blockHash1, blockHeight, content, from, hash);
        System.out.println("交易状态:\n" + transactionState);

        System.out.println("----------------------------------------------------------------");

        String blockHash2 = "0x361d7344e6e71fc53b1af81703078048ff94c3874b48c515ecd7d898719acd65" ;
        AppBlock.Block targetBlock = service.appGetBlockByHash(blockHash2, true).send().getBlock();
        String preHash = targetBlock.getHeader().getPrevHash();
        //System.out.println(targetBlock.getHeader().getNumber());
        BigInteger height = hexStringToBigInteger(targetBlock.getHeader().getNumber());
        String hash1 = targetBlock.getHash();
        BigInteger timestamp1 = BigInteger.valueOf(targetBlock.getHeader().getTimestamp());
        List<AppBlock.TransactionObject> transactions = targetBlock.getBody().getTransactions();
        BigInteger number = BigInteger.valueOf(transactions.size());

        for(int i = 0; i < transactions.size(); i ++){
            Transaction transaction1 = transactions.get(i).get();
            System.out.println("交易哈希:" + transaction1.getHash());
        }

        BlockState blockState = new BlockState(type, chainId, preHash, height, hash1, timestamp1, number, null);
        System.out.println("区块状态:\n" + blockState);
        }
}
