package com.campus.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.parking.entity.ParkingSpot;
import com.campus.parking.mapper.ParkingSpotMapper;
import com.campus.parking.service.ParkingSpotService;
import org.springframework.stereotype.Service;

@Service
public class ParkingSpotServiceImpl extends ServiceImpl<ParkingSpotMapper, ParkingSpot> implements ParkingSpotService {
}
