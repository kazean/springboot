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
> 유지보수학 ㅣ좋은 애플리케이션 개발의 가장 기본 원칙은 변하는 것과 변하지 않는 것을 분리하는 것이다.
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