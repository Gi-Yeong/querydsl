package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();
    }

    @Test
    public void startJPQL() throws Exception {
        //given
        // member1 을 찾아라.
        Member findByJPQL = em.createQuery("select m From Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        //when

        //then
        assertThat(findByJPQL.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQueryDsl() throws Exception {
        //given

        //when
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1")) // 파라미터 바인딩 처리
                .fetchOne();

        //then
        assertThat(findMember != null ? findMember.getUsername() : null).isEqualTo("member1");
    }

    @Test
    public void 검색_조건_쿼리() throws Exception {
        //given
        Member findMember = queryFactory.selectFrom(member)
                .where(member.username.eq("member1")
                        .and(member.age.eq(10)))
                .fetchOne();

        //when

        //then
        assertThat(findMember != null ? findMember.getUsername() : null).isEqualTo("member1");
    }

    @Test
    public void 검색_조건_쿼리_andParam() throws Exception {
        //given
        Member findMember = queryFactory.selectFrom(member)
                .where(member.username.eq("member1"), (member.age.eq(10)))
                // , 도 and 조건으로 들어간다. 이 방법은 중간에 null 들어가면 무시한다. 동적 쿼리를 생성할 때 좋다.
                .fetchOne();

        //when

        //then
        assertThat(findMember != null ? findMember.getUsername() : null).isEqualTo("member1");
    }

}
