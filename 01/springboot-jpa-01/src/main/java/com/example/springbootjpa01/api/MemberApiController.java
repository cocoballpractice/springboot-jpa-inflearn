package com.example.springbootjpa01.api;

import com.example.springbootjpa01.domain.Member;
import com.example.springbootjpa01.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 아래의 케이스는 WORST 케이스
     * 파라미터를 엔티티로 받아버릴 경우 화면에서 검증을 해야 하는 것을 엔티티가 직접 수행하게 됨
     * 또한 특정 서비스에 맞게 엔티티를 수정할 경우 다른 서비스의 API 스펙도 같이 바뀌게 되어버림 (side effect)
     * 따라서 파라미터를 엔티티가 아닌 Dto로 받아야 한다
     */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) { // TODO: 매개값 : 엔티티에서 추후 변경

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 파라미터를 Dto로 변경
     * Dto에서 Validation을 수행할 수 있음 (엔티티에서 굳이 하지 않아도 됨)
     * 엔티티를 수정하더라도 API 스펙은 바뀌지 않는다
     */
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        Member member = new Member();
        member.setName(request.name);
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }


    /**
     * 별도의 클래스로 만들지는 않고 내부 클래스로 간편하게 설정
     */

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {

        private Long id;
        private String name;

    }

    @Data
    static class UpdateMemberRequest {
        @NotEmpty
        private String name;
    }

}
