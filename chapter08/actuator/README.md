# 8. 액츄에이터
## 1. 프로덕션 준비 기능이란
- 서비스에 문제가 없는지 모니터링하고 지표들을 심어서 감시하는 활동들이다.
- `프로덕션 준비 기능` 프로덕션을 운영에 배포할 때 준비해야 하는 비 기능적 요소들을 뜻한다
```
지표(Metric), 추적(Trace), 감사(Aduit)
모니터링
```
- `액츄에이터` 스프링부트가 제공, 마이크로미터, 프로메테우스, 그라파나 같은 최근 유행하는 모니터링 툴과 연동 기능

## 2. 프로젝트 설정
- actuator-start > actuator
- build.gradle
> Spring-web, Data JPA, Actuator, H2, Lombok

## 3. 액츄에이터 시작
- build.gradle, `org.springframework.boot:spring-boot-starter-actuator`
```
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```
> http://localhost:8080/actuator
> http://localhost:8080/actuator/health
- 액츄에이터 기능을 웹에 노출, application.yml
```
management:
  endpoints:
    web:
      exposure:
        include: "*"
```
> http://localhost:8080/actuator
> 액츄에이터가 제공하는 수 많은 기능, `엔드포인트`: 기능 하나하나
>> /actuator/{endpoint}

## 4. 엔드포인트 설정
- `엔드포인트 사용하려면`
1. 엔드포인트 활성화(on,off)
2. 엔드포인트 노출(Web,JMX)
> 대부분 기본 활성화, (shutdown제외) 노출이 되있지 않을뿐
- shutdown endpoint 활성화, application.yml
```
management:
	endpoint:
		shutdown:
			enabled: true
```
> `management.endpoint.{endpoint}.enabled=true`
> POST http://localhost:8080/actuator/shutdown
- ex, 엔드포인트 노출
```
management:
  endpoints:
		jmx:
			exposure
				include: "health,info"
    web:
      exposure:
        include: "*"
				exclude: "env,bean"
```

## 5. 다양한 엔드포인트
- bean, conditions, configprops, env, health, httpexchanges, info, loggers, metrics, mappings, threaddump, shutdown
> `health, httpexchanges, info, loggers, metrics`
- cf, 전체 엔드포인트
> https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints

## 6. 헬스정보
- `http://localhost:8080/actuator/health`
> 단순히 애플리케이션 요청에 응답판단을 넘어서 데이터베이스가 응답하는지, 디스크 사용량에 문제가 없는지 같은 다양한 정보
- 헬스정보 자세히/간략히 보기
> `management.endpoint.health.show-details/show-components=always`
- 헬스 이상 상태
> 하나라도 문제가 있으면 DOWN
- cf, 액츄에이터는 db, mongo, redis, diskspace, ping 수 많은 헬스 기능 제공
- 자세한 헬스 기본 지원기능
> https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints.health.auto-configured-health-indicators
- 헬스 기능 직접 구현하기
> https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints.health.writing-custom-health-indicators

## 7. 애플리케이션 정보
- `info 엔드포인트`는 애플ㄹ리케이션의 기본정보 노출
```
java, os
env : Environment에서 info. 로 시작하는 정보
build : META-INF/build-info.properties
git : git.properties
```
> java, os, env 기본적으로 비활성화 
> http://localhost:8080/actuator/info
- `java, os`
> `management.info.<id>.enabled= true`
>> 주의! management.endpoint 하위가 아니다

- `env`
> `management.info.env.enabled= true`
```
info.app.name: hello-actuator
info.app.company: yh
```
> info. 관련정보 추가
> http://localhost:8080/actuator/info

- build
> build.gradle `springBoot Extension > buildInfo()`
```
springBoot {
	buildInfo()
}
```
>> 빌드시 resources/main/META-INF/`build-info.properties` 생성
>> http://localhost:8080/actuator/info

- git
> build.gradle plugin `gradle-git-properties`
```
plugins {
	id "com.gorylenko.gradle-git-properties" version "2.4.1" //git info
}
```
>> 물론 git으로 관리되고 있어야한다. 그렇지 않으면 빌드시 오류
>> build폴더안 `resources/main/git.properteis` 파일
>> http://localhost:8080/actuator/info
> git에 대한 자세한 정보 추가
>> `management.info.git.mode= "full"`
- info 사용자 정의 기능 추가
> https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints.info.writing-custom-info-contributors

## 8. 로거
- `loggers 엔드포인트` 로깅과 관련된 정보 확인, 실시간 변경
- hello.controller.LogController
```
@Get("/log")
log.trace~error
```
- application.yml
> logging.level.hello.controller= debug
>> `http://localhost:8080/actuator/loggers`
- 스프링부트 기본 INFO, 하위도 INFO
- hello.controller configuredLevel: debug
- 더 자세히 조쇠하기
> http://localhost:8080/actuator/loggers/{로거이름}
- 실시간 로그 레벨 변경
```
POST http://localhost:8080/actuator/loggers/{로거이름}
Content-Type: json
{
	"configuredLevel": "TRACE"
}
```
> trace로 변경

## 9. HTTP 요청 응답기록
- HTTP 요청과, 응답의 과거 기록 확인 `httpexchanes` 엔드포인트
- `HttpExchangeRepository` 인터페이스 구현체를 빈 등록해야 함
> 스프링부트 기본제공 InMemoryHttpExchangeRepository
- ActuatorApplication
> @Bean InMemoryHttpExchangeRepository
> `http://localhost:88080/actuator/httpexchanges`
>> 참고로 이 기능은 매우 단순하고 제한이 많아 개발단계에서만 사용, 운영 핀포인트, ZipKin

## 10. 액츄에이터와 보안
- 보안주의
> 너무 많은 내부 정보 노출 외부망보단 내부망에서만
- 액츄에이터를 다른 포트에서 실행
> `management.server.port=9292`
- 액츄에이터 URL 경로에 인증 설정
> `management.endpoints.web.base-path="/manage"`