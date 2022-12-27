package com.example.querydsl;

import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
class QuerydslApplicationTests {

	@Autowired
	EntityManager entityManager;

	@Test
	void contextLoads() {
		Member member1 = new Member("김기범1");
		entityManager.persist(member1);

		JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
		QMember qMember = QMember.member;

		Member member2 = jpaQueryFactory
				.selectFrom(qMember)
				.fetchOne();

		Assertions.assertThat(member1).isEqualTo(member2);
	}
}
