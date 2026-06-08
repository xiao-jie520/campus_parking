package com.campus.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("parking_area")
public class ParkingArea {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String areaName;

    private Integer totalSpaces;

    private Integer availableSpaces;

    private String location;

    // 1:启用, 0:停用
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
