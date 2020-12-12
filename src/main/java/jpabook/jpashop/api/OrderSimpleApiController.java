package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * xToOne (ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 * 이 때, collection인 orderItem들은 Lazy로딩으로 처리된다. 그러므로 메모리상에서 프록시초기화를 해야한다.
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    /**
     * Order -> Member -> Order -> Member -> ... 무한루프에 빠진다.
     * 해결방법
     * Hibernate https://mvnrepository.com/artifact/com.fasterxml.jackson.datatype/jackson-datatype-hibernate5/2.12.0
     * 로딩되도록 한 후에 프록시 추가한다.
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
//        // 프록시를 강제 초기화한다.
//        for (Order order : all) {
//            order.getMember().getName();
//            order.getDelivery().getAddress();
//        }
        return all;
    }
}
