pragma solidity ^0.4.18;

contract Evidence {
    // 定义Business结构体
    struct Business {
        string uuId;
        string businessType;
        string businessId;
        string businessTime;
        string businessData;
    }

    // 使用bytes32作为映射的键（而非string）
    mapping(bytes32 => Business) public evidences;

    // 创建单据的方法
    function createEvidence(
        bytes32 _uuId, // 改为bytes32类型
        string _businessType,
        string _businessId,
        string _businessTime,
        string _businessData
    ) public {
        evidences[_uuId] = Business({
        uuId: _businessType,
        businessType: _businessType,
        businessId: _businessId,
        businessTime: _businessTime,
        businessData: _businessData
        });
    }

    // 获取单据的方法，返回对应的Business信息
    function getEvidence(bytes32 _uuId) public view returns (
        string,
        string,
        string,
        string,
        string
    ) {
        Business storage business = evidences[_uuId];
        require(bytes(business.uuId).length > 0);
        return (
        business.uuId,
        business.businessType,
        business.businessId,
        business.businessTime,
        business.businessData
        );
    }
}
