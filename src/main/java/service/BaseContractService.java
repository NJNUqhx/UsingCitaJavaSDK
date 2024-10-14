package service;

import com.citahub.cita.abi.EventValues;
import com.citahub.cita.abi.datatypes.Event;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.methods.response.Log;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.tx.TransactionManager;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Slf4j
public class BaseContractService extends com.citahub.cita.tx.Contract {
    BaseContractService(String contractBinary, String contractAddress, CITAj citaj, TransactionManager transactionManager, String nonce, long quota) {
        super(contractBinary, contractAddress, citaj, transactionManager, nonce, quota);
    }

    /**
     * 从日志中提取指定事件的参数值
     * @param event 事件类型（事件名称，索引列类型列表，非索引列类型列表）
     * @param log 一条日志（包含事件签名，事件值）
     * @return 附有日志的事件值（索引列，非索引列，日志）
     */
    protected static EventValuesWithLog extractEventParametersWithLog(Event event, Log log) {
        final EventValues eventValues = staticExtractEventParameters(event, log);
        return (eventValues == null) ? null : new EventValuesWithLog(eventValues, log);
    }

    /**
     * 从交易回执的每条日志中提取指定事件的参数值
     * @param event 事件类型（事件名称，索引列类型列表，非索引列类型列表）
     * @param transactionReceipt 交易回执（包含多条日志信息）
     * @return
     */
    protected static List<EventValuesWithLog> extractEventParametersWithLog(
            Event event, TransactionReceipt transactionReceipt) {

        System.out.println(transactionReceipt.getLogs().stream().map(log -> extractEventParametersWithLog(event, log)));

        return transactionReceipt
                .getLogs()
                .stream()
                .map(log -> extractEventParametersWithLog(event, log))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    // 将十六进制字符串转为 ASCII 字符串
    public static String hexToAscii(String hex) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            char ch = (char) Integer.parseInt(str, 16);
            if (ch != 0) {  // 跳过空字符
                output.append(ch);
            }
        }
        return output.toString();
    }

    // 将十六进制字符串解析为大整数
    public static BigInteger hexToBigInteger(String hex) {
        return new BigInteger(hex, 16);
    }

    // 解析 BillCreated 事件的 data 域并返回结果字符串
    public static String parseBillCreatedEvent(String data) {
        // 移除前缀 "0x" 如果有
        if (data.startsWith("0x")) {
            data = data.substring(2);
        }

        // 按32字节分割
        String idHex = data.substring(0, 64); // 第一个参数：id
        String amountHex = data.substring(192, 256); // 第五个参数：amount

        // 从192字节之后，解析字符串，都是32字节的块
        String descriptionHex = data.substring(256, 320); // 第二个参数：description
        String senderHex = data.substring(384, 448); // 第三个参数：sender
        String receiverHex = data.substring(512, 576); // 第四个参数：receiver

        // 转换各个字段
        BigInteger id = hexToBigInteger(idHex);
        BigInteger amount = hexToBigInteger(amountHex);
        String description = hexToAscii(descriptionHex);
        String sender = hexToAscii(senderHex);
        String receiver = hexToAscii(receiverHex);

        // 拼接解析结果为字符串
        String result = "BillCreated Event Parameters:\n" +
                "ID: " + id + "\n" +
                "Description: " + description + "\n" +
                "Sender: " + sender + "\n" +
                "Receiver: " + receiver + "\n" +
                "Amount: " + amount;

        return result;
    }
    public static HashMap<String, String> extractFromTransactionReceipt(TransactionReceipt transactionReceipt){
        List<Log> logs = transactionReceipt.getLogs();
        HashMap<String, String> result = new HashMap<>();
        log.info("num of logs: " + logs.size());
        for(Log logElem: logs){
            String data = parseBillCreatedEvent(logElem.getData());
            log.info(data);
            result.put("data", data);
        }



        return result;
    }
}
