package com.example.ebearrestapi.controller;

import com.example.ebearrestapi.dto.request.UserDto;
import com.example.ebearrestapi.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/wish")
@RequiredArgsConstructor
@Slf4j
public class WishController {
    private final WishService wishService;

    @GetMapping("/list")
    public ResponseEntity<?> wishList(@AuthenticationPrincipal UserDto userDto,
                                      @RequestParam(required = false) Long cursor,
                                      @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(wishService.getWishList(userDto, cursor, size));
    }

    @PostMapping("/product/{id}")
    public ResponseEntity<?> wishProduct(@AuthenticationPrincipal UserDto userDto, @PathVariable Long id) {
        wishService.wishProduct(userDto, id);
        return ResponseEntity.status(HttpStatus.OK).build(); //200
    }

    @DeleteMapping("/{wishListNo}")
    public ResponseEntity<?> wishDelete(@AuthenticationPrincipal UserDto userDto, @PathVariable Long wishListNo) {
        wishService.wishDelete(userDto, wishListNo);
        return ResponseEntity.noContent().build(); //204
    }

}
