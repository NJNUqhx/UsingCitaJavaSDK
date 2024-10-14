package util;

import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameterName;
import com.citahub.cita.protocol.core.methods.response.AppMetaData;
import com.citahub.cita.protocol.http.HttpService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
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

    public static void main(String[] args){
        String url =  "http://1.94.111.202:1337";
        HttpService httpService = new HttpService(url, false);
        CITAj service = CITAj.build(httpService);
        System.out.println("getVersion: " + getVersion(service));
        System.out.println("getChainId: " + getChainId(service));
        System.out.println("getCurrentHeight: " + getCurrentHeight(service));
        System.out.println("getValidUtilBlock: " + getValidUtilBlock(service));
    }
}
