package com.campus.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("access_record")
public class AccessRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long vehicleId;

    private String plateNumber;

    // IN:入场, OUT:出场
    private String accessType;

    private Long spotId;

    private String spotNumber;

    private String areaName;

    private LocalDateTime accessTime;

    private Long operatorId;

    private String operatorName;

    private String remark;
}
