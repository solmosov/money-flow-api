package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.transaction.enums.StatsType;
import com.example.moneyflowapi.transaction.projection.TransactionCategoryExpenseStatProjection;
import com.example.moneyflowapi.transaction.projection.TransactionDailyProjection;
import com.example.moneyflowapi.transaction.projection.TransactionIncomeExpenseStatProjection;
import com.example.moneyflowapi.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query(value = """
                select 
                    date_trunc('day', t.date, 'UTC') as day,
                    coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) as total_income,
                    coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as total_expense,
                    coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) 
                        - coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as total, 
            
                    json_agg(
                        json_build_object(
                                'category_name', c.name,
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
                where t.user_id = :userId
                    and (cast(:from as timestamp with time zone) is null or t.date >= :from)
                    and (cast(:to as timestamp with time zone) is null or t.date < :to)
                group by date_trunc('day', t.date, 'UTC')
                order by date_trunc('day', t.date, 'UTC') desc
            """,
            countQuery = """
                    select count(distinct (t.date at time zone 'UTC')::date)
                    from transactions t
                    where (cast(:from as timestamp with time zone) is null or t.date >= :from)
                              and (cast(:to as timestamp with time zone) is null or t.date < :to)
                    """,
            nativeQuery = true)
    Page<TransactionDailyProjection> findDailyTransactions(
            @Param("userId") UUID userId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable
    );

    Page<Transaction> findAllByUserId(UUID userId, Pageable pageable);

    List<Transaction> user(User user);

    // STATS
    @Query(value = """
        select 
                x.date,
                coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) as income,
                coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as expense
        from transactions t
        cross join lateral (
                select date_trunc(cast(:unit as text), t.date, 'UTC') as date
            ) x
        where t.user_id = :userId
            and t.date >= date_trunc(cast(:unit as text), :from, 'UTC')
            and t.date < date_trunc(cast(:unit as text), :from, 'UTC') + cast(:interval as interval)
        group by x.date
        order by x.date
    """, nativeQuery = true)
    List<TransactionIncomeExpenseStatProjection> findIncomeExpenseStats(
            @Param("userId") UUID userId,
            @Param("unit") String unit,
            @Param("interval") String interval,
            @Param("from") Instant from
    );

    @Query(value = """
        select sum(t.amount) as  value, c.name as name
        from categories c
        join transactions t on c.id = t.category_id
        cross join lateral(
            select date_trunc(cast(:unit as text), t.date, 'UTC') as date
        ) x
        where c.user_id = :userId
            and c.type = 'EXPENSE'
            and t.date >= date_trunc(cast(:unit as text), :from, 'UTC')
            and t.date < date_trunc(cast(:unit as text), :from, 'UTC') + cast(:interval as interval)
        group by c.name
        order by sum(t.amount) desc
    """, nativeQuery = true)
    List<TransactionCategoryExpenseStatProjection> findCategoryExpenseStats(
            @Param("userId") UUID userId,
            @Param("unit") String unit,
            @Param("interval") String interval,
            @Param("from") Instant from
    );
}
