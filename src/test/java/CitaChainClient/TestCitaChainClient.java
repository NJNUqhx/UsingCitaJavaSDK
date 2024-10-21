package CitaChainClient;

import com.pojo.client.ChainClientBase;
import com.pojo.client.CitaChainClient;
import com.pojo.util.ContractUtil;
import org.junit.jupiter.api.Test;

import java.util.Properties;

public class TestCitaChainClient {
    String configFile = "src/main/resources/citaChainClientConfigFile.properties";
    ChainClientBase clientBase = new CitaChainClient(configFile, "cita1");

    public TestCitaChainClient() throws Exception {
    }

    @Test
    void TestLoad(){
        Properties properties = CitaChainClient.load(configFile);
        properties.list(System.out);  // 打印到控制台
        System.out.println(clientBase);
    }

    @Test
    void TestDeployContract(){
        // ContractUtil contractUtil = clientBase.
    }
}
