package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.transaction.projection.*;
import com.example.moneyflowapi.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query(value = """
                select 
                    date_trunc('day', t.date, :zoneId) as day,
                    coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) as total_income,
                    coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as total_expense,
                    coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) 
                        - coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as total, 
            
                    json_agg(
                        json_build_object(
                                'id', t.id,
                                'category_name', c.name,
                                'category_icon_name', c.icon_name,
                                'amount', t.amount,
                                'currency', t.currency,
                                'description', t.description,
                                'type', t.type,
                                'date', t.date,
                                'created_at', t.created_at,
                                'updated_at', t.updated_at
                            ) order by t.date desc
                        ) as transactions
                from transactions t
                join categories c on c.id = t.category_id
                where t.user_id = :userId
                    and (
                        cast(:from as timestamptz) is null or date_trunc('day', t.date, :zoneId) >= date_trunc('day', cast(:from as timestamptz), :zoneId)
                    )
                    and (
                        cast(:to as timestamptz) is null or date_trunc('day', t.date, :zoneId) <= date_trunc('day', cast(:to as timestamptz), :zoneId)
                    )
                group by day
                order by day desc
            """,
            countQuery = """
                    select count(distinct (date_trunc('day', t.date, :zoneId)))
                    from transactions t
                    where
                        (cast(:from as timestamptz) is null or date_trunc('day',t.date, :zoneId) >= date_trunc('day',cast(:from as timestamptz), :zoneId))
                        and (cast(:to as timestamptz) is null or date_trunc('day',t.date, :zoneId) <= date_trunc('day',cast(:to as timestamptz), :zoneId))
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
                date_trunc(cast(:unit as text), cast(:from as timestamptz), :zoneId),
                date_trunc(cast(:unit as text), cast(:from as timestamptz), :zoneId) + cast(:interval as interval) - cast(concat('1 ', :unit) as interval),
                cast(concat('1 ', cast(:unit as text)) as interval)
            ) as m(date)
            left join transactions t
              on t.user_id = :userId
              and t.date at time zone :zoneId >= m.date
              and t.date < m.date + cast(concat('1 ', cast(:unit as text)) as interval)
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
                    and t.date >= date_trunc(cast(:unit as text), cast(:from as timestamptz), :zoneId)
                    and t.date < date_trunc(cast(:unit as text), cast(:from as timestamptz), :zoneId) + cast(:interval as interval)
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

    @Query(value = """
                select 
                    m.date as date,
                    c.name as category_name,
                    c.icon_name as category_icon_name,
                    coalesce(sum(t.amount), 0) as total_expense
                from generate_series(
                    date_trunc(cast(:unit as text), cast(:from as timestamptz), :zoneId),
                    date_trunc(cast(:unit as text), cast(:from as timestamptz), :zoneId) + cast(:interval as interval) - cast(cast('1 ', :unit) as interval),
                    cast(concat('1 ', cast(:unit as text)) as interval)    
                ) as m(date)
                cross join (
                    select *
                    from categories
                        where user_id = :userId
                        and type = 'EXPENSE'
                    ) c
                left join transactions t 
                    on t.category_id = c.id
                    and t.user_id = :userId
                    and t.date >= m.date
                    and t.date < m.date + cast(:interval as interval)
                group by 
                    m.date,
                    c.name,
                    c.icon_name
                order by m.date 
            
            """, nativeQuery = true)
    List<TransactionCategoryExpenseStatsByIntervalProjection> findCategoryExpenseStatsByInterval(
            @Param("userId") UUID userId,
            @Param("unit") String unit,
            @Param("interval") String interval,
            @Param("from") Instant from,
            @Param("zoneId") ZoneId zoneId
    );

    @Query(value = """
                select 
                        date_trunc('month', cast(:from as timestamptz), cast(:zoneId as text)) as month,
                        coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) as total_income,
                        coalesce(sum(t.amount) filter(where t.type = 'EXPENSE'), 0) as total_expense,
                        coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0)\s
                            - coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as total
                from transactions t
                where 
                    t.user_id = :userId
                    and t.date >= date_trunc('month', cast(:from as timestamptz),  cast(:zoneId as text))
                    and t.date < date_trunc('month', cast(:from as timestamptz),  cast(:zoneId as text)) + '1 month'
            """, nativeQuery = true)
    TransactionMonthTotalsStatProjection findMonthTotalsStat(
            @Param("userId") UUID userId,
            @Param("from") Instant from,
            @Param("zoneId") ZoneId zoneId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Transaction t WHERE t.id = :id AND t.user.id = :userId")
    int deleteByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);
}
