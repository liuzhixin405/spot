package com.btc100.btc_q.timerTask;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.entity.*;
import com.btc100.btc_q.service.*;
import com.btc100.btc_q.util.TimeUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;

public class TimerTaskStatisticsFee {
    private static final Logger logger = Logger.getLogger(TimerTaskStatsAccount.class);

    @Autowired
    private BQMemberDrawService bqMemberDrawService;
    @Autowired
    private SpyMemcachedClient spyMemcachedClient;
    @Autowired
    private BQTransactionService transactionService;
    @Autowired
    protected NTransactionService nTransactionService;
    @Autowired
    private TradeLogService tradeLogService;
    @Autowired
    private NTradeLogService nTradeLogService;


    public void run() {
        try {
            Calendar time = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
            time.set(Calendar.MINUTE, 0);
            time.set(Calendar.SECOND, 0);
            time.set(Calendar.MILLISECOND, 0);
            long nowHourTime = time.getTimeInMillis() / 1000;//获取当前小时时间戳
            long upHourTime = nowHourTime - 3600;

            Set<Long> uids = new HashSet<>();

            List<BQTransaction> transactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST);
            if (transactionList == null) {
                transactionList = transactionService.findTransByState_new(Contents.TRANSACTION_STATE_ZC);
                spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST, BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, transactionList);
            }
            Map<Long, BigDecimal> sumFeeT = new HashMap<>();
            for (BQTransaction bqTransaction : transactionList) {
                List<TradeLog> trades = tradeLogService.findUidByCodeAndTimeGroupUid(bqTransaction.getTransaction_code(), upHourTime, nowHourTime);
                if (trades.size() <= 0) continue;

                for (TradeLog tradeLog : trades) {
                    if (sumFeeT.get(tradeLog.getF_user_id()) == null) {
                        sumFeeT.put(tradeLog.getF_user_id(), tradeLog.getTrade_count() == null ? BigDecimal.ZERO : tradeLog.getTrade_count());
                    } else {
                        sumFeeT.put(tradeLog.getF_user_id(), (tradeLog.getTrade_count() == null ? BigDecimal.ZERO : tradeLog.getTrade_count()).add(sumFeeT.get(tradeLog.getF_user_id())));
                    }
                    uids.add(tradeLog.getF_user_id());
                }
            }

            List<NTransaction> nTransactionList = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTIONLIST + "_usdr");
            if (nTransactionList == null) {
                nTransactionList = nTransactionService.findTransByState_new("usdr", Contents.TRANSACTION_STATE_ZC);
                spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTIONLIST + "_usdr", BTCQMemecacheKeys.TIMES.TRANSACTIONLIST, nTransactionList);
            }
            Map<Long, BigDecimal> sumFeeU = new HashMap<>();
            for (NTransaction nTransaction: nTransactionList) {
                List<NTradeLog> nTradeLogs = nTradeLogService.findUidByCodeAndTimeGroupUid("usdr", nTransaction.getTransaction_code(), upHourTime, nowHourTime);
                if (nTradeLogs.size() <= 0) continue;
                for (NTradeLog nTradeLog : nTradeLogs) {
                    if (sumFeeU.get(nTradeLog.getF_user_id()) == null) {
                        sumFeeU.put(nTradeLog.getF_user_id(), nTradeLog.getTrade_count() == null ? BigDecimal.ZERO : nTradeLog.getTrade_count());
                    } else {
                        sumFeeU.put(nTradeLog.getF_user_id(), (nTradeLog.getTrade_count() == null ? BigDecimal.ZERO : nTradeLog.getTrade_count()).add(sumFeeU.get(nTradeLog.getF_user_id())));
                    }
                    uids.add(nTradeLog.getF_user_id());
                }
            }

            for (Long userI : uids) {
                BQMemberDraw bqMemberDraw = bqMemberDrawService.findById(userI);
                if (null == bqMemberDraw) {
                    bqMemberDraw = new BQMemberDraw();
                    bqMemberDraw.setF_user_id(userI);
                    bqMemberDraw.setF_draw_count(0);
                    bqMemberDraw.setF_already_draw_count(0);
                    bqMemberDraw.setF_produce_fee_usdr(BigDecimal.ZERO);
                    bqMemberDraw.setF_produce_fee_usdt(BigDecimal.ZERO);
                    bqMemberDraw.setF_surplus_fee(BigDecimal.ZERO);
                    bqMemberDrawService.save(bqMemberDraw);
                }

                BigDecimal usdtCountFee = BigDecimal.ZERO;
                BigDecimal usdrCountfee = BigDecimal.ZERO;
                BigDecimal countFee = BigDecimal.ZERO;

                usdtCountFee = usdtCountFee.add(sumFeeT.get(userI) == null ? BigDecimal.ZERO : sumFeeT.get(userI));
                usdrCountfee = usdrCountfee.add(sumFeeU.get(userI) == null ? BigDecimal.ZERO : sumFeeU.get(userI));
                countFee = countFee.add(usdtCountFee).add(usdrCountfee);

                double allFee = countFee.doubleValue() + bqMemberDraw.getF_surplus_fee().doubleValue();
                int count = (int) (allFee / 20);
                double surplusFee = allFee % 20;

                bqMemberDraw.setF_draw_count(bqMemberDraw.getF_draw_count() + count);
                bqMemberDraw.setF_produce_fee_usdr(bqMemberDraw.getF_produce_fee_usdr().add(usdrCountfee));
                bqMemberDraw.setF_produce_fee_usdt(bqMemberDraw.getF_produce_fee_usdt().add(usdtCountFee));
                bqMemberDraw.setF_surplus_fee(new BigDecimal(surplusFee));
                bqMemberDrawService.update(bqMemberDraw);
            }

            if (TimeUtil.getTimesmorning() == nowHourTime) {
                bqMemberDrawService.resetAllFee();
            }

        } catch (Exception e) {
            logger.info("每小时统计用户手续费异常。。。。");
            e.printStackTrace();
        }

    }
}
