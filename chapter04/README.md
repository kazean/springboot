# [이론] 4. 스프링 부트 스타터와 라이브러리 관리
- 라이브러리 관리의 어려움
> 라이브러리 선택, 버전 호환성

## 1. 라이브러리 직접관리
- 라이브러리 관리의 어려움
> 각 라이브러리끼리 호환이 잘되는 버전, 잘안되는 버전
>> plugins `io.spring.dependency-management`

- 라이브러리 직접지정
```
//스프링 웹 MVC
implementation 'org.springframework:spring-webmvc:6.0.4'
//내장 톰캣
implementation 'org.apache.tomcat.embed:tomcat-embed-core:10.1.5'
```
> 스프링 웹 MVC, 내장 톰캣, JSON처리, LOG관련, YML관련
- !라이브러리 직접선택시 발생하는 문제
> 수 많은 라이브러리를 알아야한다.
> 라이브러리 버전까지 골라서 선택(호환성)

## 2. 스프링 부트 라이브러리 버전 관리
- `io.spring.dependency-management` 플러그인
- build.gradle
```
plugins {
    id 'org.springframework.boot' version '3.0.2'
    id 'io.spring.dependency-management' version '1.1.0'
    id 'java'
}
//2. 스프링 부트 라이브러리 버전 관리
//스프링 웹, MVC
implementation 'org.springframework:spring-webmvc'
//내장 톰캣
implementation 'org.apache.tomcat.embed:tomcat-embed-core'
```
- dependency-management 버전 관리
```
io.spring.dependcy-management플러그인을 사용하면 spring-boot-dependencies에 있는 다음 bom 정보를 참고한다.
spring-boot-dependencies는 스프링 부트 gradle 플러그인에서 사용하기 때문에 개발자의 눈에 의존관계로 보이지 않음
```
- 버전 정보 bom(Bill Of Materials)
> https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/springboot-dependencies/build.gradle
>> cf, 스프링 부트가 관리하지 않는 라이브러리

- 정리
> 스프링부트가 제공하는 버전관리, 스프링을 포함해서 많은 외부 라이브러리의 버전을 최적화
> 개발자는 스프링 부트 자체 버전만 지정하면 된다.

## 3. 스프링 부트 스타터
- 스프링 웹 MVC, 내장 톰캣, JSON, 스프링 부트 관련, LOG, YML등 다양한 라이브러리
- org.springframework.boot:spring-boot-starter-web
> 사용하기 편하게 의존성을 모아둔 세트(web mvc, embed-tomcat, json, springboot, log, yml, ..)
- 스프링 부트 스타터 - 이름 패턴
> 공식: spring-boot-starter-*
> 비공식: thirdpartyproject-spring-boot-starter
- 스프링 부트 스타터의 전체 목록
> https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.build-systems.starters
- 라이브러리 버전 변경 
```
ext['tomcat.version'] - "10.1.4"
```
>> Version Properties: https://docs.spring.io/spring-boot/docs/current/reference/html/dependencyversions.html#appendix.dependency-versions.properties