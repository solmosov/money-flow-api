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
import tools.jackson.databind.ext.javatime.deser.key.ZoneIdKeyDeserializer;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query(value = """
                select 
                    x.date as day,
                    coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) as total_income,
                    coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as total_expense,
                    coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) 
                        - coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as total, 
            
                    json_agg(
                        json_build_object(
                                'category_name', c.name,
                                'category_icon_name', c.icon_name,
                                'amount', t.amount,
                                'currency', t.currency,
                                'description', t.description,
                                'type', t.type,
                                'created_at', t.created_at,
                                'updated_at', t.updated_at
                            )
                        ) as transactions
                from transactions t
                cross join lateral (
                               select date_trunc('day', t.date, :zoneId) as date 
                            ) x
                join categories c on c.id = t.category_id
                where t.user_id = :userId
                    and (cast(:from as timestamptz) is null or t.date >= :from)
                    and (cast(:to as timestamptz) is null or t.date < :to)
                group by x.date
                order by x.date desc
            """,
            countQuery = """
                    select count(distinct (t.date at time zone :zoneId)::date)
                    from transactions t
                    where (cast(:from as timestamptz) is null or t.date >= :from)
                              and (cast(:to as timestamptz) is null or t.date < :to)
                    """,
            nativeQuery = true)
    Page<TransactionDailyProjection> findDailyTransactions(
            @Param("userId") UUID userId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("zoneId") ZoneId zoneId,
            Pageable pageable
    );

    Page<Transaction> findAllByUserId(UUID userId, Pageable pageable);

    List<Transaction> user(User user);

    // STATS
    @Query(value = """
            select
                m.date,
                coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) as income,
                coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as expense
            from generate_series(
                date_trunc(cast(:unit as text), :from, :zoneId),
                date_trunc(cast(:unit as text), :from, :zoneId) + cast(:interval as interval) - cast('1 day' as interval),
                cast(concat('1 ', cast(:unit as text)) as interval)
            ) as m(date)
            left join transactions t
              on t.user_id = :userId
              and t.date >= m.date
              and t.date < m.date + cast(concat('1 ', cast(:unit as text)) as interval)
              and t.date >= :from
              and t.date < date_trunc(cast(:unit as text), :from, :zoneId)
                  + cast(:interval as interval)
            group by m.date
            order by m.date
            """, nativeQuery = true)
    List<TransactionIncomeExpenseStatProjection> findIncomeExpenseStats(
            @Param("userId") UUID userId,
            @Param("unit") String unit,
            @Param("interval") String interval,
            @Param("from") Instant from,
            @Param("zoneId") ZoneId zoneId
    );

    @Query(value = """
                select sum(t.amount) as  value, c.name as name
                from categories c
                join transactions t on c.id = t.category_id
                cross join lateral(
                    select date_trunc(cast(:unit as text), t.date, :zoneId) as date
                ) x
                where c.user_id = :userId
                    and c.type = 'EXPENSE'
                    and t.date >= :from
                    and t.date < date_trunc(cast(:unit as text), :from, :zoneId) + cast(:interval as interval)
                group by c.name
                order by sum(t.amount) desc
            """, nativeQuery = true)
    List<TransactionCategoryExpenseStatProjection> findCategoryExpenseStats(
            @Param("userId") UUID userId,
            @Param("unit") String unit,
            @Param("interval") String interval,
            @Param("from") Instant from,
            @Param("zoneId") ZoneId zoneId
    );
}
