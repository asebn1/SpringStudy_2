package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository  // 스프링 빈으로 관리
@RequiredArgsConstructor
public class MemberRepository {
    // @PersistenceContext -> @Autowired 가능 -> @RequiredArgsConstructor
    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id); // 타입, PK
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)   // JPAQuery
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name=:name", Member.class)
                .setParameter("name", name)   // :name에 name을 넣음
                .getResultList();
    }
}
