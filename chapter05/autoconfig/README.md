# [실습] 05 자동구성
## 1. 프로젝트 설정
- autoconfig-start > autoconfig
- build.gradle
```
spring-boot-starter-jdbc
spring-boot-starter-web
org.projectlombok:lombok
com.h2database:h2
```

## 2. 예제만들기
- hello.member.Member
```
memberId
name
```

- hello.config.DbConfig
```
DataSource = new HikariDataSource()
TransactionManager = new JDbcTransactionManager(datasource())
JdbcTemplate = new JdbcTemplate(datasource)
```

- hello.member.MemberRepository
```
JdbcTemplate
void initTable()
void save(Member member)
Member find(String memberId)
List<Member> findAll()
```

- test hello.memberMemberRepositoryTest
```
member table create
member insert
member find
Assertions
```

## 3. 자동 구성 확인
- test hello.config.DbConfigTest
```
@Autowire
DataSource, TransactionManager, Jdbctemplate

checkBean()
```
> DbConfig @Configuration 주석후 테스트
>> 객체 자동구성

## 4. 스프링 부트의 자동 구성
## 5. 자동 구성 직접 만들기 - 기반 예제
- memory.Memory
```
used, max
```
- memory.MemoryFinder
```
public Memory get()
init() // PostConstructor
```
- memory.MemoryController
```
("/memory") system() : Memory
```

- hello.config.MemoryConfig
```
@Bean
MemoryController, MemoryFinder
```

## 6. @Conditional
- @Conditional, <I>condition
- memory.MemoryCondition 
```
implements Condition
```
> -Dmemory=on 일 경우 만 동작
- MemoryConfig 수정
```
@Conditional(MemoryCondition.class)
```

## 7. @Conditional 다양한 기능
- 스프링은 이미 필요한 대부분의 구현체를 만들어 두었다
- MemoryConfig 수정
> 기존 @Conditional 주석, @ConditionalOnProperty(name = "memory", havingValue = "on")
- @ConditionalOnXXX
> @ConditionalOnClass/MissingClass
> @ConditionOnBean,MssingBean
> @ConditionalOnPropertiy
> @ConditionalOnWebApplication,NotWebApplication
> @ConditionalOnExpression