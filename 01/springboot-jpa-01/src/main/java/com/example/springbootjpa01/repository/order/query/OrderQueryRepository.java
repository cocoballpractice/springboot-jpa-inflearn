package com.example.springbootjpa01.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<OrderQueryDto> findAllByDto_optimization() {

        // 주문 한번에 조회
        List<OrderQueryDto> result = findOrders();

        // 주문번호 컬렉션 조회
        List<Long> orderIds = toOrderIds(result);

        // key:주문번호 value:OrderItemDto 컬렉션 인 Map 생성
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

        // 주문 - Map 매칭
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        // 쿼리는 한 번 나가지만 OrderItems 기준으로 페이징이 됨 (Order 기준으로는 불가) + 중복 Order가 DB상에 조회됨
        return em.createQuery(
                "select new com.example.springbootjpa01.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count) " +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();
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

    /**
     * OrderItemDto 조회 시 IN 쿼리 파라미터로 사용할 OrderId 컬렉션 처리
     */
    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList()); // orderId를 전부 컬렉션에 담음
        // -> 아래 쿼리에 있는 IN 절에 뿌림 (batch.size 적용 효과)
        return orderIds;
    }

    /**
     * OrderItemDto 처리용 메소드 (IN 쿼리 적용)
     * 이후 key : orderId value : List<OrderIdDto> 인 Map 생성
     */
    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {

        // OrderItemDto 처리용 쿼리 (IN 절 적용하여 한 번에 조회)
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "select new com.example.springbootjpa01.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        // orderId가 Key이고 OrderItemQueryDto 컬렉션이 value인 맵으로 묶음 (메모리 상으로 올려둠)
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
        return orderItemMap;
    }


}
