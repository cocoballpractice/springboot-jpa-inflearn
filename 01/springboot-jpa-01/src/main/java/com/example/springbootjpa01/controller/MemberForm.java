package com.example.springbootjpa01.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {

    // 실제로는 멤버 생성용 Dto

    @NotEmpty(message = "회원 이름은 필수")
    private String name;

    private String city;
    private String street;
    private String zipcode;

}
