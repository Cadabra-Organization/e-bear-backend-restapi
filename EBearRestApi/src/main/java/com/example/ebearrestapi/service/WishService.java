package com.example.ebearrestapi.service;

import com.example.ebearrestapi.dto.request.UserDto;
import com.example.ebearrestapi.dto.response.WishListPageDto;
import com.example.ebearrestapi.dto.response.WishListResultDto;
import com.example.ebearrestapi.entity.*;
import com.example.ebearrestapi.etc.FileType;
import com.example.ebearrestapi.etc.Role;
import com.example.ebearrestapi.repository.ProductRepository;
import com.example.ebearrestapi.repository.UserRepository;
import com.example.ebearrestapi.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishService {
    private final WishRepository wishRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public WishListPageDto getWishList(UserDto userDto, Long cursor, int size) {
        Long cursorValue = (cursor != null) ? cursor : Long.MAX_VALUE;
        Pageable pageable = PageRequest.of(0, size);
        Slice<WishListEntity> slice = wishRepository.findWishList(userDto.getUserId(), cursorValue, Role.SELLER, pageable);

        List<WishListResultDto> content = slice.getContent().stream()
                .map(this::toDto)
                .toList();

        Long nextCursor = content.isEmpty()
                ? null
                : content.get(content.size() - 1).getWishListNo();

        return WishListPageDto.builder()
                .content(content)
                .hasNext(slice.hasNext())
                .nextCursor(nextCursor)
                .build();
    }

    @Transactional
    public void wishProduct(UserDto userDto, Long id) {
        if (wishRepository.existsByUser_UserIdAndProduct_ProductNo(userDto.getUserId(), id)) {
            return;   // 이미 찜한 상품이면 무시
        }

        UserEntity user = userRepository.findByUserId(userDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        WishListEntity wishListEntity = WishListEntity.builder()
                .user(user)
                .product(product)
                .build();

        try {
            wishRepository.save(wishListEntity);
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 이미 저장된 경우
        }
    }

    @Transactional
    public void wishDelete(UserDto userDto, Long wishListNo) {
        WishListEntity wishListEntity = wishRepository
                .findByWishListNoAndUser_UserId(wishListNo, userDto.getUserId())
                .orElseThrow(() -> new RuntimeException("찜 내역을 찾을 수 없습니다."));

        wishRepository.delete(wishListEntity);
    }

    private WishListResultDto toDto(WishListEntity wish) {
        ProductEntity product = wish.getProduct();
        UserEntity seller = product.getUser();

        // 썸네일만 골라 경로 조합
        String productFile = product.getFileList().stream()
                .filter(f -> f.getFileType() == FileType.THUMBNAIL)
                .findFirst()
                .map(f -> f.getFileLocation() + f.getSaveFileName())
                .orElse(null);

        // 옵션 중 최저가
        Integer price = product.getProductOptionList().stream()
                .map(ProductOptionEntity::getProductOptionPrice)
                .min(Integer::compareTo)
                .orElse(null);

        FileEntity sellerFile = seller.getFile();

        return WishListResultDto.builder()
                .wishListNo(wish.getWishListNo())
                .productNo(product.getProductNo())
                .productName(product.getProductName())
                .productFile(productFile)
                .price(price)
                .sellerName(seller.getUserName())
                .sellerFile(sellerFile != null
                        ? sellerFile.getFileLocation() + sellerFile.getSaveFileName()
                        : null)
                .build();
    }
}
