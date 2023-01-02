package com.example.springbootjpa01.domain;

import com.example.springbootjpa01.domain.item.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 protected로 제약
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore // 양방향 연관관계일 경우 한쪽은 Ignore 처리 (순환참조 방지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 가격
    private int count; // 주문 수량


    // 생성 메서드

    // protected OrderItem() {} static 외 생성 제약

    public static OrderItem createOrderItem (Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count); // 재고 소진

        return orderItem;
    }


    // 비즈니스 로직

    public void cancel() {
        getItem().addStock(count); // 재고 수량 원복
    }

    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }

}
