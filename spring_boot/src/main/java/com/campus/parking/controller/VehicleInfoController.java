package com.campus.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.parking.common.Result;
import com.campus.parking.entity.ParkingArea;
import com.campus.parking.entity.ParkingSpot;
import com.campus.parking.entity.VehicleInfo;
import com.campus.parking.service.ParkingAreaService;
import com.campus.parking.service.ParkingSpotService;
import com.campus.parking.service.VehicleInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleInfoController {

    @Autowired
    private VehicleInfoService vehicleInfoService;

    @Autowired
    private ParkingSpotService parkingSpotService;

    @Autowired
    private ParkingAreaService parkingAreaService;

    // 1. 添加车辆
    @PostMapping
    public Result addVehicle(@RequestBody VehicleInfo vehicleInfo) {
        // 可以在这里加一些校验，比如车牌号不能为空
        boolean saved = vehicleInfoService.save(vehicleInfo);
        return saved ? Result.success("添加成功") : Result.error("添加失败");
    }

    // 2. 根据ID删除车辆
    @DeleteMapping("/{id}")
    public Result deleteVehicle(@PathVariable Long id) {
        boolean removed = vehicleInfoService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    // 3. 修改车辆信息
    @PutMapping
    public Result updateVehicle(@RequestBody VehicleInfo vehicleInfo) {
        boolean updated = vehicleInfoService.updateById(vehicleInfo);
        return updated ? Result.success("修改成功") : Result.error("修改失败");
    }

    // 4. 根据ID查询车辆
    @GetMapping("/{id}")
    public Result getVehicleById(@PathVariable Long id) {
        VehicleInfo vehicle = vehicleInfoService.getById(id);
        return vehicle != null ? Result.success(vehicle) : Result.error("未找到该车辆");
    }

    // 5. 分页查询车辆列表（支持按车牌号模糊搜索）
    // currentPage: 当前页码, pageSize: 每页条数, plateNumber: 搜索关键字
    @GetMapping("/list")
    public Result getVehicleList(@RequestParam(defaultValue = "1") Integer currentPage,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(required = false) String plateNumber) {
        // 创建分页对象
        Page<VehicleInfo> page = new Page<>(currentPage, pageSize);
        // 创建条件构造器
        LambdaQueryWrapper<VehicleInfo> wrapper = new LambdaQueryWrapper<>();

        // 如果传了车牌号，就进行模糊查询
        if (plateNumber != null && !plateNumber.trim().isEmpty()) {
            wrapper.like(VehicleInfo::getPlateNumber, plateNumber);
        }
        // 按创建时间倒序排列
        wrapper.orderByDesc(VehicleInfo::getCreateTime);

        // 执行查询
        vehicleInfoService.page(page, wrapper);

        // 返回分页数据（包含 total 总记录数, records 当前页数据列表）
        return Result.success(page);
    }

    // 6. 查询当前在场车辆（parkingStatus=1）
    @GetMapping("/parked")
    public Result getParkedVehicles() {
        List<VehicleInfo> vehicles = vehicleInfoService.lambdaQuery()
                .eq(VehicleInfo::getParkingStatus, 1)
                .orderByDesc(VehicleInfo::getCreateTime)
                .list();

        if (vehicles.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        // 收集所有车位ID，批量查询车位信息
        Set<Long> spotIds = vehicles.stream()
                .map(VehicleInfo::getCurrentSpotId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, ParkingSpot> spotMap = new HashMap<>();
        if (!spotIds.isEmpty()) {
            List<ParkingSpot> spots = parkingSpotService.listByIds(spotIds);
            spotMap = spots.stream().collect(Collectors.toMap(ParkingSpot::getId, s -> s));
        }

        // 收集区域ID，批量查询区域名称
        Set<Long> areaIds = spotMap.values().stream()
                .map(ParkingSpot::getAreaId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, String> areaNameMap = new HashMap<>();
        if (!areaIds.isEmpty()) {
            List<ParkingArea> areas = parkingAreaService.listByIds(areaIds);
            areaNameMap = areas.stream().collect(Collectors.toMap(ParkingArea::getId, ParkingArea::getAreaName));
        }

        // 组装返回数据
        List<Map<String, Object>> result = new ArrayList<>();
        for (VehicleInfo v : vehicles) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", v.getId());
            item.put("plateNumber", v.getPlateNumber());
            item.put("ownerName", v.getOwnerName());
            item.put("vehicleType", v.getVehicleType());

            ParkingSpot spot = v.getCurrentSpotId() != null ? spotMap.get(v.getCurrentSpotId()) : null;
            if (spot != null) {
                item.put("spotNumber", spot.getSpotNumber());
                item.put("areaName", areaNameMap.getOrDefault(spot.getAreaId(), ""));
            }
            result.add(item);
        }

        return Result.success(result);
    }
}
