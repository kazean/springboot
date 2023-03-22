# 02. 웹 서버와 서블릿 컨테이너
## 톰캣 설치
```
자바 17버전, 스프링 3.0 
sudo lsof -i :8080
sudo kill -9 <PID>
```

## 프로젝트 설정
> Project: server-start > server
- build.gradle 설정
```
build.gradle 
jakarta.servlet:jakarta.servlet-api:6.0.0
```
- index.html: /src/main/webapp/
- 서블릿등록 hello.servlet.TestServlet 
```
extends HttpServlet
protected service()
@WebServelt(urlPatterns = "/test")
```

## War빌드와 배포
- 프로젝트 빌드: ./gradlew build
```
plugins {
    id 'java'
    id 'war'
}

/build/libs/~.war
```
- war 톰캣 배포
```

```

- war 구조
```
WEB-INF
    /classes
    /lib
    /web.xml
index.html(static resource)
```

## IntelliJ 톰캣 설정
- Run > Edit Configuration > Tomcat Server > Server/Deployment

## 서블릿 컨테이너 초기화1
> 서비스에 필요한 필터와 서블릿 등록
> 스프링을 사용한다면 스프링 컨테이너를 만들고, 서블릿과 스프링을 연결하는 디스패처 서블릿도 등록해야 한다.
>> WAS가 제공하는 초기화 기능 사용하면 이런 기능을 WAS 실행시점에 초기화할 수 있다.
>> 과거에는 web.xml을 활용하여 초기화, 지금은 서블릿 스펙에서 초기화한다.

- 서블릿 컨테이너 초기화 개발
```
interface ServletContainerInitializer
public void onStartUp(Set<Class<?>> c, ServeltContext cctx) throws ServletException;
```

- 구현 hello.container.MyContainerInitV1
```
public class MyContainerInitV1 implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        ~
    }
}
```

- 초기화 클래스 지정
```
resources/META-INF/services/jakarta.servlet.ServletContainerInitializer
hello.container.MyContainerInitV1
```
> WAS 실행하여 결과 확인

## 서블릿 컨테이너 초기화2
> 서블릿을 등록하는 두가지 방법
```
1. @WebServlet
2. 프로그래밍 방식
```
- hello.servlet.HelloServlet
> mapping X
```
extends 
resp.getWriter().println("hello servlet!");
```

- [애플리케이션 초기화]
- AppInit Interface hello.container
```
void onStartUp(ServletContext servletContext)
```
- 구현 AppInitV1Servlet (AppInit)
```
ServletRegistration.Dynamic helloServlet = servletContext.addServlet("helloServlet", new HelloServlet());
helloServlet.addMapping("/hello-servlet");
```

- 구현 hello.servlet.MyContainerInitV2 
```
@HandlesTypes(AppInit.class)
implements ServletContainerInitializer

onStartUp
AppInit appInit = (AppInit) appInitClass.getDeclaredConstructor().newInstance();
appInit.onStartUp(ctx);
```

- 등록 MyContainerInitV2 
```
resources/META-INF/services/jakarta.servlet.ServletContainerInitializer
hello.container.MyContainerInitV1
hello.container.MyContainerInitV2
```

## 정리 
- `서블릿 컨테이너 초기화` resources/META-INF/services/jakarta.servlet.ServletContainerInitializer > MyContainerInit
- `애플리케이션 초기화` 특정 인터페이스 지정 @HandlesTypes(AppInit.class)


## 스프링 컨테이너 등록
> 스프링 컨테이너 만들기
> 스프링MVC 컨틔롤러를 스프링 컴테이너에 빈 등록하기
> 디스패처 서블릿을 서블릿 컨테이너에 등록하기

- 스프링 MVC 라이브러리 추가
```
build.gradle
implementation 'org.springframework:spring-webmvc:6.0.4'
```

- 스프링 컨트롤러 생성 hello.spring.HelloController
```
@RestController
@GetMapping("/hello-spring")
```

- 스프링 설정파일 생성 hello.spring.HelloConfig
```
@Configuration
@Bean // helloController
```

- 스프링 애플리케이션 초기화 생성 hellop.container.AppInitV2Spring
```
new AnnotationConfigWebApplicationConteext()
.register(HelloConfig.class)

new DispatcherServlet(appContext);

servlcetContext.addServlet("dispatcherV2", dispatcher)
.addMapping("/spring/*")

```

## 스프링 MVC 서블릿 컨테이너 초기화 지원
- `WebApplicationInitializer`
- hello.container.AppInitV3SpringMvc 구현
```
implements WebApplicationInitializer
onStartUp() 구현
```
- 스프링이 제공하는 초기화 컨테이너 분석
```
WebApplicationInitializer

META-INF/services/jakarta.servlet.ServletContainerInitializer
org.springframework.web.SpringServletContainerInitializer

@HandlesTypes(WebApplicationInitializer.class)
public class SpringServletContainerInitializer implements ServletContainerInitializer
```