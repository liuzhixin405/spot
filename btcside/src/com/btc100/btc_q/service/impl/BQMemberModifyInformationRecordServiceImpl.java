package com.btc100.btc_q.service.impl;

import com.btc100.btc_q.dao.BQMemberModifyInformationRecordDao;
import com.btc100.btc_q.entity.BQMemberModifyInformationRecord;
import com.btc100.btc_q.service.BQMemberModifyInformationRecordService;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("BQMemberModifyInformationRecordService")
public class BQMemberModifyInformationRecordServiceImpl implements BQMemberModifyInformationRecordService {

    @Autowired
    private BQMemberModifyInformationRecordDao<BQMemberModifyInformationRecord> bqMemberModifyInformationRecordDao;

    @Override
    public void save(BQMemberModifyInformationRecord modifyInformationRecord) {
        bqMemberModifyInformationRecordDao.save(modifyInformationRecord);
    }

    @Override
    public PagerModel readPagerModel(long uid, int type, String modifyUser, String name, int opertionType) {
        Map<String, Object> params = new HashMap<>();
        String hql = "from BQMemberModifyInformationRecord where 1=1 ";
        if (uid > 0) {
            hql += " and f_user_id = :uid";
            params.put("uid", uid);
        }
        if (type > 0) {
            hql += " and f_information_type = :type";
            params.put("type", type);
        }
        if (!StringUtil.isEmpty(modifyUser)) {
            hql += " and f_modify_user = :modifyUser";
            params.put("modifyUser", modifyUser);
        }
        if (opertionType > 0) {
            hql += " and f_operation_type = :opertionType";
            params.put("opertionType", opertionType);
        }
        if (!StringUtil.isEmpty(name)) {
            hql += " and (f_old_information = :name or f_new_information = :name)";
            params.put("name", name);
        }
        hql += " order by f_modify_time desc";
        return bqMemberModifyInformationRecordDao.searchHql(hql, params);
    }

    @Override
    public List<BQMemberModifyInformationRecord> readByIdAndType(long uid, int type, String modifyUser, String name) {
        Session session = bqMemberModifyInformationRecordDao.getSession();
        Map<String, Object> params = new HashMap<>();
        String sql = "select * from bq_member_modify_information_record where 1=1 ";
        if (uid > 0) {
            sql += " and f_user_id = :uid";
            params.put("uid", uid);
        }
        if (type > 0) {
            sql += " and f_information_type = :type";
            params.put("type", type);
        }
        if (!StringUtil.isEmpty(modifyUser)) {
            sql += " and f_modify_user = :modifyUser";
            params.put("modifyUser", modifyUser);
        }
        if (!StringUtil.isEmpty(name)) {
            sql += " and (f_old_information = :name or f_new_information = :name)";
            params.put("name", name);
        }
        sql += " order by f_modify_time desc";
        SQLQuery query = session.createSQLQuery(sql);
        query.addEntity(BQMemberModifyInformationRecord.class);
        return query.list();
    }
}
