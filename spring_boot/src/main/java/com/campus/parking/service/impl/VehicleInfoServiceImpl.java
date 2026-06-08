package com.campus.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.parking.entity.VehicleInfo;
import com.campus.parking.mapper.VehicleInfoMapper;
import com.campus.parking.service.VehicleInfoService;
import org.springframework.stereotype.Service;

@Service
public class VehicleInfoServiceImpl extends ServiceImpl<VehicleInfoMapper, VehicleInfo> implements VehicleInfoService {
}
