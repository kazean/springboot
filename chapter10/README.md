# [실습] 10. 모니터링 메트릭 활용
## 1. 메트릭 등록 - 예제 만들기
- 비지니스에 특화된 모니터링
> ex) 주문수, 취소수

## 2. 메트릭 등록1 - 카운터
- 마이크로미터를 사용해서 메트릭을 직접 등록하는 방법
- `MeterRegistry` 마이크로미터 기능을 제공하는 핵심 컴포넌트
> 스프링을 통해서 주입 받아서 사용하고, 이곳을 통해서 카운터, 게이지 등을 등록한다.
- `Counter`
- MeterRegistry registry
```
Counter.builder("my.order")
	.tag("class", this.getClass().getName())
	.tag("method", "order")
	.description("order")
	.register(registry).increment();
```
> Counter.builder(name)을 통해서 카운터 생성, name: 메트릭 이름
> tag 사용해서 order, cancel 구분
> register(registry): 만든 카운터를 MeterRegistry에 등록한다
> increment(): 카운터 값을 하나 증가한다
- 실행
> 주문과 취소를 각각 한번씩 실행후 메트릭 확인
>> 최소 한번은 실행해야 메트릭 등록됨, 최초 App실행시 호출후 강제로 0으로 변경하는 등 작업 필요
> http://localhost:8080/order
> http://localhost:8080/cancel
> http://localhost:8080/actuator/metrics/my.order
> http://localhost:8080/actuator/prometheus
- 그라파나 등록 - 주문수, 취소수
```
increase(my_order_total{method="order"}[1m])
increase(my_order_total{method="cancel"}[1m])
```

## 3. 메트릭 등록2 - Counted
- !OrderServiceV1의 가장 큰 단점은 메트릭을 관리하는 로직이 핵심 비즈니스 개발 로직에 침투했다는점
> AOP사용, 마이크로미터가 AOP 구성요소를 이미 다 만들어 두었다
- @Counted("my.order")
> `@Counted` 애노테이션을 측정을 원하는 메소드에 적용, 이렇게 사용하면 method를 기준으로 분류해서 적용한다.
- hello.order.v2.OrderConfigV2
```
@Bean
CountedAspect countedAspect(MeterRegistry registry){
	return new CountedAspect(registry);
}
```
> `CountedAspect`를 등록하면 `@Counted`를 인지해서 counter를 사용하는 AOP를 적용한다
> !주의, CountedAspect를 빈으로 등록하지 않으면 @Counted 관련 AOP가 동작하지 않는다.
- hello.ActuatorApplication - config 수정

## 4. 메트릭 등록3 - Timer
- `Timer`는 좀 특별한 메트릭 측정도구, 시간을 측정하는데 사용된다. 카운터 + 실행시간
- Timer는 다음과 같은 내용을 한번에 측정해준다.
> seconds_count, secounds_sum, seconds_max
- MeterRegistry registry
```
Timer timer = Timer.builder("my.order")
	.tag("class", this.getClass().getName())
	.tag("method", "order")
	.description("order")
	.register(registry);
timer.record(()->{
	~~ 측정할 함수
	sleep()	
})
```
> Timer.builder(name), tag, regiter(registry), `timer.record()`
- 실행 - measurements
> COUNT, TOTAL_TIME, MAX
> (Prometheus) seconds_count, sum, max
>> 평균 실행시간 seconds_sum / seconds_count
- 그라파나 등록 - 주문수 v3
```
increase(my_order_secods_count{method="order"}[1m])
increase(my_order_secods_count{method="cancel"}[1m])
```
- 그라파나 등록 - 최대 실행시간
> my_order_secods_max
- 그라파나 등록 - 평균 실행시간
> increase(my_order_secods_sum[1m]) / increase(my_order_secods_count[1m]) : 구간별로 쪼개서 보여짐

## 5. 메트릭 등록4 - @Timed
- @Time("my.order") // class Level
> `@Timed("my.order")` 타입이나 메서드 중에 적용할 수 있다.
> 타입에 적용시 모든 public메서드에 타이머가 적용 된다.
- hello.order.v4.OrderConfigV4
```
@Bean
public TimedAspect
```
- hello.ActuatorApplication - config 수정

## 6. 메트릭 등록5 - 게이지
- Gauge(게이지)
- hello.order.gauge.StockConfigV1
```
@Bean MyStockMetric myStockMetric(OrderService orderService, MeterRegistry registry){
	return new MyStockMetric(orderService, registry);
}

static class MyStockMetric{
	private OrderService orderService;
	private MeterRegistry registry;

	@PostConstruct
	public void init(){
		Gauge.builder("my.stock", orderService, service -> {
			return service.getStock().get();
		}).register(registry);
	}
}
```
- hello.ActuatorApplication - config 수정 ImportV4, StockConfigV1
> 애플리케이션 실행시 init() 주기적 실행
> http://localhost:8080/actuator/metric/my.stock
> http://localhost:8080/actuator/prometheus
- 그파나 등록 - 재고
> my_stock
- 게이지 단순하게 등록하기
- hello.order.gauge.StockConfigV2
```
@Bean
public MeterBinder stockSize(OrderService orderService) {
	return registry -> Gauge.builder(...).register(registry);
}
```
- hello.ActuatorApplication - config 수정 ImportV4, StockConfigV2

## 7. 정리
- Micrometer 사용법 이해
> 메트릭은 100% 정확한 숫자를 보는데 사용하는 것이 아니다.
- 마이크로미터 핵심 기능: Counter, Gauge, Timer, Tag
- MeterRegistry
- Counter
- Gauge
- Timer
- Tag, 레이블

## 8. 실무 모니터링 환경 구성 팁
- 모니터링 3단계 
> 대시보드 / 애플리케이션 추적 - 핀포인트 / 로그
- 대시보드
> 전체를 하눈ㄴ에 볼 수 있는 가장 높은 뷰
> 제품: 마이크로미터, 프로메테우스, 그라파나
> 모니터링 대상: 시스템 메트릭, 애플리케이션 메트릭, 비지니스 메트릭
- 애플리케이션 추적
> HTTP요청을 추적, 일부는 MSA환경에서 분산 추적
> 제품: 핀포인트, 스카우트, 와탭, 제니퍼
- 로그
> 같은 HTTP요청을 묶어서 확인할 수 있는 방법이 중요, MDC 적용
> 파일로 직접 로그를 남기는 경우
>> info, error 구분
> 클라우드에 로그를 저장하는 경우
>> 검색이 잘 되도룩 구분
- 모니터링 정리
> 각각 용도가 다르다, 핀포인트는 강추
- 알람
> 모니터링 툴에서 일정 수치가 넘어가면, 슬랙, 문자 등을 연동