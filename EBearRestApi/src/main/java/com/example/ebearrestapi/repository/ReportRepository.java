package com.example.ebearrestapi.repository;

import com.example.ebearrestapi.entity.ReportEntity;
import com.example.ebearrestapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    List<ReportEntity> findByParentIsNullAndBoard_DelYNOrderByReportNoDesc(String delYN);
    List<ReportEntity> findByParentIsNullAndBoard_DelYNAndProduct_UserOrderByReportNoDesc(String delYN, UserEntity user);
    @Query("""
        select r
        from ReportEntity r
        join fetch r.board b
        join fetch r.product p
        join fetch p.user pu
        where b.user.userNo = :userNo
          and r.parent is null
          and b.delYN = 'N'
        order by r.regDate desc
    """)
    List<ReportEntity> findMyRootReport(
            @Param("userNo") Long userNo
    );

    @Query("""
        select r
        from ReportEntity r
        join fetch r.board b
        where r.parent.reportNo in :parentReportNos
          and b.delYN = 'N'
    """)
    List<ReportEntity> findRepliesByParentReportNos(
            @Param("parentReportNos") List<Long> parentReportNos
    );

    @Query("""
        select r
        from ReportEntity r
        join fetch r.board b
        join fetch b.user
        join fetch r.product p
        join fetch p.user
        where r.reportNo = :reportNo
          and r.parent is null
          and b.delYN = 'N'
    """)
    Optional<ReportEntity> findRootReportDetail(@Param("reportNo") Long reportNo);

    @Query("""
        select r
        from ReportEntity r
        join fetch r.board b
        join fetch b.user
        where r.parent.reportNo = :parentReportNo
          and b.delYN = 'N'
    """)
    Optional<ReportEntity> findReplyByParentReportNo(@Param("parentReportNo") Long parentReportNo);
}
