## 3-2. 스프링 부트아 웹 서버 - 프로젝트 생성
-boot-start > boot
-build.gradle
```
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.0.2'
	id 'io.spring.dependency-management' version '1.1.0'
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```
-hello.boot.controller.HelloController
```
@RestController
@GetMapping("/hello-spring")
hello()
```

## 스프링 부트와 웹 서버 - 실행과정
- @SpringBootApplication 애노테이션, SpringApplication.run(BootApplication.class, args)
> 컴포넌트 스캔 등을 포함한 여러 기능 설정
>> 1. 스프링 컨테이너를 생성한다.
>> 2. WAS(내장 톰캣)를 생성한다.

## 스프링 부트와 웹 서버 - 빌드와 배포
- jar 빌드
- 스프링 부트 jar 분석
```
META-INF
    MANIFEST.MF
org/springframework/boot/loader
    *JarLauncher.class
BOOT-INF
    classes
    *lib
    classpath.idx
    layers.idx
```
> Jar를 푼 결과 Fat Jar가 안라 새로운 구조

## 스프링 부트 실행 가능 Jar
- Fat Jar의 단점 보안 > 실행가능 Jar (Executable Jar)
> jar내부에 jar를 포함하고 있기에 어느 라이브러리 포함하고 있는지 확인 가능
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