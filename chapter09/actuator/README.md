# 9.마이크로미터, 프로메테우스, 그라파나
## 1.마이크로미커 소개
- 그라파나 대시보드, 핀포인트
- 모니터링 툴에 지표 전달
- 모니터링 툴 변경
> !기존에 측정했던 코드를 모두 변경한 툴에 맞도록 다시 변경해야된다
>> 이런 문제를 해결하는 것이 바로 `마이크로미터(Micrometer)` 라이브러리이다.
- 마이크로미터 추상화
> micrometer JMX 구현체, micrometer프로메테우스 구현체
- 마이크로미터 전체 그림
> 마이크로미터 = `애플리케이션 메트릭 파사드`, 메트릭을 마이크로미터가 정한 표준 방법으로 모아서 제공
> 추상화를 통해서 구현체를 갈아끼움 ex) SLF4J

## 2. 메트릭 확인하기
- 각각의 지표 수집하여 마이크로 미터가 제공하는 표준 방법에 따라 등록
> 마이크로미터 수집기능 제공, `스프링부트 액츄에이터는 마이크로미터가 제공하는 자료 수집을 @AutoConfiguration을 통해 자동구성`
- metrics엔드 포인트
> http://localhost:8080/actuator/metrics, 자세히 확인하기 /actuator/metrics/{name}
> ~/actuator/metrics/jvm.memory.used
- Tag 필터
> tag:area, values[heap, nonheap]
>> tag=KEY:VALUE 형태로 필터사용
>> ex) ~/actuator/metrics/jvm.memory.used?tag=area:heap
- http 요청수 확인
> ~/actuator/metrics/http.server.requests
- log요청만 필터링
> ~/actuator/metrics/http.server.requests?tag=uri:/log
- log요청, Http status =200
> ~/actuator/metrics/http.server.requests?tag=uri:/log&tag=status:200

## 3. 다양한 메트릭
- `jvm 관련 메트릭` jvm. 으로 시작
> 메모리 및 버퍼 풀 세부정보, 가비지 수집 관련 통계, 스레드 활용 등
- `시스템 메트릭` system., process., disk.
> CPU지표, 파일 디스크립터 메트릭, 가동 시간 메트릭, 사용 가능한 디스크 공간
- `애플리케이션 시작 메트릭`
> application.started.time : 애플리케이션 시작하는데 걸리는 시간
> application.ready.time : 요청을 처리할 준비가 되는데 걸리는 시간
> ApplicationStartedEvent
> ApplicationReadyEvent
- @`스프링 MVC 메트릭`, 스프링 MVC 컨트롤러가 처리하는 모든 요청, http.server.requests
> TAG를 사용해서 다음 정보를 분류할 수 있다.
>> uri, method, status, exception, outcome(상태코드 그룹을 모아 확인)
- `데이터소스 메트릭`, Datasource, 커넥션풀에 관한 메트릭, jdbc.connection
- `로그 메트릭` logback.evemts
> trace, debug, info, warn, error
- `톰캣 메트릭` tomcat.
> 톰캣 메트릭 사용하라면 다음 옵션 켜야된다. application.yml
```
server:
	tomcat:
		mbeanregistry:
			enabled: true
```
>> 톰캣의 최대 쓰레드, 사용 쓰레드 수
- 기타
> HTTP 클라이언트 메트릭, 캐시 메트릭, 작업실행과 스케줄 메트릭, 스프링 데이터 리포지토리 메트릭, 몽고DB 메트릭, 레디스 메트릭
- 사용자 정의 메트릭
- cf, 다양한 메트릭 공식문서
> https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics.supported

## 4. 프로메테우스와 그라파나 소개
- 프로메테우스
> 메트릭을 지속해서 수집하고 DB에 저장
- 그라파나
> 사용자가 보기 편하게 보여주는 대시보드

## 5. 프로메테우스 - 설치
- https://prometheus.io/download/
> ./prometheus 
>> 시스템환경설정 > 보안 및 개인정보보호 > 일반 > 사용차단해제
> http://localhost:9090

## 6. 프로메테우스 - 애플리케이션 설정
- 애플리케이션의 메트릭을 수집하도록 연동
1. 애플리케이션 설정 : 프로메테우스 포멧에 맞추어 메트릭 만들기
2. 프로메테우스 설정 : 프로메테우스가 애플리케이션의 메트릭 수집하도록 설정
- 1. 애플리케이션 설정
> 프로메테우슨 /actuator/metrics의 JSON포맷은 이해하지 못한다.
>> 마이클 미터가 해결
- build.gradle (`micrometer-registry-prometheus`)
```
implementation 'io.micrometer:micrometer-registry-prometheus' //추가
```
> 스프링부트와 액쵸에이터가 자동구성으로 마이크로미터 프로메테우스 구현체를 등록해 동작하도록 설정
>> http://localhost:8080/actuator/prometheus
- 포맷차이
> jvm.info > jvm_info
> logback.events > loback_events_total (카운터, _total 관례) 
> http.server.requests
>> http_server_requests_seconds_count
>> http_server_requests_seconds_sum
>> http_server_requests_seconds_max

