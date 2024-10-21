package com.pojo.adapter;

import com.pojo.client.ChainClientBase;
import com.pojo.state.BlockState;
import com.pojo.state.TransactionState;
import com.pojo.util.Evidence;
import com.pojo.util.ResponseData;

import java.math.BigInteger;
import java.util.List;

public interface ChainCrossAdapter {

    /**
     * 根据配置文件路径生成链对象
     * @param configPath 配置文件路径
     * @return 包含链对象的响应数据
     */
    ResponseData<ChainClientBase> initClient(String configPath);

    /**
     * 根据单据ID获取单据列表
     * @param client 对该条链进行操作
     * @param evidenceId 单据ID
     * @return 包含单据列表的响应数据
     */
    ResponseData<List<Evidence>> getEvidence(ChainClientBase client, String evidenceId);

    /**
     * 创建新的单据
     * @param client 对该条链进行操作
     * @param evidence 要创建的单据对象
     * @return 表示操作成功或失败的响应数据
     */
    ResponseData<String> createEvidence(ChainClientBase client,Evidence evidence);

    /**
     * 更新已有的单据
     * @param client 对该条链进行操作
     * @param evidence 要更新的单据对象
     * @return 表示操作成功或失败的响应数据
     */
    ResponseData<Boolean> updateEvidence(ChainClientBase client,Evidence evidence);

    /**
     * 根据交易哈希查询交易状态
     * @param client 对该条链进行操作
     * @param transactionHash 要查询的交易的哈希
     * @return
     */
    ResponseData<TransactionState> getTransactionState(ChainClientBase client, String transactionHash);

    /**
     * 根据区块哈希查询区块状态
     * @param client 对该条链进行操作
     * @param hash 区块哈希
     * @return
     */
    ResponseData<BlockState> getBlockStateByHash(ChainClientBase client, String hash);

    /**
     * 根据区块高度查询区块状态
     * @param client 对该条链进行操作
     * @param height 区块高度
     * @return
     */
    ResponseData<BlockState> getBlockStateByHeight(ChainClientBase client, BigInteger height);

    /**
     * 获取链当前的高度
     * @param client 对该条链进行操作
     * @return 链的高度
     */
    ResponseData<BigInteger> getChainHeight(ChainClientBase client);
}
