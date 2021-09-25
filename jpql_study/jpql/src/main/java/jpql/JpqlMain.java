package jpql;

import javax.persistence.*;
import java.util.List;

public class JpqlMain {
    public static void main(String[] args) {
        //엔티티 매니저 팩토리 생성
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("meme");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        //start
        tx.begin();
        //-------
//        Member member = new Member();
//        member.setUsername("MEM1");
//        member.setAge(30);
//        em.persist(member);
        for(int i=1; i<101; i++){
            Member member = new Member();
            member.setUsername("member"+i);
            member.setAge(i);
            em.persist(member);
        }

        em.flush();
        em.clear();
        List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();
        System.out.println("result.size() = " + result.size());
        for(Member member1 : result){
            System.out.println("member = " + member1);
        }



        //-------
        //close
        tx.commit();
        em.close();
        emf.close();


    }


}