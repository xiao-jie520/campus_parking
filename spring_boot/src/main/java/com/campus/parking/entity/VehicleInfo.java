package com.campus.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("vehicle_info") // 对应数据库的表名
public class VehicleInfo {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id;

    private Long userId;

    private String plateNumber; // 车牌号

    private String ownerName; // 车主姓名

    private String ownerPhone; // 车主手机号

    // INTERNAL:内部车, TEMPORARY:临时车
    private String vehicleType;

    // 1:正常, 0:黑名单
    private Integer status;

    // 0:未入场, 1:在场
    private Integer parkingStatus;

    private Long currentSpotId;

    private LocalDateTime createTime;
}
