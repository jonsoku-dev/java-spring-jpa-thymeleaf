package jpabook.jpashop.repository;

import java.util.ArrayList;
import java.util.List;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import org.hibernate.annotations.BatchSize;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        // language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); // 최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    // order와 orderItems를 join하면 데이터가 뻥튀기가된다. (중복데이터 발생)
    // -> distinct를 사용한다. 그런데 이건 DB Query에서는 중복제거가 적용이 되지않는다.
    // -> 애플리케이션에 올라와야 중복이 제거된다. (디비에서 데이터를 받고 jpa가 중복을 제거한다는 의미)
    // -> 하지만 이 방법을 사용하면 페이지네이션이 불가능하다 !!
    // -> 디비에서 중복데이터가 발생하기때문에 데이터양에 대한 신뢰도가 현저히 떨어지기때문에
    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" + // 얘가 문제!!
                        " join fetch oi.item i", Order.class) // 얘가 문제!!
                // limit, offset이 없고, hibernate가 warn을 띄운다.
                // 2020-12-12 15:01:09.359  WARN 50454 --- [nio-8083-exec-1] o.h.h.internal.ast.QueryTranslatorImpl
                // : HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
                // 단점1: 데이터가 뻥튀기된다.
                // 단점2: 메모리에서 페이징처리를 한다. 데이터가 만개가있으면 만개를 어플리케이션에 퍼올린다음 소팅한다.
                // 이럴경우 쿼리 몇개만들어오면 서버가 작살난다.
                // 단점3: collection fetch join은 한개만 가능 !!!! 뎁스가 깊어질수록 데이터베이스는 어떤걸 기준으로 잡아야할지 모르기때문
//                .setFirstResult(1)
//                .setMaxResults(1000)
                .getResultList();
    }


    // xToOne 관계는 모두 fetch join 한다. -> 데이터 뻥튀기와 관계없으니, row수를 증가시키지않기때문에 전혀 문제없음.
    // xToMany는 그대로 냅둔다. (Lazy로 ! Dto 생성자에서 프록시초기화하면됨 -> 쿼리가 그만큼 나가게 됨)
    // 위 문제를 해결하기위해
    // @BatchSize(n) 혹은
    // application.yml에 jpa.properties.default_batch_fetch_size를 넣는다.
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
