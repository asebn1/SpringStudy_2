package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")  //id의 컬럼명
    private Long id;

    private String name;         //member에만 있으니 관계 X

    @Embedded
    private Address address;     // 단순 class

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

}
