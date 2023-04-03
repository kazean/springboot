package hello.order.gauge;

import hello.order.OrderService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class StockConfigV1 {

    @Bean
    public MyStockMetric myStockMetric(OrderService orderService, MeterRegistry registry) {
        return new MyStockMetric(registry, orderService);
    }

    static class MyStockMetric {
        private final MeterRegistry registry;
        private final OrderService orderService;

        public MyStockMetric(MeterRegistry registry, OrderService orderService) {
            this.registry = registry;
            this.orderService = orderService;
        }

        @PostConstruct
        public void init() {
            Gauge.builder("my.stock", orderService, service -> {
                log.info("stock gauge call()");
                return service.getStock().get();
            }).register(registry);
        }
    }
}
