package com.example.springbootjpa01.api;

import com.example.springbootjpa01.domain.Address;
import com.example.springbootjpa01.domain.Order;
import com.example.springbootjpa01.domain.OrderStatus;
import com.example.springbootjpa01.repository.OrderRepository;
import com.example.springbootjpa01.repository.OrderSearch;
import com.example.springbootjpa01.repository.order.simplequery.SimpleOrderQueryDto;
import com.example.springbootjpa01.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order
 * Order -> Member
 * Order -> Delivery
 * xToOne 관계 (ManyToOne, OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화 -> 프록시가 아니라 실제 객체를 조회하기 시작
            order.getDelivery().getAddress(); // Lazy 강제 초기화 -> 프록시가 아니라 실제 객체를 조회하기 시작
        }
        /**
         * 첫번째 이슈 : 엔티티 직접 노출 시 양방향 연관관계 순환 참조 발생 (이를 막기 위해 한쪽은 JsonIgnore 처리)
         * 두번째 이슈 : ByteBuddyInterceptor 관련 오류 발생
         * (지연 로딩으로 설정된 엔티티는 DB에서 가져오는 것이 아니라 프록시 객체로 만들어두고 프록시를 가져옴.
         * 이를 ByteBuddyInterceptor에서 처리하는데 문제는 이 인터셉터가 프록시 객체를 json으로 만드는 방법을 모름. -> 오류가 발생함
         * 이를 해결하기 위해서는 별도의 모듈 설치 필요 (Hibernate5Module, 부트 3.0 미만 버전)
         * 그냥 이런 문제가 있다 정도만 알아둘 것 (애초에 엔티티 직접 반환하면 안되므로)
         */
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {

        // ORDER 2개
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> results = orders.stream()
                .map(o-> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return results;
        /**
         * v1, v2 공통적인 문제 : DB에 쿼리가 굉장히 많이 나감 (order, member, delivery 3개의 테이블을 조회)
         * ORDER -> SQL 1번 -> 결과 주문 수 2개 -> 1회차 처리 (3개의 테이블 조회) -> 2회차 처리 (3개의 테이블 조회)
         * 즉 N+1 문제 발생 -> 1 + 회원 (N) + 배송 (N) (최악의 경우)
         * 이를 해결하기 위해 페치 조인 필요
         */
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {

        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> results = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return results;
    }

    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
        /**
         * SELECT 절이 v3보다 간략화되어 조금 더 조회 성능이 개선됨
         * v3와 v4는 Trade-off 관계
         * v3는 엔티티를 조회하기 때문에 SELECT 절이 조금 복잡하지만 엔티티와 관련된 비즈니스 로직 처리를 추가로 하는 등 재사용이 가능함
         * v4는 조회 성능이 조금 더 개선되었으나 해당 Dto에 특화된 SQL을 직접 날리기 때문에 재사용성이 떨어짐
         * 기존 OrderRepository는 엔티티와 엔티티 그래프를 조회하는 목적이므로 v4에서 Repository를 접근하는 것은 적절하지 않음
         * 따라서 v4를 위한 별도의 Repository를 만듬 (화면 처리에 특화된)
         */
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // Lazy 초기화 -> DB를 직접 조회
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // Lazy 초기화 -> DB를 직접 조회
        }
    }

}
