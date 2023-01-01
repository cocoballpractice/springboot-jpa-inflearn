package com.example.springbootjpa01.service;

import com.example.springbootjpa01.domain.Member;
import com.example.springbootjpa01.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // spring (javax 보다 더 나음), 읽기 전용 기본 적용됨
@RequiredArgsConstructor
public class MemberService {

    /* 필드 주입
    @Autowired
    private MemberRepository memberRepository;
     */

    /* Setter 주입
    private MemberRepository memberRepository;

    @Autowired
    public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
     */

    private final MemberRepository memberRepository;

    /* 생성자 주입, Lombok 미적용

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
     */

    // 가입
    @Transactional // readOnly = true 미포함
    public Long join(Member member) {
        validateDuplicateMember(member); // 회원 중복 여부 validation
        memberRepository.save(member);
        return member.getId();
    }

    // 아래의 경우는 정말 동시에 insert 할 경우엔 잡아내지 못한다. 따라서 member의 name을 unique로 잡아둬야 함
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("중복된 회원입니다");
        }
    }

    // 회원 리스트 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 단건 조회
    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }

}
