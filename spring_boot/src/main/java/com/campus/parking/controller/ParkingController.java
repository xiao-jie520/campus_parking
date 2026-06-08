package com.campus.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.parking.common.Result;
import com.campus.parking.config.RequireRole;
import com.campus.parking.entity.ParkingArea;
import com.campus.parking.entity.ParkingSpot;
import com.campus.parking.entity.VehicleInfo;
import com.campus.parking.service.ParkingAreaService;
import com.campus.parking.service.ParkingSpotService;
import com.campus.parking.service.VehicleInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    @Autowired
    private ParkingAreaService parkingAreaService;

    @Autowired
    private ParkingSpotService parkingSpotService;

    @Autowired
    private VehicleInfoService vehicleInfoService;

    // ==================== 区域管理 ====================

    @GetMapping("/area/list")
    public Result getAreaList() {
        List<ParkingArea> list = parkingAreaService.lambdaQuery()
                .eq(ParkingArea::getStatus, 1)
                .orderByAsc(ParkingArea::getId)
                .list();
        return Result.success(list);
    }

    @PostMapping("/area")
    @RequireRole("ADMIN")
    public Result addArea(@RequestBody ParkingArea area) {
        area.setAvailableSpaces(area.getTotalSpaces());
        area.setStatus(1);
        area.setCreateTime(LocalDateTime.now());
        area.setUpdateTime(LocalDateTime.now());
        boolean saved = parkingAreaService.save(area);
        return saved ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/area")
    @RequireRole("ADMIN")
    public Result updateArea(@RequestBody ParkingArea area) {
        area.setUpdateTime(LocalDateTime.now());
        boolean updated = parkingAreaService.updateById(area);
        return updated ? Result.success("修改成功") : Result.error("修改失败");
    }

    @DeleteMapping("/area/{id}")
    @RequireRole("ADMIN")
    public Result deleteArea(@PathVariable Long id) {
        // 检查区域下是否有车位被占用
        long occupied = parkingSpotService.lambdaQuery()
                .eq(ParkingSpot::getAreaId, id)
                .eq(ParkingSpot::getStatus, 1)
                .count();
        if (occupied > 0) {
            return Result.error("该区域下还有车辆停放，无法删除");
        }
        // 删除区域下所有车位
        parkingSpotService.lambdaUpdate().eq(ParkingSpot::getAreaId, id).remove();
        boolean removed = parkingAreaService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    // ==================== 车位管理 ====================

    @GetMapping("/spot/list")
    public Result getSpotList(@RequestParam Long areaId) {
        List<ParkingSpot> list = parkingSpotService.lambdaQuery()
                .eq(ParkingSpot::getAreaId, areaId)
                .orderByAsc(ParkingSpot::getSpotNumber)
                .list();
        return Result.success(list);
    }

    @PostMapping("/spot")
    @RequireRole("ADMIN")
    public Result addSpot(@RequestBody ParkingSpot spot) {
        spot.setStatus(0);
        spot.setCreateTime(LocalDateTime.now());
        spot.setUpdateTime(LocalDateTime.now());
        boolean saved = parkingSpotService.save(spot);
        // 更新区域总车位数和剩余车位
        if (saved) {
            refreshAreaSpaces(spot.getAreaId());
        }
        return saved ? Result.success("添加成功") : Result.error("添加失败");
    }

    @DeleteMapping("/spot/{id}")
    @RequireRole("ADMIN")
    public Result deleteSpot(@PathVariable Long id) {
        ParkingSpot spot = parkingSpotService.getById(id);
        if (spot == null) return Result.error("车位不存在");
        if (spot.getStatus() == 1) return Result.error("该车位已被占用，无法删除");

        Long areaId = spot.getAreaId();
        boolean removed = parkingSpotService.removeById(id);
        if (removed) {
            refreshAreaSpaces(areaId);
        }
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    // 分配车位（入场时调用）
    @PostMapping("/spot/assign")
    public Result assignSpot(@RequestParam Long areaId, @RequestParam Long vehicleId, @RequestParam String plateNumber) {
        // 找一个空闲车位
        ParkingSpot spot = parkingSpotService.lambdaQuery()
                .eq(ParkingSpot::getAreaId, areaId)
                .eq(ParkingSpot::getStatus, 0)
                .orderByAsc(ParkingSpot::getSpotNumber)
                .last("LIMIT 1")
                .one();

        if (spot == null) {
            return Result.error("该区域已无空闲车位");
        }

        // 标记为占用
        spot.setStatus(1);
        spot.setVehicleId(vehicleId);
        spot.setPlateNumber(plateNumber);
        spot.setUpdateTime(LocalDateTime.now());
        parkingSpotService.updateById(spot);

        // 更新车辆的当前车位
        VehicleInfo vehicle = new VehicleInfo();
        vehicle.setId(vehicleId);
        vehicle.setCurrentSpotId(spot.getId());
        vehicleInfoService.updateById(vehicle);

        // 刷新区域剩余车位
        refreshAreaSpaces(areaId);

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("spotId", spot.getId());
        data.put("spotNumber", spot.getSpotNumber());
        return Result.success("车位分配成功", data);
    }

    // 释放车位（出场时调用）
    @PostMapping("/spot/release")
    public Result releaseSpot(@RequestParam Long spotId) {
        ParkingSpot spot = parkingSpotService.getById(spotId);
        if (spot == null) return Result.error("车位不存在");

        Long areaId = spot.getAreaId();
        spot.setStatus(0);
        spot.setVehicleId(null);
        spot.setPlateNumber(null);
        spot.setUpdateTime(LocalDateTime.now());
        parkingSpotService.updateById(spot);

        refreshAreaSpaces(areaId);
        return Result.success("车位释放成功");
    }

    // 根据车牌号释放车位
    @PostMapping("/spot/release-by-plate")
    public Result releaseSpotByPlate(@RequestParam String plateNumber) {
        ParkingSpot spot = parkingSpotService.lambdaQuery()
                .eq(ParkingSpot::getPlateNumber, plateNumber)
                .eq(ParkingSpot::getStatus, 1)
                .one();
        if (spot == null) {
            return Result.success("该车辆未占用车位");
        }

        Long areaId = spot.getAreaId();
        spot.setStatus(0);
        spot.setVehicleId(null);
        spot.setPlateNumber(null);
        spot.setUpdateTime(LocalDateTime.now());
        parkingSpotService.updateById(spot);

        refreshAreaSpaces(areaId);
        return Result.success("车位释放成功");
    }

    // 刷新区域的剩余车位数
    private void refreshAreaSpaces(Long areaId) {
        long total = parkingSpotService.lambdaQuery()
                .eq(ParkingSpot::getAreaId, areaId)
                .count();
        long occupied = parkingSpotService.lambdaQuery()
                .eq(ParkingSpot::getAreaId, areaId)
                .eq(ParkingSpot::getStatus, 1)
                .count();

        ParkingArea area = new ParkingArea();
        area.setId(areaId);
        area.setTotalSpaces((int) total);
        area.setAvailableSpaces((int) (total - occupied));
        area.setUpdateTime(LocalDateTime.now());
        parkingAreaService.updateById(area);
    }
}
