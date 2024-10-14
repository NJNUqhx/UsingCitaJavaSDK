import com.citahub.cita.protocol.CITAj;
import config.CITAConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Properties;

@Slf4j
public class CITAConfigTest {
    @Test
    public void TestLoadProperties(){
        Properties properties = CITAConfig.load(CITAConfig.configPath);
        System.out.println(properties.get("BillManagementContractSolidity"));
        CITAConfig config = new CITAConfig();
    }
}
