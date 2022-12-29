package com.example.springbootjpa01.controller;

import com.example.springbootjpa01.domain.item.Book;
import com.example.springbootjpa01.domain.item.Item;
import com.example.springbootjpa01.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("items/new")
    public String createForm(Model model) {

        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    // validation 생략
    @PostMapping("/items/new")
    public String create(BookForm form) {

        Book book = new Book(); // Best Practice : 엔티티 내 생성자 메서드로 파라미터값 넣어서 생성하는 것이 좋음, 강의 편의상
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity((form.getStockQuantity()));
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    @GetMapping("/items")
    public String list(Model model) {

        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {

        Book item = (Book) itemService.findOne(itemId); // 강의 편의상 형 변환
        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        // 인텔리제이 멀티라인 셀렉트 검색해보기

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form) {

        /*
        Book book = new Book();
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book); // em.merge 작동 (ItemRepository 참고)
         */

        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
        // parameter가 많으면 update용 dto로 받을 것것

       return "redirect:/items";
    }

}
