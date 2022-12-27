package com.example.springbootjpa01.service;

import com.example.springbootjpa01.domain.Delivery;
import com.example.springbootjpa01.domain.Member;
import com.example.springbootjpa01.domain.Order;
import com.example.springbootjpa01.domain.OrderItem;
import com.example.springbootjpa01.domain.item.Item;
import com.example.springbootjpa01.repository.ItemRepository;
import com.example.springbootjpa01.repository.MemberRepository;
import com.example.springbootjpa01.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 주문
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송 정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order); // order를 persist 하므로 cascade 되어 있는 delivery, orderitem도 자동으로 persist됨
        // cascade의 범위? : 생성 주기가 동일하게 관리되는 경우, 연관관계의 주인 외에 참조되는 곳이 없을 경우
        // 애매하면 cascade를 일단 안 쓰는 것도 방법... 이후 감이 잡히면 리팩토링하면서 사용용

        return order.getId();
    }

   // 취소
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId); // 조회
        order.cancel(); // 취소
        // JPA가 아닌 ORM, SQL Mapper의 경우 여기서 끝나는 것이 아니라 실제 Update 쿼리를 해야 함
        // 그러나 JPA는 Dirty Checking을 하여 변경점을 체크, 자동으로 Update를 해줌
    }

    // 검색
    // public List<Order>
}
