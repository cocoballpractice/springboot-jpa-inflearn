package com.example.springbootjpa01.repository;

import com.example.springbootjpa01.api.OrderSimpleApiController;
import com.example.springbootjpa01.domain.Address;
import com.example.springbootjpa01.domain.Order;
import com.example.springbootjpa01.domain.OrderStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // TODO : Querydsl로 변경 예정
    public List<Order> findAll(OrderSearch orderSearch) {
        return em.createQuery("select o from Order o join o.member m" +
                " where o.status = :status " +
                " and m.name like :name ",
                Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList(); // 프록시 객체가 아닌 실제 DB를 조회해서 객체를 가져와버림
    }

    public List<Order> findAllWithItem() {

        /**
         * distinct의 2가지 기능
         * (1) SQL에 DISTINCT를 추가 (단, DB는 결과가 완전히 동일한 ROW만 중복 제거를 해주기 때문에 DB 상으로는 결과가 동일할 수 있음)
         * (2) 애플리케이션 상에서 같은 엔티티가 조회될 시 중복 제거를 해서 결과를 컬렉션에 담음
         * 매우 중요 - 페이징이 불가능하다
         */

        return em.createQuery(
                "select distinct o from Order o" + // SQL의 DISTINCT
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch  oi.item i", Order.class)
                .setFirstResult(1)
                .setMaxResults(100)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

}
