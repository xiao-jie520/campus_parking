package com.campus.parking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.campus.parking.mapper") // 扫描 Mapper 接口
public class CampusParkingApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusParkingApplication.class, args);
        System.out.println("=====================================");
        System.out.println("校园车辆管理系统服务端端启动成功！");
        System.out.println("=====================================");
    }
}
