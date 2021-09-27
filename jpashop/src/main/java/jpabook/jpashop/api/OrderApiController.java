package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    // [조회] v1
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    // [조회] v2
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }
    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDate orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;
        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                            .map(orderItem -> new OrderItemDto(orderItem))
                            .collect(Collectors.toList());

        }
    }
    @Data
    static class OrderItemDto{
        private String itemName; // 상품명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량
        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
    // [조회] v3
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }
    // [조회] v3.1 (문제해결)
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();  // 1. ToOne 관계만 join fetch
        // 2. 컬렉션은 정해진 사이즈만큼 가져옴
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    // [조회] v4 쿼리 -> jpa에서 dto를 조회
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    // [조회] v5 쿼리 -> in절 사용해서 최적화
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findAllByDto_optimization();
    }

    // [조회] v6 쿼리 -> join 결과를그대로 조회 -> 원하는 모양으로 변환
    @GetMapping("/api/v6/orders")
    public List<OrderFlatDto> ordersV6(){
        return orderQueryRepository.findAllByDto_flat();
    }
}
