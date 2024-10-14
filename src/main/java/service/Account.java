package service;

import com.citahub.cita.crypto.ECKeyPair;
import com.citahub.cita.crypto.Keys;
import com.citahub.cita.crypto.sm2.SM2;
import com.citahub.cita.crypto.sm2.SM2KeyPair;
import com.citahub.cita.crypto.sm2.SM2Keys;
import com.citahub.cita.protocol.account.CompiledContract;
import com.citahub.cita.utils.Numeric;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.citahub.cita.abi.datatypes.Function;
import com.citahub.cita.abi.datatypes.Type;
import com.citahub.cita.abi.datatypes.UnorderedEvent;
import com.citahub.cita.protocol.CITAj;
import com.citahub.cita.protocol.core.DefaultBlockParameter;
import com.citahub.cita.protocol.core.DefaultBlockParameterName;
import com.citahub.cita.protocol.core.methods.request.AppFilter;
import com.citahub.cita.protocol.core.methods.request.Call;
import com.citahub.cita.protocol.core.methods.response.AppCall;
import com.citahub.cita.protocol.core.methods.response.AppSendTransaction;
import com.citahub.cita.protocol.core.methods.response.Log;
import com.citahub.cita.tx.RawTransactionManager;
import com.citahub.cita.utils.TypedAbi;
import io.reactivex.Flowable;
import com.citahub.cita.protocol.core.methods.response.AbiDefinition;

