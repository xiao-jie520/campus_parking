package com.campus.parking.controller;

import com.campus.parking.common.Result;
import com.campus.parking.entity.AccessRecord;
import com.campus.parking.entity.ParkingArea;
import com.campus.parking.entity.VehicleInfo;
import com.campus.parking.service.AccessRecordService;
import com.campus.parking.service.ParkingAreaService;
import com.campus.parking.service.VehicleInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private AccessRecordService accessRecordService;

    @Autowired
    private VehicleInfoService vehicleInfoService;

    @Autowired
    private ParkingAreaService parkingAreaService;

    @GetMapping("/today")
    public Result getTodayStatistics() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 今日入场数
        long todayIn = accessRecordService.lambdaQuery()
                .eq(AccessRecord::getAccessType, "IN")
                .between(AccessRecord::getAccessTime, todayStart, todayEnd)
                .count();

        // 今日出场数
        long todayOut = accessRecordService.lambdaQuery()
                .eq(AccessRecord::getAccessType, "OUT")
                .between(AccessRecord::getAccessTime, todayStart, todayEnd)
                .count();

        // 当前在场车辆数
        long currentParked = vehicleInfoService.lambdaQuery()
                .eq(VehicleInfo::getParkingStatus, 1)
                .count();

        // 总注册车辆数
        long totalVehicles = vehicleInfoService.count();

        Map<String, Object> data = new HashMap<>();
        data.put("todayIn", todayIn);
        data.put("todayOut", todayOut);
        data.put("currentParked", currentParked);
        data.put("totalVehicles", totalVehicles);
        return Result.success(data);
    }

    @GetMapping("/area")
    public Result getAreaStatistics() {
        List<ParkingArea> areas = parkingAreaService.lambdaQuery()
                .eq(ParkingArea::getStatus, 1)
                .orderByAsc(ParkingArea::getId)
                .list();
        return Result.success(areas);
    }
}
