# [이론] 02. 웹 서버와 서블릿 컨테이너
## 1. 웹서버와 스프링부트 소개
- 전통적인 방식
> WAR형식 빌드 > WAS전달해서 배포하는 방식
> !IDE에서도 WAS와 연동해서 실행되도록 복잡한 추가 설정
- 최근방식
> `내장 톰캣 포함`, `실행가능 JAR빌드`

## 2. 톰캣 설치
- 자바 17버전, 스프링 3.0 
```
[mac]
sudo lsof -i :8080
sudo kill -9 <PID>
[window]
netstat -ano | findstr :포트번호
taskkill /f /pid 프로세스번호
```

## 3. 프로젝트 설정
> Project: server-start > server
- build.gradle
> `jakarta.servlet:jakarta.servlet-api:6.0.0`: 서블릿 등록시 필요 라이브러리
```
plugins {
    id: 'java'
    id: 'war'
}
```
> WAR생성 plugin

- 서블릿등록: `HttpServlet` `@WebServelt(urlPatterns = "/test")`
```
@WebServelt(urlPatterns = "/test")
extends HttpServlet
protected service()
```

## 4. War빌드와 배포
- 프로젝트 빌드: ./gradlew build
```
plugins {
    id 'java'
    id 'war'
}
```
- JAR, WAR 간단소개
> JAR(Java Archive), 직접 실행시 main() 메소드 필요: `MANIFEST.MF` 파일에 실행할 메인 메서드가 있는 클래스를 지정해두어야한다.
> WAR(Web Application Archive), WAR는 웹 어플리케이션 서버위에서 실행된다.
- war 구조
```
WEB-INF
    /classes
    /lib
    /web.xml
index.html(static resource)
```

## 5. IntelliJ 톰캣 설정
- Run > Edit Configuration > Tomcat Server > Server/Deployment(exploded)
## 6. 서블릿 컨테이너 초기화1
> 서비스에 필요한 필터와 서블릿 등록
> 스프링을 사용한다면 스프링 컨테이너를 만들고, 서블릿과 스프링을 연결하는 디스패처 서블릿도 등록해야 한다.
>> WAS가 제공하는 초기화 기능 사용하면 이런 기능을 WAS 실행시점에 초기화할 수 있다.
>> 과거에는 web.xml을 활용하여 초기화, 지금은 서블릿 스펙에서 초기화한다.

- 서블릿 컨테이너 초기화 개발: `ServletContainerInitializer` 초기화 인터페이스를 제공한다, 초기화 메서드 onStartup()
```
interface ServletContainerInitializer
public void onStartUp(Set<Class<?>> c, ServeltContext cctx) throws ServletException;
```
> `Set<Class<?>> c`조금더 유연한 초기화 기능을 제공, `@HandlesTypes` 애노테이션과 함께 사용
> ServletContext: 서블릿 컨테이너 자체의 기능을 제공한다.
- 구현 MyContainerInitV1 impl ServletContainerInitializer
- 추가로 초기화 클래스를 알려줘야 한다, 초기화 클래스 지정: `resources/META-INF/services/jakarta.servlet.ServletContainerInitializer`
```
resources/META-INF/services/jakarta.servlet.ServletContainerInitializer
hello.container.MyContainerInitV1
```
> WAS 실행하여 결과 확인

## 7. 서블릿 컨테이너 초기화2
> 서블릿을 등록하는 두가지 방법
```
1. @WebServlet
2. 프로그래밍 방식
```
- HelloServlet extends HttpServlet
- [애플리케이션 초기화] AppInit<I>
```
void onStartUp(ServletContext servletContext)
```
- 구현 AppInitV1Servlet (AppInit)
```
ServletRegistration.Dynamic helloServlet = servletContext.addServlet("helloServlet", new HelloServlet());
helloServlet.addMapping("/hello-servlet");
```
- 구현 MyContainerInitV2: `@HandlesTypes(AppInit.class)`, `ServletContainerInitializer`
```
@HandlesTypes(AppInit.class)
implements ServletContainerInitializer

onStartUpvoid onStartup(Set<Class<?>> c, ServletContext ctx)
AppInit appInit = (AppInit) appInitClass.getDeclaredConstructor().newInstance(); //reflection
appInit.onStartUp(ctx);
```
- 등록 MyContainerInitV2: `resources/META-INF/services/jakarta.servlet.ServletContainerInitializer`
```
resources/META-INF/services/jakarta.servlet.ServletContainerInitializer
hello.container.MyContainerInitV1
hello.container.MyContainerInitV2
```
- cf, 프로그래밍 방식을 사용하는 이유: 유연성(조건, 분기 등)
- 정리 
> `서블릿 컨테이너 초기화` resources/META-INF/services/jakarta.servlet.ServletContainerInitializer > MyContainerInit
> `애플리케이션 초기화` 특정 인터페이스 지정 @HandlesTypes(AppInit.class)


## 8. 스프링 컨테이너 등록
> 스프링 컨테이너 만들기
> 스프링MVC 컨틔롤러를 스프링 컴테이너에 빈 등록하기
> 디스패처 서블릿을 서블릿 컨테이너에 등록하기
- 스프링 MVC 라이브러리 추가: `org.springframework:spring-webmvc:6.0.4'` + spring core포함
- 스프링 컨트롤러 생성 HelloController: `@RestController`, `@GetMapping("/hello-spring)`
- 스프링 설정파일 생성 HelloConfig: `@Configuration`
- 스프링 애플리케이션 초기화 생성 AppInitV2Spring
```
//스프링 컨테이너 생성
AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
appContext.register(HelloConfig.class);

//스프링 MVC 디스패처 서블릿 생성, 스프링 컨테이너 연결
DispatcherServlet dispatcher = new DispatcherServlet(appContext);
ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcherV2", dispatcher);

// /spring/* 요청이 디스패처 서블릿을 통하도록 설정
servlet.addMapping("/spring/*");
```
> AnnotationConfigWebApplicationContext(), .register(Config.class): 스프링 컨테이너, 컨테이너에 스프링설정 추가
> new DispatcherServlet(appContext): 스프링MVC 디스패처 서블릿 생성, 스프링컨테이너 연결
> ctx.addServlet("dispatcher", dispatcher): ServletRegistration.Dynamic: 디스패처 서블릿을 서브릿컨테이너 연결
> dynServlet.addMapping("/");: 해당요청이 디스패처 서블릿을 통하도록 설정
> !주의, 서블릿 등록할 때 이름은 원하는 이름을 등록하면 되지만 같은 이름으로 중복 등록하면 오류발생.

## 9. 스프링 MVC 서블릿 컨테이너 초기화 지원
- 스프링MVC는 이러한 서블릿 컨테이너 초기화 작업을 이미 만들어 두었다. `WebApplicationInitializer`<I>
```
package org.springframework.web;

public interface WebApplicationInitializer {
	void onStartup(ServletContext servletContext) throws ServletException;
}
```
- AppInitV3SpringMvc 구현
> impl WebApplicationInitializer, AnnotationConfigWebApplicationContext, DispatcherServlet, stx.addServlet(), dynServlet.addMapping()
- 스프링이 제공하는 초기화 컨테이너 분석
```
WebApplicationInitializer

META-INF/services/jakarta.servlet.ServletContainerInitializer
org.springframework.web.SpringServletContainerInitializer

@HandlesTypes(WebApplicationInitializer.class)
public class SpringServletContainerInitializer implements ServletContainerInitializer
```