## 7. 프로메테우스 - 수집설정
- prometheus.yml
```
scrape_configs:
	- job_name: "spring-actuator"
		metrics_path: "/actuator/prometheus"
		scrape_interval: 1s
		static_configs:
			- targets: ['localhost:8080']
```
> joba_name(job 수집명), metrcis_path(수집경로 지정), scrape_interval(수집주기), targets(수집서버 IP,PORT)
>> 운영시 수집주기 10s~1m
- 재기동
- 프로메테우스 연동 확인
> menu > Status > Configuration > prometheus.yml
> menu > Status > Targets
>> Status(UP/DOWM)
- 프로메테우스를 통한 데이터 조회
> jvm_info

## 8. 프로메테우스 - 기본기능
> http_server_requests_seconds_count
- 태그, 레이블: error, exception, instance, job, method, outcome, status, uri 메트릭 정보룰 구분해서 사용하기 위한 태그(마이크로미터), 레이블(프로메테우스)
- 숫자 : metric value
- 기본기능
> Table, Graph

- 필터 
> 레이블 기준 필터사용 `{}문법`
> uri=/log, method=get
>> http_server_requests_seconds_count{uri="/log", method="GET"}
> /actuator/prometheus 제외
>> http_server_requests_seconds_count{uri!="/actuator/prometheus"}
> method = get, post
>> http_server_requests_seconds_count{method=~"GET|POST"}
> /actuator 시작하는 uri제외
>> http_server_requests_seconds_count{uri!~"/actuator.*"}

- 레이블 일치 연산자
> =, !=, =~(정규식), !~

- 연산자와 쿼리 함수
> 연산자(+, -, *, /, %, ^)
> sum(http_server_requests_seconds_count)
> sumb by(method, status)(http_server_requests_seconds_count) : group by 유사
> count(http_server_requests_seconds_count)
> topk(3, http_server_requests_seconds_count)
> http_server_requests_seconds_count offest 10m
> 범위 백터 선택기 http_server_requests_seconds_count[1m]
>> 범위 벡터 선택기는 차트에 바로 표현할 수 없다

## 9. 프로메테우스 게이지와 카운터
- 게이지(Gauge): 임의로 오르내릴 수 있는 값(CPU사용량), system.cpu.usage
- 카운터(Counter): 단순하게 증가하는 값, http_server_requests_seconds_count{uri="/log"}
> 카운터에 대한 그래프 인사이트를 위해 increase(), rate(), irate()함수 지원
- increase(): 지정한 시간단위 별 증가
> increase(http_server_requests_seconds_count{uri="/log"}[1m])
- rate(): 범위 벡터에서 초당 평균 증가율
> rate(http_server_requests_seconds_count{uri="/log"}[1m])
- irate(): 범위 벡터에서 초당 순간 증가율

## 10. 그라파나 - 설치
- https://grafana.com/grafana/download
> ./grafana-server
> http://localhost:300
> id/pw: admin/admin

## 11. 그라파나 - 연동
- `프로메테우스를 데이터소스`로 사용해서 데이터 읽어와야 한다.
> 설정 > Add datasource > Prometheus(http://localhost:9090)

## 12. 그라파나 - 대시보드 만들기
- 다음 3가지 수행
```
애플리케이션 실행
프로메테우스 실행
그라파나 실행
```
- 대시보드 만들기
> Dashboards > New Dashboard > Save dashboard > Dashboard name: hello dashboard
- 대시보드 확인
- 패널 만들기
> Add panel > Add a new panel > Run queries (Builder, Code) > Code > Enter a PromQL query...

- CPU 메트릭 만들기
> system_cpu_usage, process_cpu_usage + Query
> 그래프의 데이터 이름 변경
>> 범례(Legend)
> 패널 이름 설정, 패널 저장하기

- 디스크 사용량 추가하기
> 패널옵션(Title)
> PromQL : disk_total_bytes, disk_free_bytes
> 그래프 데이터 사이즈 변경 : Standard options > Unit > Data > bytes(SI)
> 최소값 변경 : Standard options > Min(0)

## 13. 그라파나 - 공유 대시보드 활용
- 스프링부트 시스템 모니터 대시보드 불러오기
- https://grafana.com/grafana/dashboards/11378-justai-system-monitor/ , ID: 11378
> Dashboards > New > Import > 11378(ID)
- 불러온 대시보드 수정하기
> 설정 > Make editable
- Jetty 통계 > Tomcat 통계
```
jetty_threads_config_max > tomcat_thread_config_max_threads
jetty_threads_current > tomcat_threads_current_threads
jetty_threads_busy > tomcat_threads_busy_threads
jetty_threads_idle (remove)
jetth_threads_jobs (remove)
```
- 마이크로미터 대시보드 불러오기
> https://grafana.com/grafana/dashboards/4701-jvm-micrometer/

## 14. 그라파나 - 메트릭을 통한 문제확인
- 실무에서 주로 많이 사용하는 다음 4가지 대표 예시
```
CPU 사용량 초과
JVM 메모리 사용량 초과
커넥션 풀 고갈
에러 로그 급증
```
- hello.controller.TrafficController
```
@Get("/cpu")
	value++;
```
> Hello Dashboards(system.cpu.usage, process.cpu.usage)
- JVM 메모리 사용량 초과
```
List<String>
@Get("jvm)
	hello jvm! + i
```
> jvm dashboard
- 에러로그 급증
```
@Get("/error-log")
	log.error
```
> ERROR Logs, logback_events_total
- 정리
> 메트릭을 보는 것은 정확한 값을 보는 것이 목적이 아니다. 대략적인 값과 추세를 확인하는 것이 주 목적
