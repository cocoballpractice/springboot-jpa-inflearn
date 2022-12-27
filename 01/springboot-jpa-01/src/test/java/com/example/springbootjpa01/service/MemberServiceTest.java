package com.example.springbootjpa01.service;

import com.example.springbootjpa01.domain.Member;
import com.example.springbootjpa01.repository.MemberRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    // @Transaction은 테스트 시 기본적으로 commit이 아닌 rollback을 하므로 테스트 상에서 commit을 하지 않아
    // 실제 insert는 되지 않음(영속성 컨텍스트 persist 이후 commit이 되어 flush가 발생하여야 DB 상 insert)
    // 만약 실제로 DB에 들어가는 것을 보고 싶다면 @Rollback(false) 부착

    @Test
    public void 회원가입() throws Exception {

        // Given
        Member member = new Member();
        member.setName("kim");

        // When
        Long savedId = memberService.join(member);

        // Then
        assertEquals(member, memberRepository.findOne(savedId));

    }

    @Test(expected = IllegalStateException.class) // 발생되어야 할 exception
    public void 중복_회원_예외() throws Exception {

        // Given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // When
        memberService.join(member1);
        memberService.join(member2); // 예외 발생되어야 함

        // Then
        fail("예외가 발생하지 않았음");

    }

}