package jpabook.jpashop.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;

@SpringBootTest
@Transactional
class ItemServiceTest {
    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;

    @Test
    public void 아이템_저장() throws Exception {
        //given
        Item book = new Book();
        book.setName("존소쿠");

        // when
        Long savedId = itemService.saveItem(book);

        // then
        assertEquals(book, itemRepository.findOne(savedId));
    }

    @Test
    public void 아이템_찾기() throws Exception {
        //given
        Item book = new Book();
        book.setName("존소쿠");

        //when
        Long savedId = itemService.saveItem(book);
        Item foundItem = itemService.findOne(savedId);

        //then
        assertEquals("존소쿠", foundItem.getName());
    }
}
