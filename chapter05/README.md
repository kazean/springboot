# [이론] 5. 자동구성(Auto Configuration)
## 1. 프로젝트 설정
- autoconfig-start > autoconfig
> spring-boot-starter-jdbc, spring-boot-starter-web, org.projectlombok:lombok, com.h2database:h2

## 2. 예제만들기
- Member, DbConfig(`DataSource, TransactionManager, JdbcTeplate`), MemberRepository, MemberRepositoryTest
- 정리
> !JdbcTemplate, DataSource, TransactionManager 항상 스프링 빈으로 등록해야되는 번거로움

## 3. 자동 구성 확인
- config없이 @Autowired DataSource, ...
> 객체 자동구성, 의존관계 주입이 정상 처리된 것을 확인할 수 있다.
- 빈 등록 제거: DbConfig에서 해당 빈들을 등록하지 않고 제거해보자
> @Configuration, @Bean 주석
> 정상 빈 등록
>> `사실 이 빈들은 모두 스프링 부트가 자동으로 등록해 준 것이다.`

## 4. 스프링 부트의 자동 구성
- 스프링 부트는 `자동 구성(Auto Configuration)`이라는 기능을 제공, 일반적으로 자주 사용하는 수 많은 빈들을 자동으로 등록해주는 기능
> 자동 구성 덕분에 개발자는 반복적이고 복잡한 빈 등록과 설정을 최소화, 애플리케이션 개발을 빠르게 수행
- 자동구성 살짝 알아보기
> JdbcTemplateAutoConfiguration, @AutoConfiguration(after= = DataSourceAutoConfiguration.class), @Import({JdbcTemplateConfiguration.class, ...})
>> `@AutoConfiguration`: 자동구성을 사용하라면 이 애노테이션 등록
>>> after 자동구성이 실행되는 순서지정
>> `@ConditionalOnClass`({DataSource.class, JdbcTemplate.class}): if문과 유사한 기능, @ConditionalXXX 시리즈
>> `@Import`: 스프링부트에서 자바 설정을 추가할 때 사용
> JdbcTemplateConfiguration, @Configuration, @ConditionalOnMissingBean(JdbcOperations.class)
- 자동 등록 설정
> 다음과 같은 자동 구성 기능들이 다음 빈들을 등록해준다
```
JdbcTemplateAutoConfiguration: JdbcTemplate
DataSourceAutoConfiguration: DataSource
DataSourceTransacionManagerAutoConfiguration: TransactionManager
```
- 스프링부트가 제공하는 자동구성(Auto Configuration), `spring-boot-auto-configure`
> https://docs.spring.io/spring-boot/docs/current/reference/html/auto-configurationclasses.html
- Auto Configuration - 용어, 자동설정? 자동 구성?
- 자동 설정
> Configuration은 크게 보면 빈들을 자동으로 등록해서 스프링이 동작하는 환경을 자동으로 설정
- 자동 구성
> 스프링도 스프링 실행에 필요한 빈들을 적절히 배치
- 정리
> `@Conditional, @AutoConfiguration` 을 이해해야한다

## 5. 자동 구성 직접 만들기 - 기반 예제
- Memory, JVM에서 메모리 정보를 실시간으로 조회하는 기능

## 6. @Conditional
- 특정 조건일 때만 해당 기능이 활성화 되도록
> 여기서 핵심은 소스코드를 고치지 않고 이런 것이 가능해야 한다는 점이다.
- `@Conditional, <I>Condition`
```
package org.springframework.context.annotation;

public interface Condition {
	boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);
}
```
- MemoryCondition impl Condition
```
implements Condition
matches(){
	context.getEnvironment().getProperty("memory")
}
```
> -Dmemory=on 일 경우 만 동작
- MemoryConfig 수정
> `@Conditional(MemoryCondition.class)`
- cf, 스프링은외부 설정을 추상화해서 Environment로 통합했다


## 7. @Conditional 다양한 기능
- 스프링은 이미 필요한 대부분의 구현체를 만들어 두었다
- MemoryConfig 수정
> 기존 @Conditional 주석, `@ConditionalOnProperty(name = "memory", havingValue = "on")`
- @ConditionalOnXXX
> @ConditionalOnClass/MissingClass
> @ConditionOnBean,MissingBean
> @ConditionalOnPropertiy
> @ConditionalOnResource
> @ConditionalOnWebApplication,NotWebApplication
> @ConditionalOnExpression
- 공식 메뉴얼
> https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration.condition-annotations
- cf, @Conditional 자체는 스픵 부트가 아니라 스프링 프레임워크 기능이다. 스프링 부트는 이기능을 확장해서 @ConditionalOnXxx기능 제공

## 08.순수 라이브러리 만들기
- memory-v1-start > memory
- memory.Memory,MemoryFinder,MemoryController, MemoryFinderTest
- 빌드
> ./gradlew clean build

## 09.순수 라이브러리 사용하기 1
- project-v1-start > project-v1

