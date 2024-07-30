package com.beyond.ordersystem.ordering.controller;

import com.beyond.ordersystem.common.dto.CommonResDto;
import com.beyond.ordersystem.ordering.domain.OrderDetail;
import com.beyond.ordersystem.ordering.domain.Ordering;
import com.beyond.ordersystem.ordering.dto.OrderListResDto;
import com.beyond.ordersystem.ordering.dto.OrderSaveReqDto;
import com.beyond.ordersystem.ordering.service.OrderingService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderingController {

    private final OrderingService orderingService;
    @Autowired
    public OrderingController(OrderingService orderingService) {
        this.orderingService = orderingService;
    }


    @PostMapping("/order/create")
    public ResponseEntity<?> orderCreate(@RequestBody List<OrderSaveReqDto> dto){
        Ordering ordering = orderingService.orderCreate(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "정상완료", ordering.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/order/list")
    public ResponseEntity<?> orderList(){
        List<OrderListResDto> orderList = orderingService.orderList();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "정상완료", orderList);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }


//    내 주문만 볼 수 있는 myOrders
    @GetMapping("/order/myorders")
    public ResponseEntity<?> myOrders(){
        List<OrderListResDto> myOrderList = orderingService.myOrders();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "정상완료", myOrderList);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }


//    admin사용자의 주문취소 : /order/{id}/ cancel -> orderstatus만 변경
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/order/{id}/cancel")
    public ResponseEntity<?> orderCancel(@PathVariable Long id){
        Ordering ordering = orderingService.orderCancel(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "취소완료", ordering.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }
}
