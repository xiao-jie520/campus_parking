package com.campus.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.parking.entity.AccessRecord;
import com.campus.parking.mapper.AccessRecordMapper;
import com.campus.parking.service.AccessRecordService;
import org.springframework.stereotype.Service;

@Service
public class AccessRecordServiceImpl extends ServiceImpl<AccessRecordMapper, AccessRecord> implements AccessRecordService {
}
