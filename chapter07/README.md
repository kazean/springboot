# [이론정리] 7. 외부설정과 프로필2
## 1. 프로젝트 설정
- external-read-start > external-read
> spring-boot-starter, lombok, spring-boot-starter-test

## 2. 외부설정 사용 - Enviroment
- 외부설정
```
설정 데이터(application.properties)
OS 환경변수
자바 시스템 속성
커맨드 라인 옵션 인수
```
- 다양한 외부 설정 읽기
> `Environment`는 물론이고 Enviroment를 활용해서 더 편리하게 외부 설정을 읽는 방법
- 스프링이 지원하는 다양한 외부 설정 조회 방법
> `Environment`
> `@Value`
> `@ConfigurationProperties`

- `Enviroment env`
> env.getProperty(key, Type);
- 정리
> typesafe-configuration-properties
> application.properties에 필요한 외부설정 추가, Environment를 통해서 해당 값을 읽어서, MyDataSource를 만들었다.
> 향후 외부설정방식이 바뀌어도 애플리케이션 코드 유지
> !단점, Environment 직접 주입, env.getProperty("key") 값을 꺼내는 과정 반복
>> `@Value`를 통해서 외부 설정값 주입 받는 더욱 편리한 기능

## 3. 외부설정 사용 - @Value
- `@Value("${my.datasource.url}")`
- @Value에 `${}` 사용, 필드 및 파라미터에 사용가능
- 기본값 : `:`뒤에 기본값을 적어주면 된다
>  @Value("${my.datasource.etc.max-connection:1}")

## 4. 외부설정 사용 - @ConfigurationProperties
- `Type-safe Configuration Properties`
> 타입 안전한 설정 속성, 외부 설정의 묶음 정보를 객체로 변환하는 기능, 객체를 사용하면 타입 사용, 잘못된 타입 문제 방지, 객체를 사용하면서 다른 장점
- `@ConfigurationProperties("my.datasource")`, 클래스 단위
- Config class : `@EnableConfigurationProperties(MyDataSourcePropertiesV1.class)` 
> EnableConfigurationProperties(MyDataSourcePropertiesV1.class) 스프링에게 사용할 @ConfigurationProperties를 지정해주어야 한다.
- 정리
> application.properties에 외부 설정을 추가하고 @ConfigurationProperties를 통해 MyDataSourcePropertiesV1에 외부 설정의 값들을 설정했다. 해당 값을 읽어서 MyDataSource를 만들었다.

- 표기법 변환
> 스프링을 캐밥 표기법(-)을 자바 낙타 기법(Camel 대소문자)으로 중간에서 자동으로 변환해준다(ex, property(max-connection) > java maxConnection)
- `@ConfigurationPropertiesScan(package)` @ConfigurationProperties를 특정 범위 자동 등록할때 사용, 개별은 @EnableConfigurationProperties(class)
> !문제 Setter를 가지고 있기 때문에 누군가 실수로 값 변경
>> Setter를 제거하고 생성자를 사용

## 5. 외부설정 사용 - @ConfigurationProperties 생성자
- 생성자 주입사용 
> @ConfigurationProperties("my.datasource")
> @DefaultValue Etc, @DefaultValue("DEFAULT") List<String> options
- cf, @ConstructorBinding
> 스프링부트 3.0 이전에는 생성자 바인딩 시에 @ConstructorBinding 애노테이션을 필수로 사용해야 했다.
> 3.0 부터 생성자 하나일 경우 생략가능
- 정리
> `@ConfigurationProperties의 생성자 주입`을 통해서 값을 읽어들었다
> !문제 타입은 맞는데 숫자의 범위, 문자의 길이 검증
>> 자바 빈 검증기

## 6. 외부설정 사용 - @ConfigurationProperties 검증
- 자바 빈 검증기, @ConfigurationProperties 자바 객체이기 때문에 `자바 빈 검증기`를 사용할 수 있다.
> spring-boot-starter-validation
- `@Validated` `@NotEmpty` `@Min(1)` `@Max(999)` : jakarta.validation(자바표준)
- `@DurationMin(seconds = 1)` `@DurationMax(seconds = 60) Duration` : org.hibernate.validator
- 정리
> @ConfigurationProperties 타입 안전, 외부설정 사용, 검증기, 가장 좋은 예외(컴파일)
- ConfigurationProperties 장점
> 외부설정을 객체로 변환, 외부설정의 계층을 객체로 편리하게 표현, 외부 설정 타입을 안전하게, 검증기

## 7. YAML
- 스프링은 설정 데이터를 사용할 때, application.properties뿐만 아니라 `application.yml`이라는 형식도 사용
- YAML은 `space(공백)`로 계층 구조, 구분기호 `:`
- application.yml 
- 주의, application.properties, application.yml 같이 사용시 application.properties 우선권
> 함께 사용하면 일관성이 없으므로 하나만 사용 (실무 yml 선호)
- yml과 프로필
```
sping
  config
    activate
      on-profile: dev
```
> `---` 논리파일 구분

## 8 @Profile
- 설정값이 다른 정도가 아니라 각 환경마다 `서로 다른 빈 등록해야한다면?`, `@Profile`
- PayConfig
```
@Bean
@Profile("default") : LocalPayClient

@Bean
@Profile("prod") : ProdPayClient
```
- OrderRunner
```
public class OrderRunner implements ApplicationRunner {
    private final OrderService orderService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        orderService.order(1000);

    }
}
```
> AplicationRunner 인터페이스를 사용하면 스프링은 빈 초기화가 모두 끝나고 애플리케이션 로딩이 완료되는 시점에 run(args)메서드를 호출해준다.
- `@Profile` 정체
> @Conditional(ProfileCondition.class)