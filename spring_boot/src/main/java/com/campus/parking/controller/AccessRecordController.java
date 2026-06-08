package com.campus.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.parking.common.Result;
import com.campus.parking.entity.AccessRecord;
import com.campus.parking.entity.ParkingArea;
import com.campus.parking.entity.ParkingSpot;
import com.campus.parking.entity.VehicleInfo;
import com.campus.parking.service.AccessRecordService;
import com.campus.parking.service.ParkingAreaService;
import com.campus.parking.service.ParkingSpotService;
import com.campus.parking.service.VehicleInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/access")
public class AccessRecordController {

    @Autowired
    private AccessRecordService accessRecordService;

    @Autowired
    private VehicleInfoService vehicleInfoService;

    @Autowired
    private ParkingAreaService parkingAreaService;

    @Autowired
    private ParkingSpotService parkingSpotService;

    // 车辆入场/出场登记
    @PostMapping("/record")
    public Result recordAccess(@RequestBody AccessRecord record, HttpServletRequest request) {
        String plateNumber = record.getPlateNumber();
        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            return Result.error("车牌号不能为空");
        }

        // 查找车辆
        VehicleInfo vehicle = vehicleInfoService.lambdaQuery()
                .eq(VehicleInfo::getPlateNumber, plateNumber.trim())
                .one();

        if (vehicle == null) {
            return Result.error("该车牌号未登记，请先注册车辆信息");
        }

        // 检查黑名单
        if (vehicle.getStatus() == 0) {
            return Result.error("该车辆已被加入黑名单，禁止入场");
        }

        String accessType = record.getAccessType();

        // ========== 入场逻辑 ==========
        if ("IN".equals(accessType)) {
            if (vehicle.getParkingStatus() != null && vehicle.getParkingStatus() == 1) {
                return Result.error("该车辆已在场内，无法重复入场");
            }

            // 自动分配车位：找一个有空闲车位的区域
            ParkingSpot assignedSpot = null;
            ParkingArea assignedArea = null;

            List<ParkingArea> areas = parkingAreaService.lambdaQuery()
                    .eq(ParkingArea::getStatus, 1)
                    .gt(ParkingArea::getAvailableSpaces, 0)
                    .orderByAsc(ParkingArea::getId)
                    .list();

            for (ParkingArea area : areas) {
                ParkingSpot spot = parkingSpotService.lambdaQuery()
                        .eq(ParkingSpot::getAreaId, area.getId())
                        .eq(ParkingSpot::getStatus, 0)
                        .orderByAsc(ParkingSpot::getSpotNumber)
                        .last("LIMIT 1")
                        .one();
                if (spot != null) {
                    assignedSpot = spot;
                    assignedArea = area;
                    break;
                }
            }

            if (assignedSpot == null) {
                return Result.error("所有区域已满，无空闲车位");
            }

            // 标记车位为占用
            assignedSpot.setStatus(1);
            assignedSpot.setVehicleId(vehicle.getId());
            assignedSpot.setPlateNumber(plateNumber.trim());
            assignedSpot.setUpdateTime(LocalDateTime.now());
            parkingSpotService.updateById(assignedSpot);

            // 刷新区域剩余车位
            refreshAreaSpaces(assignedArea.getId());

            // 更新车辆状态
            vehicle.setParkingStatus(1);
            vehicle.setCurrentSpotId(assignedSpot.getId());
            vehicleInfoService.updateById(vehicle);

            // 写入出入记录
            record.setVehicleId(vehicle.getId());
            record.setSpotId(assignedSpot.getId());
            record.setSpotNumber(assignedSpot.getSpotNumber());
            record.setAreaName(assignedArea.getAreaName());
            record.setAccessTime(LocalDateTime.now());
            record.setOperatorId((Long) request.getAttribute("userId"));
            record.setOperatorName((String) request.getAttribute("username"));
            accessRecordService.save(record);

            // 返回分配结果
            Map<String, Object> data = new HashMap<>();
            data.put("spotNumber", assignedSpot.getSpotNumber());
            data.put("areaName", assignedArea.getAreaName());
            data.put("accessRecord", record);
            return Result.success("入场成功，已分配车位", data);
        }

