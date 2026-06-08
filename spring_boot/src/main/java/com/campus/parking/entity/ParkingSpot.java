package com.campus.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("parking_spot")
public class ParkingSpot {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long areaId;

    private String spotNumber;

    // 0:空闲, 1:占用, 2:故障
    private Integer status;

    private Long vehicleId;

    private String plateNumber;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
