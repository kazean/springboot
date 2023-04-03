# [실습] 3-1. 스프링 부트와 내장 실습
## 내장 톰캣1 -설정
- build.gradle
```
plugins {
    id 'java'
}

dependencies {
    //스프링 MVC 추가
    implementation 'org.springframework:spring-webmvc:6.0.4'

    //내장 톰켓 추가
    implementation 'org.apache.tomcat.embed:tomcat-embed-core:10.1.5'
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
> tomcat-embed-core, spring-webmvc
> buildJar, buildFatJar

## 내장 톰캣2 - 서블릿
- hello.embed. EembedTomcatServletMain
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

## 내장 톰캣3 - 스프링
- hello.embed.EmbedTomcatSpringMain
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

## 내장 톰캣4 - 빌드와 배포 1
- jar build
> ./gradlew clean buildJar
>> buildJar build.gradle task
- jar 압축풀기
```
META-INF
    MANIFEST.MF
package
    class
```
> 라이브러리가 전혀 보이지 않는다
>> [ERR]java -jar, WAR와 비교

## 내장 톰캣5 - 빌드와 배포 2
-FatJar
- jar build
- jar 압축푸기
```
META-INF
    MANIFEST.MF
package
    class
+library class
```
> !but, 파일명 중복을 해결할 수 없다, 어던 라이브러리가 포함되어 있는지 확인하기 어렵다

## 편리한 부트 클래스 만들기
-hello.boot.MySpringApplication
```
public static void run(Class configClass,String[] args)
//톰캣 설정
//스프링 컨테이너 생성
AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
appContext.register(configClass);
//스프링 MVC 디스패처 서블릿 생성, 스프링 컨테이너 연결
//디스패처 서블릿 등록
```

-hello.boot.@MySpringBootApplication
```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan
public @interface MySpringBootApplication {
}
```

-HelloConfig 수정
> @Configuration 주석

-MySpringBootAppMain
```
@MySpringBootApplication
public class MySpringBootAppMain {
    public static void main(String[] args) {
        System.out.println("MySpringBootAppMain.main");
        MySpringApplication.run(MySpringBootAppMain.class, args);
    }
}
```
> componentScan을 스프링 컨테이너 등록