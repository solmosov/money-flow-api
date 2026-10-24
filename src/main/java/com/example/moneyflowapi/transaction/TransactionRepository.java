package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.transaction.projection.*;
import com.example.moneyflowapi.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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
                    t.id as id,
                    w.name as wallet_name,
                    w.type as wallet_type,
                    c.name as category_name,
                    c.icon_name as category_icon_name,
                    t.amount as amount,
                    t.currency_code as currency_code,
                    cr.symbol as currency_symbol,
                    t.description as description,
                    t.type as type,
                    t.date as date,
                    t.created_at as created_at,
                    t.updated_at as updated_at
                from transactions t
                join wallets w on a.id = t.wallet_id
                join categories c on c.id = t.category_id
                join currencies cr on cr.code = t.currency_code
                where c.user_id = :userId
                    and (cast(:from as timestamptz) is null or t.date >= cast(:from as timestamptz))
                    and (cast(:to as timestamptz) is null or t.date < cast(:to as timestamptz))
                order by t.date desc, t.id desc
            """,
            nativeQuery = true)
    Page<TransactionProjection> findFeedTransactions(
            @Param("userId") UUID userId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            Pageable pageable
    );

    @Query(value = """
            select
                date_trunc('day', t.date, :zoneId) as day,
                coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) as total_income,
                coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as total_expense
            from transactions t
            where t.user_id = :userId
                and t.date >= :from
                and t.date < :to
            group by day
            order by day desc
            """,
            nativeQuery = true)
    List<DailyTotalProjection> findDailyTotals(
            @Param("userId") UUID userId,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("zoneId") String zoneId
    );

    Page<Transaction> findAllByUserId(UUID userId, Pageable pageable);

    List<Transaction> user(User user);

    // STATS
    @Query(value = """
            select
                 date_trunc(cast(:unit as text), t.date, cast(:zoneId as text)) as date,
                 coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) as income,
                 coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as expense
             from transactions t
             where t.user_id = :userId
                 and t.date >= cast(:from as timestamptz)
                 and t.date < cast(:to as timestamptz)
             group by 1
             order by 1
            """, nativeQuery = true)
    List<TransactionIncomeExpenseStatProjection> findIncomeExpenseStats(
            @Param("userId") UUID userId,
            @Param("unit") String unit,
            @Param("from") Instant from,
            @Param("to") Instant to,
            @Param("zoneId") String zoneId
    );

    @Query(value = """
                select sum(t.amount) as value, c.name as name
                from transactions t
                join categories c on c.id = t.category_id
                where t.user_id = :userId
                    and c.type = :categoryType
                    and t.date >= cast(:from as timestamptz)
                    and t.date < cast(:to as timestamptz)
                group by c.id, c.name
                order by sum(t.amount) desc
            """, nativeQuery = true)
    List<TransactionCategoryExpenseStatProjection> findCategoryTotalsStatsByType(
            @Param("userId") UUID userId,
            @Param("categoryType") String categoryType,
            @Param("from") Instant from,
            @Param("to") Instant to
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

    @EntityGraph(attributePaths = {"account"})
    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);

    @Query(value = """
            SELECT DISTINCT CAST(EXTRACT(YEAR FROM t.date AT TIME ZONE 'UTC') AS integer)
            FROM transactions t
            WHERE t.user_id = :userId
            ORDER BY 1 DESC
            """, nativeQuery = true)
    List<Integer> findDistinctYearsByUserId(@Param("userId") UUID userId);


    @Query(value = """
            WITH params AS (
                SELECT
                    CAST(:zone AS text) AS zone,
                    CAST(:period AS text) AS period,
                    CASE CAST(:period AS text)
                        WHEN 'week' THEN date_trunc('week', CAST(:from AS timestamptz) AT TIME ZONE CAST(:zone AS text))
                        WHEN 'month' THEN date_trunc('month', CAST(:from AS timestamptz) AT TIME ZONE CAST(:zone AS text))
                        ELSE date_trunc('year', CAST(:from AS timestamptz) AT TIME ZONE CAST(:zone AS text))
                    END AS cur_start
            ),
            bounds AS (
                SELECT
                    p.zone,
                    p.period,
                    p.cur_start,
                    CASE p.period
                        WHEN 'week' THEN p.cur_start - interval '1 week'
                        WHEN 'month' THEN p.cur_start - interval '1 month'
                        ELSE p.cur_start - interval '1 year'
                    END AS prev_start,
                    CASE p.period
                        WHEN 'week' THEN p.cur_start + interval '1 week'
                        WHEN 'month' THEN p.cur_start + interval '1 month'
                        ELSE p.cur_start + interval '1 year'
                    END AS cur_end,
                    p.cur_start AS prev_end
                FROM params p
            ),
            buckets AS (
                SELECT
                    b.zone,
                    g.i,
                    CASE b.period
                        WHEN 'week' THEN b.cur_start + (g.i - 1) * interval '1 day'
                        WHEN 'month' THEN b.cur_start + (g.i - 1) * interval '7 days'
                        ELSE b.cur_start + (g.i - 1) * interval '1 month'
                    END AS cur_from,
                    CASE b.period
                        WHEN 'week' THEN b.cur_start + g.i * interval '1 day'
                        WHEN 'month' THEN CASE WHEN g.i = 4 THEN b.cur_end ELSE b.cur_start + g.i * interval '7 days' END
                        ELSE b.cur_start + g.i * interval '1 month'
                    END AS cur_to,
                    CASE b.period
                        WHEN 'week' THEN b.prev_start + (g.i - 1) * interval '1 day'
                        WHEN 'month' THEN b.prev_start + (g.i - 1) * interval '7 days'
                        ELSE b.prev_start + (g.i - 1) * interval '1 month'
                    END AS prev_from,
                    CASE b.period
                        WHEN 'week' THEN b.prev_start + g.i * interval '1 day'
                        WHEN 'month' THEN CASE WHEN g.i = 4 THEN b.prev_end ELSE b.prev_start + g.i * interval '7 days' END
                        ELSE b.prev_start + g.i * interval '1 month'
                    END AS prev_to
                FROM bounds b
                CROSS JOIN generate_series(1, 12) AS g(i)
                WHERE g.i <= CASE b.period WHEN 'week' THEN 7 WHEN 'month' THEN 4 ELSE 12 END
            ),
            agg AS (
                SELECT
                    bk.i,
                    bk.zone,
                    bk.cur_from,
                    COALESCE(SUM(t.amount) FILTER (
                        WHERE t.date >= bk.prev_from AT TIME ZONE bk.zone
                          AND t.date <  bk.prev_to AT TIME ZONE bk.zone
                    ), 0) AS last_period,
                    COALESCE(SUM(t.amount) FILTER (
                        WHERE t.date >= bk.cur_from AT TIME ZONE bk.zone
                          AND t.date <  bk.cur_to AT TIME ZONE bk.zone
                    ), 0) AS this_period
                FROM buckets bk
                LEFT JOIN transactions t
                    ON t.user_id = CAST(:userId AS uuid)
                    AND t.type = 'EXPENSE'
                    AND t.date >= bk.prev_from AT TIME ZONE bk.zone
                    AND t.date <  bk.cur_to AT TIME ZONE bk.zone
                GROUP BY bk.i, bk.zone, bk.cur_from
            )
            SELECT
                i AS bucket_index,
                cur_from AT TIME ZONE zone AS day_name,
                last_period,
                this_period,
                this_period - last_period AS diff,
                CASE
                    WHEN last_period = 0 THEN NULL
                    ELSE ROUND((this_period - last_period) * 100.0 / last_period, 1)
                END AS diff_percent
            FROM agg
            ORDER BY i
            """, nativeQuery = true)
    List<TransactionDiffProjection> getPeriodDiff(
            @Param("userId") UUID userId,
            @Param("from") Instant from,
            @Param("zone") String zone,
            @Param("period") String period
    );
}
