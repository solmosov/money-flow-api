package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.transaction.projection.TransactionDailyProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query(value = """
        select 
            ((t.date AT TIME ZONE 'UTC')::date::timestamp AT TIME ZONE 'UTC') as day,
            coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) as total_income,
            coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as total_expense,
            coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) 
                - coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as total, 
                    
            json_agg(
                json_build_object(
                        'categoryName', c.name,
                        'amount', t.amount,
                        'currency', t.currency,
                        'description', t.description,
                        'type', t.type,
                        'created_at', t.created_at,
                        'updated_at', t.updated_at
                    )
                ) as transactions
        from transactions t
        join categories c on c.id = t.category_id
        group by ((t.date at time zone 'UTC')::date::timestamp at time zone 'UTC')
        order by ((t.date at time zone 'UTC')::date::timestamp at time zone 'UTC')
          
    """, nativeQuery = true)
    List<TransactionDailyProjection> findDailyTransactions();
}
