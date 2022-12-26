package com.example.springbootjpa01.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // 내장 타입
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    protected Address() {} // 외부에서 직접 호출하지 못하게

    // 값 타입을 변경하지 못하게 생성자에서 값을 모두 초기화
    public Address (String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
