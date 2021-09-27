# springboot_jpa
스프링부트 + JPA 활용한 쇼핑몰사이트 만들어보기
![image](https://user-images.githubusercontent.com/57438644/134947319-75bf5fe1-4f54-45de-8a62-b2b30c52f4c4.png)

### 프로젝트 환경설정

- Spring Boot
- JPA
- Gradle
- Thymeleaf
- HIBERNATE


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
- 
![2](https://user-images.githubusercontent.com/57438644/133923817-6e3f7172-050e-4d71-bf61-e7198e2b2257.png)
![3](https://user-images.githubusercontent.com/57438644/133923816-e99693c6-8a5a-4b32-b561-17c7cb4de4ee.png)
![4](https://user-images.githubusercontent.com/57438644/133923814-442fdaae-d796-4a59-9408-e176568cfd1e.png)

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
# API 개발과 성능 최적화

### api 패키지

- 엔티티X
- DTO로 받기 → @RequestBody @Valid + 새로 등록한 함수
- 어노테이션
    - RestController : @Controller + @ResponseBody
    - RequiredArgsConstructor

### API 개발고급

- 조회용 샘플 데이터 입력
- 지연로딩과 조회 성능 최적화
- 컬렉션 조회 최적화
- 페이징과 한계 돌파
- OSIV와 성능 최적화

### 지연로딩과 조회성능최적화 (XToOne)

1. 연관관계 (xToOne)인 경우
    - 한쪽에 @JsonIgnore 추가
    - 조회시 Lazy 강제 초기화

    ```java
    public List<Order> ordersV1(){
            List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
            for (Order order : all){
                order.getMember().getName();  // Lazy 강제 초기화
                order.getDelivery().getAddress(); // Lazy 강제 초기화
            }
            return all;
        }
    ```

2. DTO로 조회하는 경우 → 조회는 DTO로 조회X
    - N+1문제 발생 (쿼리가 최악의 경우 2N(배송 → 주문자, 주문상품)+1)
3. fetch join
    - 쿼리가 1번 나옴
4. DTO로 직접 조회
- 쿼리 방식 선택 권장 순서
    1. 우선 엔티티를 DTO로 변환하는 방법을 선택
    2. 필요하면 fetch join으로 성능 최적화 → 대부분 해결
    3. 그래도 안되면 DTO로 직접 조회하는 방법 선택
    4. 최후는 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접 사용

    ### 컬렉션 조회 최적화 (OneToMany)

    1. 엔티티 직접노출 - v1
        - 함수호출 → Lazy 강제초기화
        - 사용X
    2. DTO 사용  - v2
        - List → DTO
        - orderItems.stream().forEach(o -> o.getItem().getName());
    3. fetch join 쿼리 + DTO 사용 → v3
        - 쿼리 1번 사용
        - 페이징 불가 : 일대다 → 데이터가 예측할 수 없이 증가
            - setFirstResult(1)
            - setMaxResults(100)

        **→ 해결법 ( 컬렉션 + 페이징 문제 )**

        1. ToOne(ManyToOne, OneToOne) 먼저 join fetch   → row수에 영향X
        2. **컬렉션은 지연로딩(LAZY)**으로 조회
        3. 지연로딩 성능최적화
            - hibernate.default_batch_fetch_size : 글로벌설정
            - @BatchSize : 개별최적화

    ### 권장순서

    1. 엔티티 조회 방식으로 우선접근
        - join fetch로 쿼리 수 최소화
        - 컬렉션 최적화
            - 페이징 필요 : hibernate.default_batch_fetch_size, @BatchSize
            - 페이징 필요X → join fetch 사용
    2. 엔티티 조회 방식으로 해결안되면 DTO 조회방식 사용
    3. DTO 조회 방식으로 해결안되면, NativeSQL, 스프링 JdbcTemplate

### OSIV ON

: Open EntityManger in View : JPA

- spring.jpa.open-in-view: **true 기본값**
- 트랜잭션처럼, 최초 DB 커넥션 시작시점부터 API응답이 끝날 때 까지 **영속성 컨텍스트와 DB커넥션을 유지**. → 지연로딩 가능
- 지연로딩 조건
    - 영속성 컨텍스트
    - DB 커넥션 유지
- 치명적인 단점
    - 너무 오랜시간 DB커넥션 리소스를 사용 → Application에서 커넥션이 모자랄 수 있다(장애로 이어짐)

### OSIV OFF

- 트랜잭션을 종료할 때 영속성 컨텍스트를 닫고, DB 커넥션도 반환 → 커넥션 리소스 낭비하징낳음
- 지연로딩
    - 트랜잭션 안에서 처리해야 함 → 강제 호출
    - view template에서 지연로딩이 동작하지 않음
- 복잡성 관리법
    - Command와 Query를 분리
    - 비즈니스 로직 < 쿼리(최적화 중요)
    - EX) OrderService
        - OrderService : 핵심 비즈니스 로직
        - OrderQueryService : 화면이나 API에 맞춘 서비스 (읽기전용 트랜잭션)

### OSIV 결론

- 고객 서비스 실시간 API : OSIV OFF
- ADMIN처럼 많이 사용하지 않는 곳 : OSIV ON
