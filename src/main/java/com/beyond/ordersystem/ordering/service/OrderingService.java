package com.beyond.ordersystem.ordering.service;

import com.beyond.ordersystem.member.domain.Member;
import com.beyond.ordersystem.member.repository.MemberRepository;
import com.beyond.ordersystem.ordering.domain.OrderDetail;
import com.beyond.ordersystem.ordering.domain.OrderStatus;
import com.beyond.ordersystem.ordering.domain.Ordering;
import com.beyond.ordersystem.ordering.dto.OrderListResDto;
import com.beyond.ordersystem.ordering.dto.OrderSaveReqDto;
import com.beyond.ordersystem.ordering.repository.OrderDetailRepository;
import com.beyond.ordersystem.ordering.repository.OrderingRepository;

import com.beyond.ordersystem.product.domain.Product;
import com.beyond.ordersystem.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderingService {
    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    @Autowired
    public OrderingService(OrderingRepository orderingRepository, MemberRepository memberRepository, ProductRepository productRepository, OrderDetailRepository orderDetailRepository) {
        this.orderingRepository = orderingRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.orderDetailRepository = orderDetailRepository;
    }


    public Ordering orderCreate(List<OrderSaveReqDto> dtos){
//        //        방법1.쉬운방식
////        Ordering생성 : member_id, status
//        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(()->new EntityNotFoundException("없음"));
//        Ordering ordering = orderingRepository.save(dto.toEntity(member));
//
////        OrderDetail생성 : order_id, product_id, quantity
//        for(OrderSaveReqDto.OrderDto orderDto : dto.getOrderDtos()){
//            Product product = productRepository.findById(orderDto.getProductId()).orElse(null);
//            int quantity = orderDto.getProductCount();
//            OrderDetail orderDetail =  OrderDetail.builder()
//                    .product(product)
//                    .quantity(quantity)
//                    .ordering(ordering)
//                    .build();
//            orderDetailRepository.save(orderDetail);
//        }


//        방법2. JPA애 최적화된 방식
//        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(()->new EntityNotFoundException("없는 회원입니다."));
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(()-> new EntityNotFoundException("없는 회원입니다."));
        Ordering ordering = Ordering.builder()
                .member(member)
                .build();
        for (OrderSaveReqDto dto : dtos) {
            Product product = productRepository.findById(dto.getProductId()).orElse(null);
            int quantity = dto.getProductCount();
            if (product.getStockQuantity() < quantity){
                throw new IllegalArgumentException("재고부족");
            }
            product.updateStockQuantity(quantity); //변경감지(dirty checking)로 인해 별도의 save 불필요
            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .quantity(quantity)
                    .ordering(ordering)
                    .build();
            ordering.getOrderDetails().add(orderDetail);
        }
        orderingRepository.save(ordering);

        return ordering;
    }


    public List<OrderListResDto> orderList(){
        List<Ordering> orderings = orderingRepository.findAll();
        List<OrderListResDto> orderListResDtos = new ArrayList<>();
        for (Ordering ordering : orderings){
            orderListResDtos.add(ordering.fromEntity());
        }

        return orderListResDtos;
    }



    public List<OrderListResDto> myOrders(){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()-> new EntityNotFoundException("not found"));
        List<Ordering> orderings = orderingRepository.findByMember(member);
        List<OrderListResDto> orderListResDtos = new ArrayList<>();
        for (Ordering ordering : orderings){
            orderListResDtos.add(ordering.fromEntity());
        }

        return orderListResDtos;
    }


    public Ordering orderCancel(Long id){
        Ordering ordering = orderingRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("not found"));
        ordering.updateStatus(OrderStatus.CANCELED);
        return ordering;
    }

}
