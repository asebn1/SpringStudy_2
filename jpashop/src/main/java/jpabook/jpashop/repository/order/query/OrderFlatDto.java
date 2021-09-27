package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderFlatDto {
    // order
    private Long id;
    private String name;
    private LocalDate orderDate;
    private OrderStatus orderStatus;
    private Address address;
    // orderItem
    private String itemName;
    private int orderPrice;
    private int count;

    public OrderFlatDto(Long id, String name, LocalDate orderDate, OrderStatus orderStatus, Address address, String itemName, int orderPrice, int count) {
        this.id = id;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
