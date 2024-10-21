package CitaChainClient;

import com.pojo.adapter.ChainCrossAdapter;
import com.pojo.adapter.impl.CitaChainAdapter;
import com.pojo.client.ChainClientBase;
import com.pojo.client.CitaChainClient;
import com.pojo.state.BlockState;
import com.pojo.state.TransactionState;
import com.pojo.util.Code;
import com.pojo.util.Evidence;
import com.pojo.util.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class TestCitaChainAdapter {
    String configPath = "src/main/resources/citaChainClientConfigFile.properties";

    ChainCrossAdapter chainCrossAdapter = new CitaChainAdapter();
    ChainClientBase clientBase;

    @Test
    void TestInitClient(){
        ResponseData<ChainClientBase> responseData = chainCrossAdapter.initClient(configPath);

        if(responseData.code == Code.SUCCESS.getCode()){
            clientBase = responseData.result;
            log.info(clientBase.toString());
        }else{
            log.error("TestInitClient Fail");
        }
    }

    @Test
    void TestCallCreateEvidence(){
        TestInitClient();
        for(int i = 0, count = 10; i < count; i ++){
            Evidence evidence = new Evidence();
            evidence.generateRandomValues();
            log.info("createEvidence: " + evidence);
            ResponseData<String> responseData = chainCrossAdapter.createEvidence(clientBase, evidence);
            if (responseData.code == Code.SUCCESS.getCode()){
                log.info("createEvidence successfully txHash: " + responseData.result);
                evidence.writeToFile("src/main/resources/evidence.txt");
            }else{
                log.error("createEvidence fail");
            }
        }
    }

    @Test
    void TestCallGetEvidence(){
        TestInitClient();
        String evidenceId = "70f50799-74c9-4151-9785-927620";
        ResponseData<List<Evidence>> responseData = chainCrossAdapter.getEvidence(clientBase, evidenceId);
        if (responseData.code == Code.SUCCESS.getCode()){
            List<Evidence> evidences = responseData.result;
            for (Evidence evidence: evidences) {
                System.out.println(evidence);
            }
        }

    }

    @Test
    void TestGetTransactionState(){
        TestInitClient();
        String transactionHash = "0x299a502654a92ee07b8bcccecdd772c67b5b343328b6d1d7bfb328083efd13cd";
        ResponseData<TransactionState> responseData = chainCrossAdapter.getTransactionState(clientBase, transactionHash);
        if(responseData.code == Code.SUCCESS.getCode()){
            log.info(responseData.result.toString());
        }
    }

    @Test
    void TestGetBlockState(){
        TestInitClient();
        String blockHash = "0x361d7344e6e71fc53b1af81703078048ff94c3874b48c515ecd7d898719acd65";
        ResponseData<BlockState> responseData = chainCrossAdapter.getBlockStateByHash(clientBase, blockHash);
        if(responseData.code == Code.SUCCESS.getCode()){
            log.info(responseData.result.toString());
            BlockState blockState = responseData.result;
            for(TransactionState transactionState: blockState.getTransactions()){
                log.info("解析块中存储交易: " + transactionState.toString());
            }
        }
    }
}
