package com.btc100.btc_q.service.impl;

import com.btc100.btc_q.dao.BQMemberDrawDao;
import com.btc100.btc_q.entity.BQMemberDraw;
import com.btc100.btc_q.service.BQMemberDrawService;
import com.btc100.btc_q.vo.PagerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("bqMemberDrawService")
public class BQMemberDrawServiceImpl implements BQMemberDrawService {

    @Autowired
    private BQMemberDrawDao<BQMemberDraw> bqMemberDrawDao;

    @Override
    public BQMemberDraw readById(long id) {
        String hql = "From BQMemberDraw WHERE f_user_id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return bqMemberDrawDao.get(hql, params);
    }

    @Override
    public BQMemberDraw findById(long id) {
        String hql = "From BQMemberDraw WHERE f_user_id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return bqMemberDrawDao.get(hql, params);
    }

    @Override
    public void update(BQMemberDraw bqMemberDraw) {
        bqMemberDrawDao.update(bqMemberDraw);
    }

    @Override
    public void save(BQMemberDraw bqMemberDraw) {
        bqMemberDrawDao.save(bqMemberDraw);
    }

    @Override
    public int resetAllFee() {
        Map<String, Object> params = new HashMap<>();
        params.put("f_produce_fee_usdt", BigDecimal.ZERO);
        params.put("f_produce_fee_usdr", BigDecimal.ZERO);
        params.put("f_surplus_fee", BigDecimal.ZERO);
        String hql = "update BQMemberDraw set f_produce_fee_usdt = :f_produce_fee_usdt,f_produce_fee_usdr = :f_produce_fee_usdr,f_surplus_fee = :f_surplus_fee";
        return bqMemberDrawDao.updateCondition(hql, params);
    }

    @Override
    public List<Long> findIds() {
        String hql = "select b.f_user_id from BQMemberDraw b";
        return bqMemberDrawDao.createQuery(hql).list();
    }

    @Override
    public PagerModel readModel(long uid) {
        StringBuilder hql = new StringBuilder("From BQMemberDraw WHERE 1=1 ");
        Map<String, Object> map = new HashMap<>();
        if (uid > 0) {
            hql.append(" AND f_user_id = :uid ");
            map.put("uid", uid);
        }
        hql.append(" order by f_draw_count DESC");
        return bqMemberDrawDao.searchHql(hql.toString(), map);
    }
}
