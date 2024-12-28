package com.btc100.btc_q.service.impl;

import com.btc100.btc_q.common.UrlConfig;
import com.btc100.btc_q.dao.*;
import com.btc100.btc_q.entity.*;
import com.btc100.btc_q.exception.ServiceException;
import com.btc100.btc_q.service.BQLuckDrawSettingService;
import com.btc100.btc_q.util.RandomUtil;
import com.btc100.btc_q.util.SendMail;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("bqLuckDrawSetting")
public class BQLuckDrawSettingServiceImpl implements BQLuckDrawSettingService {

    @Autowired
    private BQAccountDao<BQAccount> bqAccountDao;
    @Autowired
    private BQCurrencyTypeDao<BQCurrencyType> bqCurrencyTypeDao;
    @Autowired
    private BQAccountAmountLogDao<BQAccountAmountLog> bqAccountAmountLogDao;
    @Autowired
    private BQLuckDrawSettingDao<BQLuckDrawSetting> bqLuckDrawSettingDao;
    @Autowired
    private BQLuckObtainRecordDao<BQLuckObtainRecord> bqLuckObtainRecordDao;
    @Autowired
    private BQLuckDrawRecordDao<BQLuckDrawRecord> bqLuckDrawRecordDao;
    @Autowired
    private BQMemberDrawDao<BQMemberDraw> bqMemberDrawDao;
    @Autowired
    private BQAppointLuckDao<BQAppointLuck> bqAppointLuckDao;

    @Override
    public void updateCountByAward(BQLuckDrawSetting bqLuckDrawSetting, BQMemberDraw bqMemberDraw, BQAppointLuck bqAppointLuck, String email) throws ServiceException {
        BigDecimal prizeUsdt = bqLuckDrawSetting.getPrize_usdt();
        long userId = bqMemberDraw.getF_user_id();
        BQCurrencyType bqCurrencyType = bqCurrencyTypeDao.getByTypeName("USDT");

        Map<String, Object> params = new HashMap<>();
        params.put("f_user_id", userId);
        String hql = "update BQMemberDraw set f_already_draw_count = f_already_draw_count + 1 where f_user_id = :f_user_id AND f_draw_count-f_already_draw_count > 0";
        if (bqMemberDrawDao.updateCondition(hql, params) <= 0) {        //修改抽奖次数
            throw new ServiceException("用户抽奖异常，异常记录ID：" + userId);
        }

        if (!StringUtil.isEmpty(bqLuckDrawSetting.getPrize_matter())) {     //实物奖品

            final String mansgerEmail = UrlConfig.getValue("exchange.manualNotify.email");
            final String msg = "ID为: "+ userId + "的用户抽取到" + bqLuckDrawSetting.getName_zh_CN() + "，获得" + bqLuckDrawSetting.getPrize_matter();
            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        SendMail themail = new SendMail();
                        String content = msg + "，请登录xkd后台管理处理";
                        themail.send(mansgerEmail, content);
                    } catch (Exception e) {
                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();

        }

        if (prizeUsdt.compareTo(BigDecimal.ZERO) > 0) {      //usdt奖品

            BQAccount uaccount = bqAccountDao.getByUerAndCurrencyId(userId, bqCurrencyType.getType_id());
            if (bqAccountDao.updateAmount(userId, bqCurrencyType.getType_id(), prizeUsdt, BigDecimal.ZERO) <= 0) {
                throw new ServiceException("抽奖所得，增加可用处理异常，用户资金异常，抽奖记录ID：" + userId);
            }

            String title = userId + "抽到" + bqLuckDrawSetting.getName_zh_CN() + "获得" + prizeUsdt + "usdt，增加可用";
            BQAccountAmountLog pAccountLog = new BQAccountAmountLog(title, bqCurrencyType.getType_id(), 0, 1,
                    uaccount.getBtc(), prizeUsdt, uaccount.getBtc().add(prizeUsdt), uaccount.getBtc_locked(),
                    uaccount.getBtc_locked(),
                    bqAccountAmountLogDao.getUnix_TIMESTAMP(), userId, RandomUtil.getTransNo(),
                    "抽取获得，增加可用", 0);
            bqAccountAmountLogDao.save(pAccountLog);

        }

        //添加获奖记录
        if (!bqLuckDrawSetting.getPrize().equals("noprize")) {
            BQLuckObtainRecord bqLuckObtainRecord = new BQLuckObtainRecord();
            if (bqAppointLuck != null) {
                bqLuckObtainRecord.setRandom(bqAppointLuck.getNumber() == 0);       //是否随机获得
            } else {
                bqLuckObtainRecord.setRandom(false);
            }
            bqLuckObtainRecord.setUser_id(userId);
            bqLuckObtainRecord.setObtain_time(System.currentTimeMillis()/1000);
            bqLuckObtainRecord.setPrize_id(bqLuckDrawSetting.getId());
            bqLuckObtainRecord.setPrize_matter(bqLuckDrawSetting.getPrize_matter());
            bqLuckObtainRecord.setPrize_usdt(prizeUsdt);
            bqLuckObtainRecord.setUser_name(new StringBuilder(email).replace(3,7,"****").toString());
            bqLuckObtainRecord.setPrize_name_CN(bqLuckDrawSetting.getName_zh_CN());
            bqLuckObtainRecord.setPrize_name_US(bqLuckDrawSetting.getName_en_US());
            bqLuckObtainRecord.setNotes(bqLuckDrawSetting.getNotes());
            bqLuckObtainRecordDao.save(bqLuckObtainRecord);
        }

        //添加抽奖记录
        BQLuckDrawRecord bqLuckDrawRecord = new BQLuckDrawRecord();
        bqLuckDrawRecord.setPrize(bqLuckDrawSetting.getPrize());
        bqLuckDrawRecord.setDraw_time(System.currentTimeMillis()/1000);
        bqLuckDrawRecord.setPrize_name(StringUtil.isEmpty(bqLuckDrawSetting.getNotes()) ? "" : bqLuckDrawSetting.getNotes());
        bqLuckDrawRecord.setUser_id(userId);
        bqLuckDrawRecordDao.save(bqLuckDrawRecord);

        if (null != bqAppointLuck) {
            //变更指定记录
            if (bqAppointLuck.getNumber()  <= 0) {
                bqAppointLuckDao.delete(bqAppointLuck);
            } else {
                bqAppointLuckDao.updateNumByUid(userId);
            }
        }

    }

    @Override
    public void update(BQLuckDrawSetting bqLuckDrawSetting) {
        bqLuckDrawSettingDao.update(bqLuckDrawSetting);
    }

    @Override
    public List<BQLuckDrawSetting> readAllSettigs() {
        return bqLuckDrawSettingDao.find("from BQLuckDrawSetting");
    }

    @Override
    public BQLuckDrawSetting readByPrize(String prize) {
        String hql = "FROM BQLuckDrawSetting where prize = :prize";
        Map<String, Object> map = new HashMap<>();
        map.put("prize", prize);
        return bqLuckDrawSettingDao.get(hql, map);
    }

    @Override
    public BQLuckDrawSetting readById(int id) {
        String hql = "FROM BQLuckDrawSetting where id = :id";
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return bqLuckDrawSettingDao.get(hql, map);
    }

    @Override
    public PagerModel readModel() {
        return bqLuckDrawSettingDao.searchHql("from BQLuckDrawSetting");
    }

}
