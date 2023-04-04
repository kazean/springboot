# [실습] 05-02 자동구성
## 12.자동 구성 라이브러리 사용하기1
- settings.gradle
> project-v1 > project-v2
- hello.controller.HelloController

## 13.자동 구성 라이브러리 사용하기2
- libs/memory-v2.jar
- build.gradle
```
implementation files('libs/memory-v2.jar')
```
- JAVA VM OPT -Dmemory=on

## 14.자동 구성 이해1 - 스프링 부트의 시작
- 스프링 부트는 다음 경로에 있는 파일을 읽어서 스프링 부트 자동 구성으로 사용한다.
> resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
>> hello.MemoryConfig
> spring-boot-autoconfigure - org.springframework.boot.autoconfigure.AutoConfiguration.imports
- 스프링 부트 자동 구성이 동작하는 원리는 다음 순서로 알 수 있다.
> @SpringBootApplication > @EnableAutoConfiguration > @Import(AutoConfigurationImportSelector.class)


## 15.자동 구성 이해2 - ImportSelector
- @Import 정보 추가 2가지 방법
> 정적(클래스), 동적(ImportSelector)
- org.springframework.context.annotation.ImportSelector<I> 인터페이스
```
String[] selectImports(AnntationMetadata importingClassMetadata);
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