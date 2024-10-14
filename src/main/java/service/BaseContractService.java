package service;

import com.citahub.cita.abi.EventValues;
import com.citahub.cita.abi.datatypes.Event;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.methods.response.Log;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import com.citahub.cita.tx.TransactionManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
}