import com.citahub.cita.abi.EventEncoder;
import com.citahub.cita.abi.EventValues;
import com.citahub.cita.abi.FunctionEncoder;
import com.citahub.cita.abi.FunctionReturnDecoder;
import com.citahub.cita.abi.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Account {
    private static Logger logger = LoggerFactory.getLogger(Account.class);
    private static final String ABI_ADDRESS = "ffffffffffffffffffffffffffffffffff010001";
    private RawTransactionManager transactionManager;
    private CITAj service;
    private String abi;



    /**
     * 创建适用于国密版本的account
     * @param privateKey 0x+hex
     * @param service
     */
    public Account(String privateKey, CITAj service) {
        SM2 sm2 = new SM2();
        SM2KeyPair sm2KeyPair = sm2.fromPrivateKey(privateKey);
        String addr = SM2Keys.getAddress(sm2KeyPair.getPublicKey());
        logger.info("accountAddress: {}", addr);
        this.transactionManager = RawTransactionManager.createSM2Manager(service, sm2KeyPair);
        this.service = service;
    }



    /**
     * 创建适用于国密版本的account
     * @param sm2KeyPair
     * @param service
     */
    public Account(SM2KeyPair sm2KeyPair, CITAj service) {
        ECKeyPair ecKeyPair = ECKeyPair.create(sm2KeyPair.getPrivateKey());
        String publicKey = String.valueOf(ecKeyPair.getPublicKey());
        String address = Keys.getAddress(new BigInteger(publicKey));
        logger.info("accountAddress: {}", address);
        this.transactionManager = RawTransactionManager.createSM2Manager(service, sm2KeyPair);
        this.service = service;
    }

    public RawTransactionManager getTransactionManager() {
        return transactionManager;
    }

    /// TODO: get contract address from receipt after deploy, then return contract name
    public AppSendTransaction deploy(
            File contractFile, String nonce, long quota,
            int version, BigInteger chainId, String value)
            throws IOException, InterruptedException, CompiledContract.ContractCompileError {
        CompiledContract contract = new CompiledContract(contractFile);
        String contractBin = contract.getBin();
        return this.transactionManager
                .sendTransaction("", contractBin, quota, nonce, getValidUntilBlock(),
                        version, chainId, value);
    }

    /**
     *
     * @param contractFile
     * @param constructorCode
     * @param nonce
     * @param quota
     * @param version
     * @param chainId
     * @param value
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws CompiledContract.ContractCompileError
     */
    public AppSendTransaction deploy(
            File contractFile, String constructorCode, String nonce, long quota,
            int version, BigInteger chainId, String value)
            throws IOException, InterruptedException, CompiledContract.ContractCompileError {
        CompiledContract contract = new CompiledContract(contractFile);
        String contractBin = contract.getBin();
        String data = constructorCode != null ? contractBin + Numeric.cleanHexPrefix(constructorCode) : contractBin;
        return this.transactionManager
                .sendTransaction("", data, quota, nonce, getValidUntilBlock(),
                        version, chainId, value);
    }

    public Flowable<AppSendTransaction> deployAsync(
            File contractFile, String nonce, long quota,
            int version, BigInteger chainId, String value)
            throws IOException, InterruptedException, CompiledContract.ContractCompileError {
        CompiledContract contract = new CompiledContract(contractFile);
        String contractBin = contract.getBin();
        return this.transactionManager
                .sendTransactionAsync("", contractBin, quota, nonce, getValidUntilBlock(),
                        version, chainId, value);
    }

    // sendTransaction: nonce and quota is necessary
    public Object callContract(
            String contractAddress, String funcName,
            String nonce, long quota, int version,
            BigInteger chainId, String value, Object... args)
            throws Exception {
        abi = getAbi(contractAddress);
        CompiledContract contract = new CompiledContract(abi);
        AbiDefinition functionAbi = contract.getFunctionAbi(funcName, args.length);
        return callContract(
                contractAddress, functionAbi, nonce, quota, version, chainId, value, args);
    }

    public Object callContract(
            String contractAddress, AbiDefinition functionAbi,
            String nonce, long quota,
            int version,BigInteger chainId, String value, Object... args)
            throws Exception {
        List<Type> params = new ArrayList<>();
        List<AbiDefinition.NamedType> inputs = functionAbi.getInputs();
        for (int i = 0; i < inputs.size(); i++) {
            Object arg = args[i];
            String typeName = inputs.get(i).getType();
            params.add(TypedAbi.getType(typeName, arg));
        }

        Function func;
        if (functionAbi.isConstant()) {
            // call
            List<TypedAbi.ArgRetType> retsType = new ArrayList<>();
            List<TypeReference<?>> retsTypeRef = new ArrayList<>();
            List<AbiDefinition.NamedType> outputs = functionAbi.getOutputs();
            for (AbiDefinition.NamedType namedType: outputs) {
                TypedAbi.ArgRetType retType = TypedAbi.getArgRetType(namedType.getType());
                retsType.add(retType);
                retsTypeRef.add(retType.getTypeReference());
            }
            func = new Function(functionAbi.getName(), params, retsTypeRef);
            return appCall(contractAddress, func, retsType);
        } else {
            func = new Function(functionAbi.getName(), params, Collections.emptyList());
            return sendTransaction(
                    contractAddress, func, nonce, quota, version, chainId, value);
        }
    }

    public Object appCall(String contractAddress, Function func, List<TypedAbi.ArgRetType> retsType)
            throws IOException {
        String data = FunctionEncoder.encode(func);
        AppCall call = this.service.appCall(new Call(this.transactionManager.getFromAddress(),
                contractAddress, data), DefaultBlockParameterName.PENDING).send();
        String value = call.getValue();
        List<Type> abiValues = FunctionReturnDecoder.decode(value, func.getOutputParameters());
        if (retsType.size() == 1) {
            return retsType.get(0).abiToJava(abiValues.get(0));
        } else {
            List<Object> results = new ArrayList<>();
            for (int i = 0; i < retsType.size(); i++) {
                results.add(retsType.get(i).abiToJava(abiValues.get(i)));
            }
            return results;
        }
    }

    public Object sendTransaction(
            String contractAddress, Function func, String nonce,
            long quota, int version, BigInteger chainId, String value)
            throws IOException {
        String data = FunctionEncoder.encode(func);
        return this.transactionManager.sendTransaction(
                contractAddress, data, quota, nonce, getValidUntilBlock(),
                version, chainId, value);
    }

    public Object uploadAbi(
            String contractAddress, String abi, String nonce, long quota,
            int version, BigInteger chainId, String value) throws Exception {
        String data = hex_remove_0x(contractAddress) + hex_remove_0x(bytesToHexStr(abi.getBytes()));
        return this.transactionManager.sendTransaction(
                ABI_ADDRESS, data, quota, nonce, getValidUntilBlock(),
                version, chainId, value);
    }

    public String getAbi(String contractAddress) throws IOException {
        String abi = service.appGetAbi(
                contractAddress, DefaultBlockParameterName.PENDING).send().getAbi();
        return new String(hexStrToBytes(hex_remove_0x(abi)));
    }

    public Flowable<Object> eventFlowable(String contractName, String eventName)
            throws Exception {
        CompiledContract contract = loadContract(contractName);
        String contractAddress = getContractAddress(contractName);
        AbiDefinition eventAbi = contract.getEventAbi(eventName);
        return eventFlowable(
                contractAddress, eventAbi,
                DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.PENDING);
    }

    public Flowable<Object> eventFlowable(String contractAddress, AbiDefinition eventAbi,
                                          DefaultBlockParameter start,
                                          DefaultBlockParameter end)
            throws Exception {
        List<TypedAbi.ArgRetType> results = new ArrayList<>();
        List<AbiDefinition.NamedType> namedTypes = eventAbi.getInputs();
        UnorderedEvent event = new UnorderedEvent(eventAbi.getName());
        for (AbiDefinition.NamedType namedType: namedTypes) {
            TypedAbi.ArgRetType argRetType = TypedAbi.getArgRetType(namedType.getType());
            results.add(argRetType);
            event.add(namedType.isIndexed(), argRetType.getTypeReference());
        }

        AppFilter filter = new AppFilter(start, end, contractAddress);
        /// FIXME: https://github.com/web3j/web3j/issues/209, patch to this after cita fixed
        filter.addSingleTopic(EventEncoder.encode(event));
        return this.service.appLogFlowable(filter).map(new io.reactivex.functions.Function<Log, Object>() {
            @Override
            public Object apply(Log log) throws Exception {
                EventValues eventValues = staticExtractEventParameters(event, log);
                List<Type> indexedValues = eventValues.getIndexedValues();
                List<Type> nonIndexedValues = eventValues.getNonIndexedValues();
                int indexedSize = indexedValues.size();
                int nonIndexedSize = nonIndexedValues.size();
                int size = indexedSize + nonIndexedSize;
                List<Object> values = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    values.add(null);
                }

                List<Integer> indexedSeq = event.getIndexedParametersSeq();
                for (int i = 0; i < indexedSize; i++) {
                    int indexSeqNum = indexedSeq.get(i);
                    values.set(
                            indexSeqNum, results.get(indexSeqNum).abiToJava(indexedValues.get(i)));
                }

                List<Integer> nonIndexedSeq = event.getNonIndexedParametersSeq();
                for (int i = 0; i < nonIndexedSize; i++) {
                    int indexSeqNum = nonIndexedSeq.get(i);
                    values.set(
                            indexSeqNum, results.get(indexSeqNum).abiToJava(nonIndexedValues.get(i)));
                }
                return values;
            }
        });
    }

    private static EventValues staticExtractEventParameters(
            UnorderedEvent event, Log log) {

        List<String> topics = log.getTopics();
        String encodedEventSignature = EventEncoder.encode(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            return null;
        }

        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                log.getData(), event.getNonIndexedParameters());

        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        return new EventValues(indexedValues, nonIndexedValues);
    }

    private CompiledContract loadContract(String contractName) throws IOException {
        /// TODO: jsonrpc
        return new CompiledContract("");
    }

    /// TODO: get contract address
    private String getContractAddress(String contractName) {
        return "";
    }

    private long blockHeight() throws IOException {
        return this.service.appBlockNumber().send().getBlockNumber().longValue();
    }

    private long getValidUntilBlock() throws IOException {
        return blockHeight() + 80;
    }

    private String hex_remove_0x(String hex) {
        if (hex.contains("0x")) {
            return hex.substring(2);
        }
        return hex;
    }

    private String bytesToHexStr(byte[] byteArr) {
        if (null == byteArr || byteArr.length < 1) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte t : byteArr) {
            if ((t & 0xF0) == 0) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(t & 0xFF));
        }
        return sb.toString();
    }

    private byte[] hexStrToBytes(String hexStr) {
        if (null == hexStr || hexStr.length() < 1) {
            return null;
        }
        int byteLen = hexStr.length() / 2;
        byte[] result = new byte[byteLen];
        char[] hexChar = hexStr.toCharArray();
        for (int i = 0; i < byteLen; i++) {
            result[i] = (byte)
                    (Character.digit(hexChar[i * 2],16) << 4
                            | Character.digit(hexChar[i * 2 + 1],16));
        }
        return result;
    }
}


