# springboot_jpa1
스프링부트 + JPA 활용1
![1](https://user-images.githubusercontent.com/57438644/133923641-f68f49ca-7ac2-480e-b27d-f09126bb9fc5.jpg)


### 프로젝트 환경설정

- Spring Boot
- JPA
- Gradle
- Thymeleaf
- HIBERNATE

### 목차

1. 프로젝트 생성
2. 라이브러리 살펴보기
3. View 환경설정
4. H2 데이터베이스 설치
5. JPA와 DB설정, 동작확인

# 1. 프로젝트 환경설정

- 스프링 부트 스타터([https://start.spring.io/](https://start.spring.io/))
- 사용기능 : web, thymeleaf, jpa, h2, lombok
    - groupId: jpabook
    - artifactId: jpashop

# 2. 도메인 분석 설계

1. 요구사항 분석(쇼핑몰)
    1. 회원 
        - 회원 등록
        - 회원 조회
    2. 상품
        - 상품 등록
        - 상품 조회
        - **상품 수정**
    3. 주문
        - 주문 등록
        - 주문 조회
        - **주문 취소**
    4. 기타 요구사항
        - 상품은 제고관리 필요
        - 상품의 종류 : 도서, 음반, 영화
        - 상품을 카테고리로 구분
        - 상품 주문시 배송 정보 입력할 수 있음
2. 도메인 모델과 테이블 설계
- 1:N 이면, N쪽에 1의 외래키가 있다

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/1e379440-7f4b-4830-a1f9-0bc9a150cf60/Untitled.png)

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/59d53a6b-a66c-40bc-97ff-8f5e9ee33ddc/Untitled.png)

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/2f1f0def-4a5c-448e-b34f-428e16b18b99/Untitled.png)

### 엔티티 설계시 주의점

1. 엔티티에는 가급적 Setter 금지
2. 모든 연관관계는 **지연로딩(LAZY)** 설정 ★
    - 즉시로딩(EAGER)은 예측 어렵, SQL추적 어려움. **N+1문제**
    - 즉시로딩 : 하나 가져오면 연관된 DB엔티티들 다 가져옴
    - (fetch = FetchType.LAZY) 추가 (ManyToOne, OneToOne : **XToOne**)
3. **cascade = CascadeType.ALL** ★연쇄
    - persist(orderItemA), persist(orderItemB), persist(orderItemC), persist(order)
    - persist(order)  한번만으로 가능

### 패키지 구조

- jpabook, jpashop
    - domain
    - exception
    - repository
    - service
    - web

### 1. domain

1. DB 테이블 생성

- Member
- Order (Member, Delivery, OrderItem)
- Delivery
- Item ( Book, Album, Movie )
- Category
1. 어노테이션
    - **Entity**
    - Embeddable  (address)
    - 상속(Item)
        1. 부모
            - @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
            - @DiscriminatorColumn(name="dtype")
        2. 자식
            - @DiscriminatorValue("M")

### 2. repository

1.  (em사용 → **domain 접근) 함수**
    - EntityManager
        1. persist : DB 테이블에 **엔티티 추가**
        2. merge : 이미 존재하면 추가(업데이트)
        3. find(테이블.class, PK) : select * from 테이블 where PK =id
        4. createQuery("쿼리", 테이블.class) : JPAQuery → select m from Member m
            - setParameter("name", name)
            - getResultList()
2. 어노테이션
    - **Repository**
    - RequiredArgsConstructor

### 3. service

1. Repository에 접근하는 함수
    - ItemService
    - MemberService
    - OrderService
    - OrderSearch
2. 어노테이션
    - **Service**
    - Transactional
    - RequiredArgsConstructor

### 4. controller

1. web함수
    - HomeController
2. 어노테이션
    - Controller
        - RequestMapping
    - Slf4j       // log 사용
    1. 매핑 : return html명
        - GetMapping
        - PostMapping

### 어노테이션 모음. [ @ ]

@Test : 테스트

@BeforeEach : 테스트를 실행하기 전 실행

- @Configuration : 설정정보, 싱글톤 보장
- 방법1 : @Bean : 스프링 빈(컨테이너) 등록
- 방법2 : @ComponentScan : 스프링 빈 등록 (@Component, @Controller, @Service, @Repository, @Configuration)
    - @Component 붙은 클래스 스캔
    - @Autowired : 생성자 위에 사용. 자동 의존관계. = getBean(MemberRepository.class)
    1. 옵션처리 ( 주입할 대상 없을때 )
        - @Autowired(required=false) : 메서드 자체가 호출X
        - org.springframework.lang.@Nullable : null 입력
        - Optional<> : Optional.empty 입력
    2. 생성자 자동생성 ( final 붙어야함 )
        - **@RequiredArgsConstructor**
    3. 타입 매칭
        - Qualifier("이름")

@DisplayName : 단순 함수 설명

---

**@GetMapping(실행주소)** : HTTP GET요청하기 위함.  [return (넘겨줄 **주소명** in template)]

1. 테이블 정의

**@Entity** : DB의 TABLE과 매핑 ]       : **@Table(name="orders")**

1. Primary Key 매핑
    - **@id** : Primary Key 매핑
    - **@GeneratedValue** : Primary Key 자동생성(1, 2, 3...)
2. 제약조건
    - **@Column(name=컬럼명) :** 컬럼에 다양한 제약조건 (unique, nullable, length, columnDefinition 등)
    - **@JoinColumn(name=외래키컬럼명)** : **연관관계 주인 (FK키가 가까운 곳에서 설정)  —ⓛ**
3. 관계**(mappedBy = "member_id") 연관관계 주인아님  —ⓛ**
    - **@Embedded** : @Embeddable 사용한 것에
    - **@OneToMany** : 1(기준):N,
    - **@ManyToOne** : N(기준):1
    - **@Enumerated(EnumType.STRING)** : enum 사용

**@Embeddable** : Entity속 클래스 (Member의 Address)

1. 테이블 사용

**@PersistenceContext** : EntityManager를 사용하기 위함

```java
private EntityManager em;
em.persist(member);                   // 1. 저장
member1 = em.find(Member.class, id);  // 2. 찾기
```

**@Transactional** : DB연동 메서드에 사용

---

### 스프링 테스트

1. class 위
    - @RunWith(SpringRunner.class) : 스프링 관련 테스트라는것 알림 (JUnit4)
    - @SpringBootTest
2. class 내부
    1. 생성자
        - @Autowired : 생성자 앞 사용
    2. 메서드
        - @Test : 메서드 앞 사용
        - @Transactional : DB사용하는 메서드에 적용
            - @Rollback(false)  : 테스트 확인하고 싶을때만. 롤백x

### 함수 모음

- Model : HashMap 형태(Key, value)

    ```java
    String t = "model interface"
    model.addAttribute("test", t)
    // view 파일에서 출력
    <h2> ${test} <h2>
    ```

### 변경 감지와 병합

- 준영속엔티티 : 영속성 컨텍스트(JPA)가 더는 관리하지 않는 엔티티

    ex) 수정 : Book book = new Book(), book.setId(form.getId)

1. **변경 감지 기능 사용(권장)** : 원하는 속성만 선택해 변경
2. 병합 사용 : 모든 속성 변경

### 단축키

- **변수 추출 : Ctrl + alt + V**
- 파라미터 추출 : Ctrl + alt + P
- 코드 생성자 : alt + Insert   → Getter, Setter, RequiredArgsConstuctor
