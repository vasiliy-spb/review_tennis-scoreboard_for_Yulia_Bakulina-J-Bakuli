package dao;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Transaction;

@Slf4j
public class AbstractH2Dao {
    protected boolean isDuplicate(Exception e) {
        Throwable cause = e.getCause();
        return cause != null
                && cause.getMessage() != null
                && cause.getMessage().toLowerCase().contains("unique");
    }

    protected void rollbackSafely(Transaction tx, Exception originalError) {
        if (tx == null || !tx.isActive()) {
            return;
        }
        try {
            tx.rollback();
        } catch (Exception rollbackError) {
            log.warn("Rollback failed after original error: {}", originalError.getMessage(), rollbackError);
        }
    }
}
