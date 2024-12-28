package com.btc100.btc_q.dao.impl;

import com.btc100.btc_q.dao.BQIndexStateDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository("bqIndexStateDao")
public class BQIndexStateDaoImpl<T> extends BaseDaoImpl<T> implements BQIndexStateDao<T>  {

    @Override
    public int updateStateByName(String indexName, int state) {
        String indexState = "";
        if("okex_state".equals(indexName)){
            indexState = " okex_state=:state ";
        }else if ("huobi_state".equals(indexName)){
            indexState = " huobi_state=:state ";
        }else if ("binance_state".equals(indexName)){
            indexState = " binance_state=:state ";
        }else return 0;

        Map<String, Object> params = new HashMap<>();
        params.put("state", state);
        String hql = "update BQIndexState set " + indexState;
        return super.updateCondition(hql,params);
    }
    
}
