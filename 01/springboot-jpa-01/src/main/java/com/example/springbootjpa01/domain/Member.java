package com.example.springbootjpa01.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty // 화면에서 처리해야 할 검증이 엔티티에 들어가 있음
    private String name;

    @Embedded
    private Address address;

    @JsonIgnore // 양방향 연관관계일 경우 한쪽은 Ignore 처리 (순환참조 방지)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

}
