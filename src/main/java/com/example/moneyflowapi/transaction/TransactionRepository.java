package com.example.moneyflowapi.transaction;

import com.example.moneyflowapi.category.CategoryType;
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

//    @Query(value = """
//                select
//                    t.id as id,
//                    c.name as category_name,
//                    c.icon_name as category_icon_name,
//                    t.amount as amount,
//                    t.currency as currency,
//                    t.description as description,
//                    t.type as type,
//                    t.date as date,
//                    t.created_at as created_at,
//                    t.updated_at as updated_at
//                from transactions t
//                join categories c on c.id = t.category_id
//                where c.user_id = :userId
//                    and (cast(:from as timestamptz) is null or t.date >= cast(:from as timestamptz))
//                    and (cast(:to as timestamptz) is null or t.date < cast(:to as timestamptz))
//                order by t.date desc, t.id desc
//            """,
//            nativeQuery = true)
//    Page<TransactionProjection> findFeedTransactions(
//            @Param("userId") UUID userId,
//            @Param("from") Instant from,
//            @Param("to") Instant to,
//            Pageable pageable
//    );
//
//    @Query(value = """
//        select
//            date_trunc('day', t.date, :zoneId) as day,
//            coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) as total_income,
//            coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as total_expense
//        from transactions t
//        where t.user_id = :userId
//            and t.date >= :from
//            and t.date < :to
//        group by day
//        order by day desc
//        """,
//            nativeQuery = true)
//    List<DailyTotalProjection> findDailyTotals(
//            @Param("userId") UUID userId,
//            @Param("from") Instant from,
//            @Param("to") Instant to,
//            @Param("zoneId") String zoneId
//    );
//
//    Page<Transaction> findAllByUserId(UUID userId, Pageable pageable);
//
//    List<Transaction> user(User user);
//
//    // STATS
//    @Query(value = """
//           select
//                date_trunc(cast(:unit as text), t.date, cast(:zoneId as text)) as date,
//                coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) as income,
//                coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as expense
//            from transactions t
//            where t.user_id = :userId
//                and t.date >= cast(:from as timestamptz)
//                and t.date < cast(:to as timestamptz)
//            group by 1
//            order by 1
//            """, nativeQuery = true)
//    List<TransactionIncomeExpenseStatProjection> findIncomeExpenseStats(
//            @Param("userId") UUID userId,
//            @Param("unit") String unit,
//            @Param("from") Instant from,
//            @Param("to") Instant to,
//            @Param("zoneId") String zoneId
//    );
//
//    @Query(value = """
//                select sum(t.amount) as value, c.name as name
//                from transactions t
//                join categories c on c.id = t.category_id
//                where t.user_id = :userId
//                    and c.type = :categoryType
//                    and t.date >= cast(:from as timestamptz)
//                    and t.date < cast(:to as timestamptz)
//                group by c.id, c.name
//                order by sum(t.amount) desc
//            """, nativeQuery = true)
//    List<TransactionCategoryExpenseStatProjection> findCategoryTotalsStatsByType(
//            @Param("userId") UUID userId,
//            @Param("categoryType") String categoryType,
//            @Param("from") Instant from,
//            @Param("to") Instant to
//    );
//
//    @Query(value = """
//                select
//                    m.date as date,
//                    c.name as category_name,
//                    c.icon_name as category_icon_name,
//                    coalesce(sum(t.amount), 0) as total_expense
//                from generate_series(
//                    date_trunc(cast(:unit as text), cast(:from as timestamptz), :zoneId),
//                    date_trunc(cast(:unit as text), cast(:from as timestamptz), :zoneId) + cast(:interval as interval) - cast(cast('1 ', :unit) as interval),
//                    cast(concat('1 ', cast(:unit as text)) as interval)
//                ) as m(date)
//                cross join (
//                    select *
//                    from categories
//                        where user_id = :userId
//                        and type = 'EXPENSE'
//                    ) c
//                left join transactions t
//                    on t.category_id = c.id
//                    and t.user_id = :userId
//                    and t.date >= m.date
//                    and t.date < m.date + cast(:interval as interval)
//                group by
//                    m.date,
//                    c.name,
//                    c.icon_name
//                order by m.date
//
//            """, nativeQuery = true)
//    List<TransactionCategoryExpenseStatsByIntervalProjection> findCategoryExpenseStatsByInterval(
//            @Param("userId") UUID userId,
//            @Param("unit") String unit,
//            @Param("interval") String interval,
//            @Param("from") Instant from,
//            @Param("zoneId") ZoneId zoneId
//    );
//
//    @Query(value = """
//                select
//                        date_trunc('month', cast(:from as timestamptz), cast(:zoneId as text)) as month,
//                        coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0) as total_income,
//                        coalesce(sum(t.amount) filter(where t.type = 'EXPENSE'), 0) as total_expense,
//                        coalesce(sum(t.amount) filter (where t.type = 'INCOME'), 0)\s
//                            - coalesce(sum(t.amount) filter (where t.type = 'EXPENSE'), 0) as total
//                from transactions t
//                where
//                    t.user_id = :userId
//                    and t.date >= date_trunc('month', cast(:from as timestamptz),  cast(:zoneId as text))
//                    and t.date < date_trunc('month', cast(:from as timestamptz),  cast(:zoneId as text)) + '1 month'
//            """, nativeQuery = true)
//    TransactionMonthTotalsStatProjection findMonthTotalsStat(
//            @Param("userId") UUID userId,
//            @Param("from") Instant from,
//            @Param("zoneId") ZoneId zoneId);
//
//    @Modifying(flushAutomatically = true, clearAutomatically = true)
//    @Query("DELETE FROM Transaction t WHERE t.id = :id AND t.user.id = :userId")
//    int deleteByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);
//
//    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);
//
//    @Query(value = """
//            SELECT DISTINCT CAST(EXTRACT(YEAR FROM t.date AT TIME ZONE 'UTC') AS integer)
//            FROM transactions t
//            WHERE t.user_id = :userId
//            ORDER BY 1 DESC
//            """, nativeQuery = true)
//    List<Integer> findDistinctYearsByUserId(@Param("userId") UUID userId);
}
