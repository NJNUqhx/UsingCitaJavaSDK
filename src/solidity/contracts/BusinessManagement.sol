// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.4.18;

contract BusinessManagement {
    // 定义一个业务结构体
    struct Business {
        string uuId;
        string businessType;
        string businessId;
        string businessTime;
        string businessData;
    }

    // 业务记录的映射 (通过uuId进行映射)
    mapping(string => Business) private businessRecords;

    // 创建一个新业务的事件
    event BusinessCreated(string uuId, string businessType, string businessId, string businessTime, string businessData);

    // 创建业务
    function createBusiness(string _uuId, string _businessType, string _businessId, string _businessTime, string _businessData) public {
        // 确保该业务尚未存在
        require(bytes(businessRecords[_uuId].uuId).length == 0);

        // 创建业务并存储
        businessRecords[_uuId] = Business(_uuId, _businessType, _businessId, _businessTime, _businessData);

        // 触发事件
        BusinessCreated(_uuId, _businessType, _businessId, _businessTime, _businessData);
    }

    // 获取业务信息
    function getBusiness(string _uuId) public view returns (string, string, string, string, string) {
        // 确保业务存在
        require(bytes(businessRecords[_uuId].uuId).length != 0);

        Business memory business = businessRecords[_uuId];
        return (business.uuId, business.businessType, business.businessId, business.businessTime, business.businessData);
    }

    // 更新业务
    function updateBusiness(string _uuId, string _businessType, string _businessId, string _businessTime, string _businessData) public {
        // 确保业务存在
        require(bytes(businessRecords[_uuId].uuId).length != 0);

        // 更新业务
        businessRecords[_uuId] = Business(_uuId, _businessType, _businessId, _businessTime, _businessData);
    }
}

