package com.example.springbootjpa01.repository;

import com.example.springbootjpa01.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if (item.getId() == null) {
            em.persist(item); // 저장하기 전까지는 id가 없으므로 db에 없는 새 객체라고 할 수 있음. 따라서 영속성 컨텍스트에 persist
        } else {
            em.merge(item); // update 비슷한 개념, 이미 영속성 컨텍스트 내에 item이 있는 상황
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

}
