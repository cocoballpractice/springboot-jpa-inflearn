package com.example.springbootjpa01.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        // OrderItemDto를 OrderDto 내의 컬렉션에 넣기
        result.forEach(o-> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    /**
     * OrderDto 처리용 메소드
     */
    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new com.example.springbootjpa01.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    /**
     * OrderItemDto 처리용 메소드
     */
    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
         "select new com.example.springbootjpa01.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                 " from OrderItem oi" +
                 " join oi.item i" +
                 " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

}
