package com.example.springbootjpa01.service;

import com.example.springbootjpa01.domain.item.Book;
import com.example.springbootjpa01.domain.item.Item;
import com.example.springbootjpa01.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public Item findOne(Long id) {
        return itemRepository.findOne(id);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);
        // set 대신에 필드 변경 관련 비즈니스 로직 메소드 사용하는 것이 좋음 (changeName 등...)
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);

        // @Transaction 에 의해 commit이 발생하고, 영속성 컨텍스트는 이 시점에서 dirty-checking을 수행,
        // 변경된 내용을 찾아서 update를 진행함
    }

}
