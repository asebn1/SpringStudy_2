package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSimpleQueryDto;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * XtoOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    // [조회] v1
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all){
            order.getMember().getName();  // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }

    // [조회] v2 -> Dto로 조회할경우, N+1
    @GetMapping("/api/v2/simple-orders")
    public List<OrderSimpleQueryDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderSimpleQueryDto> result = orders.stream()
                .map(o -> new OrderSimpleQueryDto(o))
                .collect(Collectors.toList());
        return result;
    }
    // [조회] v3 -> 쿼리를 1번으로
    @GetMapping("/api/v3/simple-orders")
    public List<OrderSimpleQueryDto> ordersV3(){
        List<Order> orders =  orderRepository.findAllWithMemberDelivery();   // 쿼리
        List<OrderSimpleQueryDto> result = orders.stream()
                .map(o -> new OrderSimpleQueryDto(o))
                .collect(Collectors.toList());
        return result;
    }

    // [조회] v4
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderRepository.findOrderDtos();
    }

}
