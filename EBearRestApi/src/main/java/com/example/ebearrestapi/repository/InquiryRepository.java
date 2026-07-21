package com.example.ebearrestapi.repository;

import com.example.ebearrestapi.entity.InquiryEntity;
import com.example.ebearrestapi.entity.ProductEntity;
import com.example.ebearrestapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<InquiryEntity, Long> {
    List<InquiryEntity> findByProduct(ProductEntity product);

    // 관리자용 문의 목록 조회
    List<InquiryEntity> findByParentIsNullAndBoard_DelYNOrderByInquiryNoDesc(String delYN);
    // 판매자용 문의 목록 조회
    List<InquiryEntity> findByParentIsNullAndBoard_DelYNAndProduct_UserOrderByInquiryNoDesc(String delYN, UserEntity user);

    // 사용자 마이페이지 문의 목록 조회
    @Query("""
        select i
        from InquiryEntity i
        join fetch i.board b
        join fetch i.product p
        join fetch p.user pu
        where b.user.userNo = :userNo
          and i.parent is null
          and b.delYN = 'N'
        order by i.regDate desc
    """)
    List<InquiryEntity> findMyRootInquiries(
            @Param("userNo") Long userNo
    );

    @Query("""
        select i
        from InquiryEntity i
        join fetch i.board b
        where i.parent.inquiryNo in :parentInquiryNos
          and b.delYN = 'N'
    """)
    List<InquiryEntity> findRepliesByParentInquiryNos(
            @Param("parentInquiryNos") List<Long> parentInquiryNos
    );

    // 관리자 고객문의 상세 조회
    @Query("""
        select i
        from InquiryEntity i
        join fetch i.board b
        join fetch b.user
        join fetch i.product p
        join fetch p.user
        where i.inquiryNo = :inquiryNo
          and i.parent is null
          and b.delYN = 'N'
    """)
    Optional<InquiryEntity> findRootInquiryDetail(@Param("inquiryNo") Long inquiryNo);

    @Query("""
        select i
        from InquiryEntity i
        join fetch i.board b
        join fetch b.user
        where i.parent.inquiryNo = :parentInquiryNo
          and b.delYN = 'N'
    """)
    Optional<InquiryEntity> findReplyByParentInquiryNo(@Param("parentInquiryNo") Long parentInquiryNo);
}
