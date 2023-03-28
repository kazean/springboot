# [실습] 6.외부 설정과 프로필1
## 1.프로젝트 생성
- external-start > external
- build.gradle
```
spring-boot-starter
lombok
```

## [이론] 2.외부 설정이란?
- 개발/운영환경 빌드 다르게?
- 실행 시점에 외부 설정값을 주입한다
> 유지보수하기 좋은 애플리케이션 개발의 가장 기본 원칙은 변하는 것과 변하지 않는 것을 분리하는 것이다.
- 외부설정
```
1)OS 환경 변수
2)자바 시스템 속성
3)자바 커멘드 라인 인수
4)외부파일
```

## 3.외부 설정 - OS 환경변수
- 조회방법
> window: set, linux: printenv
- src/test.hello.external.OsEnv
```
System.getenv()
System.getenv(key)
```
> 전역변수와 같은 효과

## 4.외부 설정 - 자바 시스템 속성
- java -D옵션 (VM옵션)
- src/test.hello.external JavaSystemProperties
```
System.getProperties()
System.getPropery(String.valueOf(key))
```
> -Durl=devdb -Dusername=dev_user -Dpassword=dev_pw
- System.setProperty(propertyName, "propertyValue")
> !이 방식은 코드 안에서 사용하는 것이기 때문에 외부로 설정을 분리하는 효과는 없다.

## 5.외부 설정 - 커맨드 라인 인수
- src/test.hello.external.CommadLineV1
```
iter log args
```
- IDE에서 실행시 커멘드 라인 인수 추가
> data1 dataB
- key=value 형식 입력X

## 6.외부 설정 - 커맨드 라인 옵션 인수
```
aaa bbb > 2개
hello world > 2개
"hello word" > 1개
key=value
```
- `커맨드 라인 옵션 인수(command line option arguments)`
- --key=value 형식으로 사용
- src/test.hello.external.CommandLineV2
```
new DefaultApplicationArguments(args)
appArgs.getSourceArgs()
appArgs.getNoOptionArgs()
appArgs.getOptionNames()

getOptionNames iter

getOptionValues : List<Object>
```
> --url=devdb --username=dev_user --password=dev_pw mode=on
> cf, 커맨드 라인 옵션 인수는 자바 언어의 표준 기능이 아니다. 스프링 편리함을 위해 제공하는 기능

## 7.외부 설정 - 커맨드 라인 옵션 인수와 스프링부트
- src/main.hello.CommandLineBean
```
@Component
private fianl ApplicationArguments

@PostContruct
arguments.getSourceArgs()/getOpiotnNames()/OptionNames() + iter getOptionValues(key)
```
> --url=devdb --username=dev_user --password=dev_pw mode=on

## 8. 외부 설정 - 스프링 통합
- !어디에 있는 외부 설정값을 읽어야 하는지에 따라서 각각 일는 방법이 다르다는 단점
- Environment, PropertySource
> key=value형식
```
org.springframework.core.env.PropertySource
> XXXPropertySource(CommandLinePropertySource,SystemEnvironmentPropertySource,...)
~.Environment
> environment.getProperty(key)
```
- src/main.EnvironmentCheck
```
@Slf4j
@Component
private final Environment env;

@PostConstruct
init()
url, username, password
```
> --url=devdb --username=dev_user --password=dev_pw
> -Durl=devdb -Dusername=dev_user -Dpassword=dev_pw
>> 커맨드라인옵션인수, 자바 시스템 속성 모두 Environment를 통해서 동일한 방법으로 읽을 수 있다
- 우선순위
> 1. 더 유연한 것이 우선권을 가진다.
> 2. 범위가 넓은 것 보다는 좁은 것이 우선권을 가진다.
>> 커맨드라인옵션인수 > 자바 시스템 속성

## 9. 설정 데이터1 - 외부 파일
- !OS환경변수, 자바시스템, 커맨드라인옵션인수 값이 늘어날 수록 사용 불편
- 스프링과 설정 데이터
> application.properties, application.yml
- build/libs/application.properties
```
url=dev.db.com
username=dev_user
password=dev_pw
```
> 설정 파일로 관리
> !외부 설정자체 번거로움, 서버 여러대, 설정파일 변경 이력

## 10. 설정 데이터2 - 내부 파일 분리
- !설정 파일 외부 관리 번거로움
- 프로젝트 내부 파일 포함, 빌드 시점 함께 빌드 jar
- application-{profile}.properties
- main/resources/application-dev.properties/prod
```
[dev]
url=dev.db.com
username=dev_user
password=dev_pw

[prod]
url=prod.db.com
username=prod_user
password=prod_pw
```
> [자바시스템, 커맨드라인옵션인수] spring.profiles.active=dev/prod s
> !설정 파일 분리

## 11. 설정 데이터3 - 내부 파일 합체
- !설정 파일 분리해서 관리시 한눈에 전체가 들어오지 않는 단점
- 물리적인 하나의 파일 안에 논리적으로 영역을 구분하는 방법
- application.properties
> #---, !---
- application.yml
> ---
- spring.config.activate.on-profile에 프로필 값 지정
- main/resources/application.properties
```
spring.config.activate.on-profile=dev
url=dev.db.com
username=dev_user
password=dev_pw
#---
spring.config.activate.on-profile=prod
url=prod.db.com
username=prod_user
password=prod_pw
```
- 주의 속성파일 구분 기호에는 선행 공백이 없어야 하며 정확히 3개의 하이픈, 위 아래 주석X
> 파일 하나에 통합해서 관리

## 12. 우선순위 - 설정 데이터
- 프로필 지정 안할 시 > default profile
- 기본값 
- 스프링은 문서를 위에서 아래로 순서대로 읽음
- 프로필 지정 대체
- 속성 부분 적용

## 13. 우선순위 - 전체
- 자주 사용하는 우선순위
```
설정 데이터(application.properties)
OS 환경 변수
자바 시스템 속성
커맨드 라인 옵션 인수
@TestPropertySource
```
- 설정 데이터 우선순위
```
jar 내부
jar 내부 application-{profile}.properties
jar 외부
jar 외부 application-{profile}.proerties
```
- 우선순위 이해방법
> 더 유연한 것 우선권, 범위가 넓은 것 보다 좁은 것이 우선
- 추가 또는 변경 되는 방식
> 조회관점에서는 덮는 것처럼, 우선순위가 높은 값이 조회
- 정리 우선순위에 따라서 설정을 추가하거나 변경하는 방식은 상당히 편리하면서도 유연한 구조를 만들어줌