        // ========== 出场逻辑 ==========
        if ("OUT".equals(accessType)) {
            if (vehicle.getParkingStatus() == null || vehicle.getParkingStatus() == 0) {
                return Result.error("该车辆不在场内，无法出场");
            }

            // 释放车位
            Long spotId = vehicle.getCurrentSpotId();
            if (spotId != null) {
                ParkingSpot spot = parkingSpotService.getById(spotId);
                if (spot != null) {
                    spot.setStatus(0);
                    spot.setVehicleId(null);
                    spot.setPlateNumber(null);
                    spot.setUpdateTime(LocalDateTime.now());
                    parkingSpotService.updateById(spot);
                    refreshAreaSpaces(spot.getAreaId());

                    record.setSpotId(spot.getId());
                    record.setSpotNumber(spot.getSpotNumber());

                    ParkingArea area = parkingAreaService.getById(spot.getAreaId());
                    if (area != null) {
                        record.setAreaName(area.getAreaName());
                    }
                }
            }

            // 更新车辆状态
            vehicle.setParkingStatus(0);
            vehicle.setCurrentSpotId(null);
            vehicleInfoService.updateById(vehicle);

            // 写入出入记录
            record.setVehicleId(vehicle.getId());
            record.setAccessTime(LocalDateTime.now());
            record.setOperatorId((Long) request.getAttribute("userId"));
            record.setOperatorName((String) request.getAttribute("username"));
            accessRecordService.save(record);

            return Result.success("出场成功，车位已释放");
        }

        return Result.error("无效的出入类型");
    }

    // 查询最近一次记录
    @GetMapping("/latest/{plateNumber}")
    public Result getLatestRecord(@PathVariable String plateNumber) {
        AccessRecord latest = accessRecordService.lambdaQuery()
                .eq(AccessRecord::getPlateNumber, plateNumber)
                .orderByDesc(AccessRecord::getAccessTime)
                .last("LIMIT 1")
                .one();

        VehicleInfo vehicle = vehicleInfoService.lambdaQuery()
                .eq(VehicleInfo::getPlateNumber, plateNumber)
                .one();

        Map<String, Object> data = new HashMap<>();
        data.put("latestRecord", latest);
        data.put("vehicle", vehicle);
        return Result.success(data);
    }

    // 分页查询出入记录
    @GetMapping("/record/list")
    public Result getRecordList(@RequestParam(defaultValue = "1") Integer currentPage,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(required = false) String plateNumber,
                                @RequestParam(required = false) String accessType) {
        Page<AccessRecord> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<AccessRecord> wrapper = new LambdaQueryWrapper<>();

        if (plateNumber != null && !plateNumber.trim().isEmpty()) {
            wrapper.like(AccessRecord::getPlateNumber, plateNumber.trim());
        }
        if (accessType != null && !accessType.trim().isEmpty()) {
            wrapper.eq(AccessRecord::getAccessType, accessType.trim());
        }
        wrapper.orderByDesc(AccessRecord::getAccessTime);

        accessRecordService.page(page, wrapper);
        return Result.success(page);
    }

    private void refreshAreaSpaces(Long areaId) {
        long total = parkingSpotService.lambdaQuery()
                .eq(ParkingSpot::getAreaId, areaId).count();
        long occupied = parkingSpotService.lambdaQuery()
                .eq(ParkingSpot::getAreaId, areaId)
                .eq(ParkingSpot::getStatus, 1).count();

        ParkingArea area = new ParkingArea();
        area.setId(areaId);
        area.setTotalSpaces((int) total);
        area.setAvailableSpaces((int) (total - occupied));
        area.setUpdateTime(LocalDateTime.now());
        parkingAreaService.updateById(area);
    }
}
