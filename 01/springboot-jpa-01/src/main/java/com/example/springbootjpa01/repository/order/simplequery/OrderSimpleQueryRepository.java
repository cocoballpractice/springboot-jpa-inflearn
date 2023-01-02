package com.example.springbootjpa01.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<SimpleOrderQueryDto> findOrderDtos() {
        return em.createQuery(
                        "select new com.example.springbootjpa01.repository.order.simplequery.SimpleOrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", SimpleOrderQueryDto.class)
                .getResultList();
    }

}
