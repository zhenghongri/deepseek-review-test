package com.example.deepseekreviewtest;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 从Excel文件流中分批导入数据到库中
 * EasyExcel参考文档：https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read
 *
 * @param <T>
 * @author wangrubin
 * @date 2022-08-02
 */
@Slf4j
public class ExcelImportListener<T> implements ReadListener<BatchDTO> {

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 1000;
    /**
     * 缓存的数据
     */
    private List<BatchDTO> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(BatchDTO data, AnalysisContext context) {
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

    private final static String ARRIVED_INSET_FORMAT = "INSERT INTO `tpt_biz_record` (`id`, `sys_code`, `type`, `order_num`, `body`, `response`, `num`, `url`, `status`, `remark`, `pid`, `create_time`, `create_by`, `update_time`, `update_by`, `del_flag`) VALUES (0, 'EMP', 'SYNC_PACKAGE_ARRIVED', '{batchNo}', '{\\\"clientCode\\\":\\\"{clientCode}\\\",\\\"warehouseCode\\\":\\\"{warehouseCode}\\\",\\\"taskNoList\\\":[\\\"{batchNo}\\\"],\\\"operateName\\\":\\\"manual\\\",\\\"operateTime\\\":\\\"2025-03-07 10:00:00\\\",\\\"arrivedTime\\\":\\\"{arrivedTime}\\\"}', '', 2, 'http://employeeportal-europe.lux-mate.cn/outbound/apis/task/arrived', 'N', NULL, NULL, '2025-03-07 10:00:00', 'manual-hr', null, null, '0');";
    private final static String DELIVERED_INSET_FORMAT = "INSERT INTO `tpt_biz_record` (`id`, `sys_code`, `type`, `order_num`, `body`, `response`, `num`, `url`, `status`, `remark`, `pid`, `create_time`, `create_by`, `update_time`, `update_by`, `del_flag`) VALUES (0, 'EMP', 'SYNC_PACKAGE_DELIVERED', '{batchNo}', '{\\\"clientCode\\\":\\\"{clientCode}\",\\\"warehouseCode\\\":\\\"{warehouseCode}\\\",\\\"taskNoList\\\":[\\\"{batchNo}\\\"],\\\"operateName\\\":\\\"manual\\\",\\\"operateTime\\\":\\\"2025-03-07 10:00:00\\\",\\\"signTime\\\":\\\"{signTime}\\\",\\\"remark\\\":null,\\\"urlList\\\":[\\\"\\\"]}', '', 2, 'http://employeeportal-europe.lux-mate.cn/outbound/apis/task/sign', 'N', NULL, NULL, '2025-03-07 10:00:00', 'manual-hr', null, null, '0');";

    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        Set<String> batchNoSet = new HashSet<>();
        for (BatchDTO batchDTO : cachedDataList) {
            if (batchNoSet.contains(batchDTO.getBatchNo())) {
                continue;
            }
            Map<String, String> map = batchDTO.toMap();
//            System.out.println(StrUtil.format(ARRIVED_INSET_FORMAT, map));
            System.out.println(StrUtil.format(DELIVERED_INSET_FORMAT, map));
            batchNoSet.add(batchDTO.getBatchNo());
        }

        log.info("{} 存储数据库成功！ 共{}条数据！", cachedDataList.size(), batchNoSet.size());
    }
}