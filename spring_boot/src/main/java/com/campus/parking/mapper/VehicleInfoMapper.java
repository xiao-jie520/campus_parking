package com.campus.parking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.parking.entity.VehicleInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VehicleInfoMapper extends BaseMapper<VehicleInfo> {
    // BaseMapper 里已经自带了 insert、deleteById、updateById、selectById、selectList 等方法
}
