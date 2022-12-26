package com.example.springbootjpa01.domain;

import com.example.springbootjpa01.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany // 킹부러 복잡하게
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name="category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")) // 일대다, 다대일로 풀어내는 중간 테이블
    private List<Item> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent; // 계층 구조, 부모 카테고리

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>(); // 자식 카테고리 리스트


    // 연관관계 설정 메소드 //
    public void addChildCategory(Category child) {
        this.child.add(child);
        child.setParent(this);
    }

}
