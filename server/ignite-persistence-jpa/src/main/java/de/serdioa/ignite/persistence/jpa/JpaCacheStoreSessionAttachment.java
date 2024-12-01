package de.serdioa.ignite.persistence.jpa;

import javax.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;


@AllArgsConstructor
@Getter
public class JpaCacheStoreSessionAttachment {

    final EntityManager entityManager;

    final TransactionTemplate transactionTemplate;
    
    final TransactionStatus transactionStatus;
}
