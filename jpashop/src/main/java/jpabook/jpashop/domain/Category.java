package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class Category {
    @Id @GeneratedValue
    @Column(name="Category_id")
    private Long id;
    private String name;

    @ManyToMany // ManyToMany는 변경해줘야함
    @JoinTable(name = "category_item",                                    // 조인 테이블 명
            joinColumns = @JoinColumn(name = "category_id"),              // 현재 엔티티 참조하는 외래키 Category
            inverseJoinColumns = @JoinColumn(name = "item_id")            // 반대방향 엔티티 참조하는 외래키 Item
    )
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    // 연관관계 메서드
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }
}
