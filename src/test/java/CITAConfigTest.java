import com.citahub.cita.protocol.CITAj;
import config.CITAConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Properties;

@Slf4j
public class CITAConfigTest {
    @Test
    public void TestLoadProperties(){
        CITAConfig config = new CITAConfig("src/main/resources/cita_school.properties");

    }
}
