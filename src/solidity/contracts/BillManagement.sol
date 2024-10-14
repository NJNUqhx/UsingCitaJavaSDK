// SPDX-License-Identifier: MIT
pragma solidity ^0.4.18;

contract BillManagement {

    struct Bill {
        uint256 id;
        string description;
        string sender;
        string receiver;
        uint256 amount;
        bool isProcessed;
    }

    mapping(uint256 => Bill) private bills;
    uint256 private billCounter;


    // 事件
    event BillCreated(uint256 id, string description, string sender, string receiver, uint256 amount);// 监听创建合约
    event BillRetrieved(uint256 id);  // 监听获取合约
    event BillUpdated(uint256 id, string description, string sender, string receiver, uint256 amount, bool isProcessed); // 监听更新合约

    function setBill(string _description, string _sender, string _receiver, uint256 _amount) public returns (uint256) {
        billCounter++;
        bills[billCounter] = Bill(billCounter, _description, _sender, _receiver, _amount, false);
        BillCreated(billCounter, _description, _sender, _receiver, _amount);
        return billCounter;  // 返回新创建的 billId
    }


    function getBill(uint256 _billId) public view returns (uint256, string, string, string, uint256, bool) {
        require(_billId > 0 && _billId <= billCounter);
        Bill memory bill = bills[_billId];
        BillRetrieved(_billId);
        return (bill.id, bill.description, bill.sender, bill.receiver, bill.amount, bill.isProcessed);
    }

    function updateBill(uint256 _billId, string _description, string _sender, string _receiver, uint256 _amount, bool _isProcessed) public {
        require(_billId > 0 && _billId <= billCounter);
        Bill storage bill = bills[_billId];
        bill.description = _description;
        bill.sender = _sender;
        bill.receiver = _receiver;
        bill.amount = _amount;
        bill.isProcessed = _isProcessed;
        BillUpdated(bill.id, _description, _sender, _receiver, _amount, _isProcessed);
    }
}
