package com.chaos;

import brave.Tracing;
import brave.context.slf4j.MDCCurrentTraceContext;
import brave.http.HttpTracing;
import brave.okhttp3.TracingCallFactory;
import brave.spring.webmvc.TracingHandlerInterceptor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Reporter;
import zipkin.reporter.Sender;
import zipkin.reporter.okhttp3.OkHttpSender;

/**
 * Created by zcfrank1st on 27/05/2017.
 */
@Configuration
@Import({TracingHandlerInterceptor.class})
public class ZipkinConfig extends WebMvcConfigurerAdapter{
    @Autowired
    private TracingHandlerInterceptor serverInterceptor;

    @Bean
    Config config() {  return ConfigFactory.load(); }

    @Bean
    Sender sender() {
        return OkHttpSender.create(config().getString("zipkin.server"));
    }

    /** Configuration for how to buffer spans into messages for Zipkin */
    @Bean
    Reporter<Span> reporter() {
        return AsyncReporter.builder(sender()).build();
    }

    /** Controls aspects of tracing such as the name that shows up in the UI */
    @Bean
    Tracing tracing() {
        return Tracing.newBuilder()
                .localServiceName(config().getString("zipkin.service.name"))
                .currentTraceContext(MDCCurrentTraceContext.create())
                .reporter(reporter()).build();
    }

    @Bean HttpTracing httpTracing() {
        return HttpTracing.create(tracing());
    }

    @Bean
    Call.Factory callFactory() {
        OkHttpClient client = new OkHttpClient();
        return TracingCallFactory.create(httpTracing(), client);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(serverInterceptor);
    }
}
