package com.example.springbootjpa01.domain.item;

import com.example.springbootjpa01.domain.Category;
import com.example.springbootjpa01.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter // 편의상 넣었으나 실제로는 아래의 비즈니스 로직을 통해서 변경해야 함
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items") // 킹부러 복잡하게
    private List<Category> categories = new ArrayList<>();


    // 비즈니스 로직
    // DDD에서는 도메인에서 처리할 수 있는 것은 도메인 내부에 비즈니스 로직을 두는 것이 좋다
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("재고가 부족합니다");
        }
        this.stockQuantity = restStock;
    }

}
