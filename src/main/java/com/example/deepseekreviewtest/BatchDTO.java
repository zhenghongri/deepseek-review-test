package com.example.deepseekreviewtest;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class BatchDTO implements Serializable {

    private String clientCode;
    private String warehouseCode;
    private String batchNo;
    private String taskNo;
    private String orderNum;
    private String pushNode;
    private String status;
    private String dnStatus;
    private String dnNo;
    private String packedTime;
    private String hasPackedFinish;
    private String isOldDn;
    private String createTime;
    private String arrivedTime;
    private String signTime;


    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();


        map.put("clientCode", clientCode);


        map.put("warehouseCode", warehouseCode);
        map.put("batchNo", batchNo);
        map.put("taskNo", taskNo);
        map.put("orderNum", orderNum);
        map.put("pushNode", pushNode);
        map.put("status", status);
        map.put("dnStatus", dnStatus);
        map.put("dnNo", dnNo);
        map.put("packedTime", packedTime);
        map.put("hasPackedFinish", hasPackedFinish);
        map.put("isOldDn", isOldDn);
        map.put("createTime", createTime);
        map.put("arrivedTime", arrivedTime + " 10:00:00");
        map.put("signTime", signTime + " 10:00:00");
        return map;
    }
}
