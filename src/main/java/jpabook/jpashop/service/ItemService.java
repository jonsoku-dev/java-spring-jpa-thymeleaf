package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional(readOnly = false)
    public Long saveItem(Item item) {
        itemRepository.save(item);
        return item.getId();
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}

// 이것처럼 repository로 위임만하는 서비스같은 경우에는...
// 굳이 개발하지않고, 컨트롤러에서 바로 레포지토리에 접근하여 써도 무방하다고 생각한다.
