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

}
