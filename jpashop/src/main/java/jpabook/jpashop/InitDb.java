package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;
    @PostConstruct  // 처음시작시 구동됨
    public void init(){
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{
        private final EntityManager em;
        public void dbInit1(){

            Member member = new Member();
            member.setName("userA");
            member.setAddress(new Address("서울", "1", "1111"));
            em.persist(member);

            // book1
            Book book1 = new Book();
            book1.setName("JPA1 BOOK");
            book1.setPrice(10000);
            book1.setStockQuantity(100);
            em.persist(book1);
            // book2
            Book book2 = new Book();
            book2.setName("JPA2 BOOK");
            book2.setPrice(10000);
            book2.setStockQuantity(100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }
        public void dbInit2(){
            Member member = new Member();
            member.setName("userB");
            member.setAddress(new Address("진주", "2", "2222"));
            em.persist(member);

            // book1
            Book book1 = new Book();
            book1.setName("Spring1 BOOK");
            book1.setPrice(20000);
            book1.setStockQuantity(200);
            em.persist(book1);
            // book2
            Book book2 = new Book();
            book2.setName("Spring2 BOOK");
            book2.setPrice(40000);
            book2.setStockQuantity(300);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);

        }
    }
}

