# [이론] 3. 스프링 부트와 내장 실습
## 1. WAR배포방식의 단점
- 톰캣 같은 WAS를 별도로 설치해야 한다.
- 애플리케이션 코드를 WAR로 배포해야 한다.
- 빌드한 WAR파일을 WAS에 배포 해야한다.
- 개발환경 설정이 복잡한다.
- 고민
> 톰캣을 마치 하나의 라이브러리 처럼 포함해서 사용 `내장톰캣(enbeded tomcat)`


## 2. 내장 톰캣1 -설정
- build.gradle
```
plugins {
    id 'java'
}
//일반 Jar 생성
task buildJar(type: Jar) {
    manifest {
        attributes 'Main-Class': 'hello.embed.EmbedTomcatSpringMain'
    }
    with jar
}

//Fat Jar 생성
task buildFatJar(type: Jar) {
    manifest {
        attributes 'Main-Class': 'hello.embed.EmbedTomcatSpringMain'
    }
    duplicatesStrategy = DuplicatesStrategy.WARN
    from { configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) } }
    with jar
}
```
> `org.springframework:spring-webmvc:6.0.4`, `org.apache.tomcat.embed:tomcat-embed-core:10.1.5` 
> buildJar, buildFatJar

## 3. 내장 톰캣2 - 서블릿
- 서블릿 톰캣설정, EembedTomcatServletMain
```
//        톰캣설정
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector();
        connector.setPort(8080);
        tomcat.setConnector(connector);

//        서블릿 등록
        Context context = tomcat.addContext("", "/");
        tomcat.addServlet("", "helloServlet", new HelloServlet());
        context.addServletMappingDecoded("/hello-servlet", "helloServlet");
        tomcat.start();
```
> 톰캣설정, 내장 톰캣을 생성하고, 커넥터를 사용해서 8080포트에 연결한다.
> 서블랫등록
>> 톰캣에 사용할 contextPath와 docBase를 지정한다. tomcat.addContext("", "/");
>> tomcat.addServelt() 서블렛 등록
>> context.addServletMappingDecoded() 등록한 서블릿의 경로를 매핑한다.
> 톰캣시작, tomcat.start()

> 내장톰캣을 사용한 덕분에 IDE에 별도의 복잡한 톰캣 설정없이 main()메서드만 실행하면 톰캣까지 매우 편리하게 실행

## 4. 내장 톰캣3 - 스프링
- 스프링 톰캣설정, EmbedTomcatSpringMain
```
//        톰캣설정
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector();
        connector.setPort(8080);
        tomcat.setConnector(connector);


//        스프링 컨테이너생성
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(HelloConfig.class);

//        스프링 MVC 디스패처 서블릿 생성, 스프링 컨테이너 연결
        DispatcherServlet dispatcher = new DispatcherServlet(appContext);

//        디스패처 서블릿 등록
        Context context = tomcat.addContext("", "/");
        tomcat.addServlet("", "dispatcher", dispatcher);
        context.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();
```
> 서블릿등록, dispatcher

## 5. 내장 톰캣4 - 빌드와 배포 1
- 자바의 main()메소드를 실행하기 위해서는 jar형식으로 빌드해야 한다.
- jar build
> ./gradlew clean <task:buildJar>
>> <task:buildJar> build.gradle
- jar 압축풀기
```
META-INF
    MANIFEST.MF
package
    class
```
> 라이브러리가 전혀 보이지 않는다
> !jar파일은 jar를 포함할 수 없다.
>> [ERR]java -jar, WAR와 비교

## 6. 내장 톰캣5 - 빌드와 배포 2
- FatJar
- jar build: ./gradlew clean buildFatJar
- jar 압축풀기
```
META-INF
    MANIFEST.MF
package
    class
+library class
```
- Fat Jar정리
> Fat Jar 장점
>> Fat Jar덕분에 하나의 jar파일에 필요한 라이브러리 내장
>> 내장 톰캣 라이브러리를 jar내부에 내장
>> 하나의 jar파일로 배포부터, 웹 서버 설치+실행
> !Fat Jar 단점
>> 파일명 중복을 해결할 수 없다
>> 어떤 라이브러리가 포함되어 있는지 확인하기 어렵다

## 7. 편리한 부트 클래스 만들기
- 내장톰캣실행, 스프링컨테이너생성, 디스패처서블릿등록의 모든 과정을 편리하게 처리해주는 나만의 부트 클래스 만들기
- `MySpringApplication`
```
public static void run(Class configClass,String[] args)
//톰캣 설정
//스프링 컨테이너 생성
AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
appContext.register(configClass);
//스프링 MVC 디스패처 서블릿 생성, 스프링 컨테이너 연결
//디스패처 서블릿 등록
```
> configClass: 스프링 설정을 파라미터로 전달 받는다
> 이외 스프링컨테이너, 서블릿, 톰캣 등록과정
- @MySpringBootApplication
```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan
public @interface MySpringBootApplication {
}
```
> `@ComponentScan`
- HelloConfig 수정
> @Configuration 주석
- MySpringBootAppMain
```
@MySpringBootApplication
public class MySpringBootAppMain {
    public static void main(String[] args) {
        System.out.println("MySpringBootAppMain.main");
        MySpringApplication.run(MySpringBootAppMain.class, args);
    }
}
```
> 패키지위치가 중요 hello에 위치, `@MySpringBootApplication에 컴포넌트 스캔이 추가`, 현재와 하위 패키지 컴포넌트 스캔 대상
> `MySpringApplication.run(설정정보, args)`로 실행
> 이기능을 사용하면 개발자는 `@MySpringBootApplication 애노테이션`과 `MySpringApplication.run()` 메소드만 기억하면 된다.
> 이렇게 하면 내장 톰캣 실행, 스프링 컨테이너 생성, 디스패처 서블릿, 컴포넌트 스캔까지 모든 기능이 한번에 편리하게 동작한다.