## 10.순수 라이브러리 사용하기 2
- memory-v1.jar, project-v1 적용
- project-v1/libs
- build.gradle
> implementation files('libs/memory-v1.jar')
- hello.config.MemoryConfig
```
@Bean MemoryFinder, MemoryController
```
- 정리
> 외부 라이브러리 직접 만들고 또 그것을 프로젝트에 라이브러리로 불러서 적용
> !라이브러리 내부에 있는 어떤 빈을 등록해야하는지 알아야 하고, 그것을 또 하나하나 빈으로 등록해야 한다.
>> 이런 부분을 자동으로 처리해주는 것이 바로 스프링 부트 자동 구성(Auto Configuration)이다.

## 11. 자동 구성 라이브러리 만들기
- 프로젝트에 라이브러리를 추가만 하면 모든 구성이 자동으로 처리되도록 해보자
- memory.MemoryAutoConfig
```
@AutoConfiguration
@ConditionalOnPropery(name = "memory", havingValue = "on")
@Bean MemoryFinder, MemoryController
```
> `@AutoConfiguration`: 스프링 부트가 제고앟는 자동 구성 기능을 적용할 때 사용하는 애노테이션
> `@ConditionalOnProperty`

- * 자동 구성 대상 지정
> 파일생성 `resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
>> memory.MemoryAutoConfig
- 빌드

## 12.자동 구성 라이브러리 사용하기1
- project-v1 > project-v2

## 13.자동 구성 라이브러리 사용하기2
- libs/memory-v2.jar
- implementation files('libs/memory-v2.jar')
- JAVA VM OPT -Dmemory=on
- 정리
> 스프링부트가 제공하는 자동 구성 덕분에 복잡한 빈 등록이나 추가 설정 없이 단순하게 라이브러리 추가만으로 사용
> @ConditionalOnXxx 덕분에 유연한 설정
> 스프링부트는 수 많은 자동 구성을 제공한다.

## 14.자동 구성 이해1 - 스프링 부트의 시작
- 스프링 부트는 다음 경로에 있는 파일을 읽어서 스프링 부트 자동 구성으로 사용한다.
> `resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
>> hello.MemoryConfig
> `spring-boot-autoconfigure - org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- 스프링 부트 자동 구성이 동작하는 원리는 다음 순서로 알 수 있다.
> `@SpringBootApplication > @EnableAutoConfiguration > @Import(AutoConfigurationImportSelector.class)`
> !@Import는 주로 스프링 설정 정보(@Configuration)을 포함할 때 사용한다, 그런데 AutoConfigurationImportSelector를 열어보면 @Configuration이 아니다


## 15.자동 구성 이해2 - ImportSelector
- `@Import 정보 추가 2가지 방법`
> 정적(클래스), 동적(ImportSelector)
- 정적인 방법: @Import(클래스)
- 동적인 방법: @Import(ImportSelector) 
> 스프링은 설정 정보 대상을 동적으로 선택할 수 있는 `org.springframework.context.annotation.ImportSelector<I>` 인터페이스를 제공
```
package org.springframework.context.annotation;

public interface ImportSelector {
	String[] selectImports(AnnotationMetadata importingClassMetadata);
	//...
}
```

- ImportSelector 예제(src/test)
- hello.selector.HelloBean
- hello.selector.HelloConfig
```
@bean HelloBean
```
- hello.selector HelloImportSelector
```
implements ImportSelector
> {"hello.selector.HelloConfig"}
```
- hello.selector.ImportSelectorTest
```
@Configuration
@Import(HelloConfig.class)
class StaticConfig

@Configuration
@Import(HelloImportSelector.class)
class SelectorConfig

@Test
staticConfig(), selectorConfig()
```
- selectorConfig(): selectorConfig를 초기 설정 정보를 사용한다.
- selectorConfig: `@Import(HelloImportSelector.class)에서 ImportSelector구현체 사용`
- 스프링은 HelloImportSelector를 실행하고, "hello.selector.HelloConfig"라는 문자를 반환 받는다. 이 문자에 맞는 대상을 설정정보로 사용한다.

- @EnableAutoConfiguration 동작 방식
> @Import(AutoConfigurationImportSelector.class)는 ImportSelector의 구현체
> 실제로 이 코드는 /META-INF/spring/org.springframework.autoconfigure.AutoConfiguration.imports 파일을 확인한다
- 결론
> @SpringBootApplication > @EnableAutoConfiguration > @Import(AutoConfigurationImportSelector.class) > resources/META-INF/spring/org.springframework.autoconfigure.AutoConfiguration.imports

## 16. 정리
- @AutoConfiguration에 자동 구성의 순서를 지정할 수 있다.
- @AutoConfiguration도 설정 파일이다.
> 하지만, 일반 스프링 설정과 라이프사이클이 다르기 때문에 컴포넌트 스캔의 대상이 되면 안된다.
- 파일을 지정해서 사용해야한다
> resources/META-INF/spring/org.springframework.autoconfigure.AutoConfiguration.imports

- 자동 구성을 언제 사용하는가?
> 라이브러리 만들어서 제공할 때 유용

- !남은 문제
> 그런데 이런 방식으로 빈이 자동 등록되면, 빈이 등록할 때 사용하는 설정 정보는 어떻게 변경해야 하는지