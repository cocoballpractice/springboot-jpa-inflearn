package com.example.springbootjpa01.api;

import com.example.springbootjpa01.domain.Address;
import com.example.springbootjpa01.domain.Order;
import com.example.springbootjpa01.domain.OrderItem;
import com.example.springbootjpa01.domain.OrderStatus;
import com.example.springbootjpa01.repository.OrderRepository;
import com.example.springbootjpa01.repository.OrderSearch;
import com.example.springbootjpa01.repository.order.query.OrderFlatDto;
import com.example.springbootjpa01.repository.order.query.OrderItemQueryDto;
import com.example.springbootjpa01.repository.order.query.OrderQueryDto;
import com.example.springbootjpa01.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * OneToMany 관계
 ** Order <- OrderItems <- Item
 * ~ToOne 관계
 ** Order -> Member
 ** Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        /*
        for (Order order : orders) {
            System.out.println("order ref=" + order + " id=" + order.getId()); // 레퍼런스, id가 동일한 order가 중복으로 나감
        }
         */

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        // ~toOne 관계를 페치 조인하여 가져오는 메소드, member / delivery는 한번에 조회 (1회차 때 이미 조회하여 2회차부터는 조회 X)
        // 페이징 처리가 가능 (
        // 일대다 관계로 설정된 orderItems 수만큼, 그리고 orderItems 내의 item 수만큼 쿼리가 발생
        // hibernate.default_batch_fetch_size 옵션으로 최적화 진행, size만큼 in 쿼리를 날려서 한 번에 하위 엔티티들을 가져옴

        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return collect;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
        // 쿼리는 루트 1번, 컬렉션은 N번 실행 (ToOne 관계를 한 번, OneTo~ 관계를 N번)
        // ToOne 관계는 row 수를 증가시키지 않음 -> 한 번에 조회
        // ToMany 관계는 조인 시 row 수가 증가함 -> 최적화가 어려우므로 별도의 메소드로 조회
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        // OrderFlatDto -> OrderQueryDto로 변환 + 중복 Order 제거
        // Order 기준으로는 페이징 불가
        // DB에서 중복 데이터를 거르지 않고 애플리케이션에 전달하기 때문에 처리 속도가 느려질 수 있음
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }


    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        // private List<OrderItem> orderItems; Dto 안에는 엔티티가 있으면 안됨. 엔티티를 wrapping 해서도 안됨
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }

    }
}
