package com.btc100.btc_q.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;
import com.btc100.btc_q.dao.BQMemberSettleDao;
import com.btc100.btc_q.entity.BQMemberSettle;

@Repository("bqMemberSettleDao")
public class BQMemberSettleDaoImpl<T> extends BaseDaoImpl<T> implements BQMemberSettleDao<T> {
	public long insertLog(final BQMemberSettle t){
		getSession().doWork(new Work() {
			public void execute(Connection con) throws SQLException {
				PreparedStatement psmt = con.prepareStatement(
								"insert into bq_member_settle(transaction_code,f_user_id,f_settle_time,f_settle_price,f_settle_amount,f_settle_gain,f_settle_loss,f_settle_apportion)"
										+ "values (?,?,?,?,?,?,?,?)",
								Statement.RETURN_GENERATED_KEYS);
				psmt.setString(1, t.getTransaction_code());
				psmt.setLong(2, t.getF_user_id());
				psmt.setLong(3, t.getF_settle_time());
				psmt.setBigDecimal(4, t.getF_settle_price());
				psmt.setBigDecimal(5, t.getF_settle_amount());
				psmt.setBigDecimal(6, t.getF_settle_gain());
				psmt.setBigDecimal(7, t.getF_settle_loss());
				psmt.setBigDecimal(8, t.getF_settle_apportion());
				psmt.executeUpdate();
				ResultSet results = psmt.getGeneratedKeys();
				long num = -1;
				if (results.next()) {
					num = results.getInt(1);
				}

				t.setId(num);

			}

		});

		return t.getId();
	}
}
