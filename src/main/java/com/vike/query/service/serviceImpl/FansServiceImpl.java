package com.vike.query.service.serviceImpl;


import com.vike.query.dao.FansInfoRepository;
import com.vike.query.dao.FansRepository;
import com.vike.query.entity.Fans;
import com.vike.query.entity.FansInfo;
import com.vike.query.service.FansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/10/23
 */
@Service
public class FansServiceImpl implements FansService {

    @Autowired
    FansRepository fansRepository;
    @Autowired
    FansInfoRepository fansInfoRepository;

    @Override
    public Fans saveFans(Fans fans) {
        return fansRepository.save(fans);
    }

    @Override
    public Optional<Fans> findByOpenId(String openId) {
        return fansRepository.findFansByOpenId(openId);
    }

    @Override
    public FansInfo saveFansInfo(FansInfo fansInfo) {
        return fansInfoRepository.save(fansInfo);
    }

    @Override
    public Optional<FansInfo> findByFinsId(long fansId) {
        return fansInfoRepository.findFansInfoByFansId(fansId);
    }
}