- 스프링부트
> 지금까지 만든 것을 라이브러리로 만들어서 배포한다 > 그것이 스프링부트이다.
```
@SpringBootApplication
public class BootApplicaiton{
    main(){
        SpringApplication.run(BootApplication.class, args)
    }
}
```

## 8. 스프링 부트와 웹 서버 - 프로젝트 생성
- 스프링부트는 지금까지 고민하던 문제를 깔끔하게 해결해준다.
> 내장톰캣을 사용해서 빌드와 배포를 편리하게
> 빌드시 하나의 Jar를 사용하면서, 동시에 FatJar의 문제도 해결
> 내장톰캣 서버를 실행하기 위한 복잡한 과정 모두 자동으로 처리
- boot-start > boot
- build.gradle
```
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.0.2'
	id 'io.spring.dependency-management' version '1.1.0'
}
```
> `org.springframework.boot:spring-boot-starter-web`
- HelloController
```
@RestController
@GetMapping("/hello-spring")
hello()
```
- 내장톰캣 의존관계 확인
> spring-boot-starter-web을 사용하면 내부에서 내장톰캣을 사용
- 라이브러리 버전
> 스프링부트를 사용하면 라이브러리 뒤에 버전정보가 없는것 확인, 현재 부트버전에 가장 적절한 외부 라이브러리 버전을 자동으로 선택

## 9. 스프링 부트와 웹 서버 - 실행과정
- @SpringBootApplication 애노테이션, SpringApplication.run(BootApplication.class, args)
> 컴포넌트 스캔 등을 포함한 여러 기능 설정
>> 1. 스프링 컨테이너를 생성한다.
>> 2. WAS(내장 톰캣)를 생성한다.
- 스프링부트 내부에서 스프링 컨테이너를 생성하는 코드
> ServletWebServerApplicationContextFactory: createContext()
- 스프링부트 내부에서 내장 톰캣을 생성하는 코드
> TomcatServletWebServerFactory: WebServer getWebServer(ServletContextInitializer... initializers)

## 10. 스프링 부트와 웹 서버 - 빌드와 배포
- 스프링부트 jar 빌드
- 스프링 부트 jar 분석
```
META-INF
    MANIFEST.MF
org/springframework/boot/loader
    *JarLauncher.class: 스프링부트위 main()실행 클래스
BOOT-INF
    classes
    *lib
    classpath.idx
    layers.idx
```
> Jar를 푼 결과 Fat Jar가 안라 새로운 구조

## 11. 스프링 부트 실행 가능 Jar
- Fat Jar의 단점 보안 > 실행가능 Jar (Executable Jar)
> jar내부에 jar를 포함하는 특별한 구조를 가지고 있기에 어느 라이브러리 포함하고 있는지 확인 가능
> a.jar, b.jar 내부에 같은 경로의 파일이 있어도 둘다 인식 가능
- Jar 실행정보  META-INF/MANIFEST.MF
```
Manifest-Version: 1.0
Main-Class: org.springframework.boot.loader.JarLauncher
Start-Class: hello.boot.BootApplication
Spring-Boot-Version: 3.0.2
Spring-Boot-Classes: BOOT-INF/classes/
Spring-Boot-Lib: BOOT-INF/lib/
Spring-Boot-Classpath-Index: BOOT-INF/classpath.idx
Spring-Boot-Layers-Index: BOOT-INF/layers.idx
Build-Jdk-Spec: 17
```
> Main-Class: org.springframework.boot.loader.JarLauncher
> Start-Class: hello.boot.BootApplication
>> JarLauncher가 jar 내부에 jar를 읽어들이는 기능, 특별한 구조에 맞게 클래스 정보 읽어들이는 기능
>> 이후에 Start-class에 지정된 main()을 호출한다

- 스프링 부트 로더
- BOOT-INF
- 실행 과정 정리
```
1. java -jar xxx.jar
2. MANIFEST.MF
3. JarLauncher.main()
    BOOT-INF/classes/
    BOOT-INF/lib/
4. BootApplication.main()
```
> cf, IDE에서 직접 실행할 대에는 BootApplication.main()을 바로 실행한다 
>> IDE가 필요한 라이브러리르 모두 인식할 수 있게 도와주기 때문에 JarLauncher가 필요하지 않다.