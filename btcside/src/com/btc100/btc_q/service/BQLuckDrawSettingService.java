package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.BQAppointLuck;
import com.btc100.btc_q.entity.BQLuckDrawSetting;
import com.btc100.btc_q.entity.BQMemberDraw;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.vo.PagerModel;

import java.util.List;

public interface BQLuckDrawSettingService {

    void updateCountByAward(BQLuckDrawSetting bqLuckDrawSetting, BQMemberDraw bqMemberDraw, BQAppointLuck bqAppointLuck, String email) throws ServiceException;

    void update(BQLuckDrawSetting bqLuckDrawSetting);
    /************************************ for read only **************************************/
    BQLuckDrawSetting readByPrize(String prize);

    BQLuckDrawSetting readById(int id);

    PagerModel readModel();

    List<BQLuckDrawSetting> readAllSettigs();

}
