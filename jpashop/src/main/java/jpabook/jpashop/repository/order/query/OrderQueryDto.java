package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderQueryDto {
    private Long id;
    private String name;
    private LocalDate orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemQueryDto> orderItems;

    public OrderQueryDto(Long id, String name, LocalDate orderDate, OrderStatus orderStatus, Address address) {
        this.id = id;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
