package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dto만들 때 주의사항 ! Dto로 반환한다해도 정작 Dto 안에 Entity가 존재하면 안된다.
 * 즉, Dto안에서 엔티티가 있다면 이것 또한 Dto로 반환해야함 !
 * 엔티티를 노출하면 엔티티가 수정될때 화면단에도 영향이 크므로, 엔티티가 아닌 화면전용 Dto를 만드는 것이 현명한 방법임.
 */
@Getter
public class OrderDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;

    public OrderDto(Order order) {
        orderId = order.getId();
        name = order.getMember().getName();
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();
        // 프록시 초기화
        // Dto안에 entity가 존재하면 안된다 !
//            order.getOrderItems().stream().forEach(oi -> oi.getItem().getName());
//            orderItems = order.getOrderItems();
        // 엔티티와 화면의 의존성을 끊어야한다. 그러니 내부도 dto로 변환
        orderItems = order.getOrderItems().stream()
                .map(orderItem -> new OrderItemDto(orderItem))
                .collect(Collectors.toList());
    }
}