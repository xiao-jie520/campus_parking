package com.campus.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.parking.entity.ParkingArea;
import com.campus.parking.mapper.ParkingAreaMapper;
import com.campus.parking.service.ParkingAreaService;
import org.springframework.stereotype.Service;

@Service
public class ParkingAreaServiceImpl extends ServiceImpl<ParkingAreaMapper, ParkingArea> implements ParkingAreaService {
}
