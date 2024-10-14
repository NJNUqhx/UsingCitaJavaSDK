package service;

import com.citahub.cita.abi.TypeReference;
import com.citahub.cita.abi.datatypes.Address;
import com.citahub.cita.abi.datatypes.DynamicBytes;
import com.citahub.cita.abi.datatypes.Event;
import com.citahub.cita.abi.datatypes.generated.Bytes32;
import com.citahub.cita.abi.datatypes.generated.Int256;
import com.citahub.cita.abi.datatypes.generated.Uint256;
import com.citahub.cita.protocol.core.methods.response.Log;
import com.citahub.cita.protocol.core.methods.response.TransactionReceipt;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TestExtractEvent {
    public static final Event SETBILL_EVENT = new Event("BillCreated", Arrays.<TypeReference<?>>asList(
            new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(
                    new TypeReference<Bytes32>() {},
                    new TypeReference<DynamicBytes>() {},
                    new TypeReference<Uint256>() {},
                    new TypeReference<Int256>() {}));

    public static final Event GETBILL_EVENT = new Event("BillRetrieved", Arrays.<TypeReference<?>>asList(
            new TypeReference<Address>() {}),
            Arrays.<TypeReference<?>>asList(
                    new TypeReference<Bytes32>() {},
                    new TypeReference<DynamicBytes>() {},
                    new TypeReference<Uint256>() {},
                    new TypeReference<Int256>() {}));

    public static final void getSetBillEvents(TransactionReceipt transactionReceipt){
        HashMap<String, String> responses = new HashMap<>();
        List<EventValuesWithLog> valueList = BaseContractService.extractEventParametersWithLog(SETBILL_EVENT, transactionReceipt);
        log.info("解析回执" + transactionReceipt + "SetBill事件");
        System.out.println(valueList.size());
        for(EventValuesWithLog eventValuesWithLog: valueList){
            SetBillEventResponse setBillEventResponse = new SetBillEventResponse();
            setBillEventResponse.log = eventValuesWithLog.getLog();
            setBillEventResponse.id = (BigInteger) eventValuesWithLog.getNonIndexedValues().get(0).getValue();
            setBillEventResponse.description = (String) eventValuesWithLog.getIndexedValues().get(0).getValue();
            setBillEventResponse.sender = (String) eventValuesWithLog.getIndexedValues().get(1).getValue();
            setBillEventResponse.receiver = (String) eventValuesWithLog.getIndexedValues().get(2).getValue();
            setBillEventResponse.amount = (BigInteger) eventValuesWithLog.getNonIndexedValues().get(1).getValue();
            System.out.println(setBillEventResponse);
        }
    }

    public static class SetBillEventResponse{
        Log log;
        BigInteger id;
        String description;
        String sender;
        String receiver;
        BigInteger amount;

        @Override
        public String toString() {
            return "SetBillEventResponse{" +
                    "log=" + log +
                    ", id=" + id +
                    ", description='" + description + '\'' +
                    ", sender='" + sender + '\'' +
                    ", receiver='" + receiver + '\'' +
                    ", amount=" + amount +
                    '}';
        }
    }
}
