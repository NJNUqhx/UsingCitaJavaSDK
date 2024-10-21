[toc]

## 类

### 工具类

- ResponseData\<T>
  - BigInteger code 错误编码
  - String message 错误信息
  - T result 返回内容
- ErrorCode

### 单据类

- Evidence
  - String uuId
  - String businessType
  - String businessId
  - String businessTime
  - String businessData

### 链类

- ChainState 链状态类
  - 成员变量
    - public enum chainType

- ChainCilentBase 链客户端基类
  - 成员变量
    - public String configFile
  - 成员方法
    - ResponseData\<List\<Evidence>> getEvidence(String evidenceId)
    - ResponseData\<Boolean> createEvidence(Evidence evidence)
    - ResponseData\<Boolean> updateEvidence(Evidence evidence)
    - ResponseData\<ChainState> getChainState()
    - ResponseData\<String> getBlockInfo(BigInteger height)
    - ResponseData\<String> syncEvidence(List\<ChainCilent> chainClient, List\<String> evidenceIds)
  
- ChainMakerClient ChainMaker客户端类
  - 成员变量
    - ChainClient chainClient
  - 成员方法
    - ChainMakerClient(String configFile)

- CitaChainClient Cita 链客户端类
	
	- 成员变量
	  - String adminAddress
	  - String adminPrivateKey
	  - CITAj service
	  - Account account
	
	- 成员方法
	  - CitaChainClient(String configFile)
	
