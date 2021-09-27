package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderSimpleQueryDto {
    private Long orderId;
    private String name;
    private LocalDate orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDto(Order order) {
        orderId = order.getId();
        name = order.getMember().getName();              // Lazy 초기화
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();      // Lazy 초기화
    }

    public OrderSimpleQueryDto(Long OrderId, String name, LocalDate orderDate, OrderStatus orderStatus, Address address) {
        this.orderId = OrderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;      // Lazy 초기화
    }
}
