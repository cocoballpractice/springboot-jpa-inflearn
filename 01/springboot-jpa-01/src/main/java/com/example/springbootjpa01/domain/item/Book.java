package com.example.springbootjpa01.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B") // 싱글 테이블 상에서 구분값
@Getter
@Setter
public class Book extends Item {

    private String author;
    private String isbn;
}
