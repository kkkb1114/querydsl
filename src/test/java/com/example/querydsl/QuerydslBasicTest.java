package com.example.querydsl;

import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.QMember;
import com.example.querydsl.entity.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.example.querydsl.entity.QMember.*;
import static com.example.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @Autowired
    private EntityManager entityManager;

    private JPAQueryFactory jpaQueryFactory;

    // 모든 테스트 진행 전에 동작할 메서드를 지정하는 어노테이션
    @BeforeEach
    public void before() {
        jpaQueryFactory = new JPAQueryFactory(entityManager);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        entityManager.persist(teamA);
        entityManager.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        entityManager.persist(member1);
        entityManager.persist(member2);
        entityManager.persist(member3);
        entityManager.persist(member4);
    }

    @Test
    public void startJPQL() {
        //member1을 찾기
        Member member = entityManager.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", "member1")
                .getSingleResult();

        Assertions.assertThat(member.getName()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() {
        //member1을 찾기

        Member findMember = jpaQueryFactory
                .select(member)
                .from(member)
                .where(member.name.eq("member1"))
                .fetchOne();

        Assertions.assertThat(findMember.getName()).isEqualTo("member1");
    }

    // 검색
    @Test
    public void search() {
        Member findMember = jpaQueryFactory
                .selectFrom(member)
                .where(member.name.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();

        Assertions.assertThat(findMember.getName()).isEqualTo("member1");
        Assertions.assertThat(findMember.getAge()).isEqualTo(10);
    }

    @Test
    public void resultFetch(){
        List<Member> memberList1 = jpaQueryFactory
                .selectFrom(member)
                .fetch();

        Member memberOne1 = jpaQueryFactory
                .selectFrom(member)
                .fetchOne();

        Member fetchFirst = jpaQueryFactory
                .select(member)
                .fetchFirst();

        QueryResults<Member> result = jpaQueryFactory
                .select(member)
                .fetchResults();

        result.getTotal();
    }

    /**
     * <정렬>
     * 1. 회원 나이 내림차순 (desc)
     * 2. 회원 이름 올림차순 (asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력
     */
    @Test
    public void sort(){
        entityManager.persist(new Member(null, 100));
        entityManager.persist(new Member("member6", 100));
        entityManager.persist(new Member("member7", 100));

        List<Member> memberList = jpaQueryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.name.asc().nullsLast())
                .fetch();

        Assertions.assertThat(memberList.get(0).getName()).isEqualTo("member6");
        Assertions.assertThat(memberList.get(1).getName()).isEqualTo("member7");
        Assertions.assertThat(memberList.get(2).getName()).isNull();
    }

    @Test
    public void paging1(){
        QueryResults<Member> memberList = jpaQueryFactory
                .selectFrom(member)
                .orderBy(member.id.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        Assertions.assertThat(memberList.getTotal()).isEqualTo(4);
        Assertions.assertThat(memberList.getOffset()).isEqualTo(1);
        Assertions.assertThat(memberList.getLimit()).isEqualTo(2);
    }
}