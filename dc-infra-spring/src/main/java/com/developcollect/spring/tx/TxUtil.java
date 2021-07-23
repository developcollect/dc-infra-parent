package com.developcollect.spring.tx;


import com.developcollect.spring.SpringUtil;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 事务工具类
 */
public class TxUtil {

    private static volatile PlatformTransactionManager platformTransactionManager;

    public static PlatformTransactionManager getPlatformTransactionManager() {
        if (platformTransactionManager == null) {
            synchronized (TxUtil.class) {
                if (platformTransactionManager == null) {
                    platformTransactionManager =  SpringUtil.getBean(PlatformTransactionManager.class);
                }
            }
        }

        return platformTransactionManager;
    }

    /**
     * 开启一个新的事务
     */
    public static TransactionStatus newTx() {
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus txStatus = getPlatformTransactionManager().getTransaction(transactionDefinition);
        return txStatus;
    }

    public static void commit(TransactionStatus txStatus) {
        getPlatformTransactionManager().commit(txStatus);
    }

    public static void rollback(TransactionStatus txStatus) {
        getPlatformTransactionManager().commit(txStatus);
    }
}
