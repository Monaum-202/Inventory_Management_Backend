//package com.monaum.Rapid_Global.config;
//
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//
//import java.util.List;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.lang.NonNull;
//import org.springframework.messaging.converter.DefaultContentTypeResolver;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.converter.MessageConverter;
//import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import lombok.RequiredArgsConstructor;
//
///**
// * Zubayer Ahamed
// *
// * @since Jul 15, 2025
// */
//@Configuration
//@EnableWebSocketMessageBroker
//@Order(Ordered.HIGHEST_PRECEDENCE + 99)
//@RequiredArgsConstructor
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//	@Override
//	public void configureMessageBroker(@NonNull MessageBrokerRegistry registry) {
//		registry.enableSimpleBroker("/user");
//		registry.setApplicationDestinationPrefixes("/app");
//		registry.setUserDestinationPrefix("/user");
//	}
//
//	@Override
//	public void registerStompEndpoints(@NonNull StompEndpointRegistry registry) {
//		registry.addEndpoint("/api/v1/ws").setAllowedOrigins("http://localhost:8000", "http://127.0.0.1:8000", "null").withSockJS();
//	}
//
//	@Override
//	public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> argumentResolvers) {
//		argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
//	}
//
//	@Override
//	public boolean configureMessageConverters(@NonNull List<MessageConverter> messageConverters) {
//		DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
//		resolver.setDefaultMimeType(APPLICATION_JSON);
//		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//		converter.setObjectMapper(new ObjectMapper());
//		converter.setContentTypeResolver(resolver);
//		messageConverters.add(converter);
//		return false;
//	}
//
//}
