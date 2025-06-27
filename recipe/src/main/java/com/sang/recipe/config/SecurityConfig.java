package com.sang.recipe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity // 시큐리티를 위한 config라는 뜻
public class SecurityConfig {
	// AuthenticationManager 메서드 생성 (스프링 시큐리티 인증을 처리)
	// PrincipalDetailService와 연결되는 인증필터체인
	private final AuthenticationConfiguration authenticationConfiguration;
	// JWTUtil 주입
	private final JWTUtil jwtUtil;
	// cors 설정 주입
	private final CorsConfigurationSource corsConfigurationSource;

	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, CorsConfigurationSource corsConfigurationSource) {

		this.authenticationConfiguration = authenticationConfiguration;
		this.jwtUtil = jwtUtil;
		this.corsConfigurationSource = corsConfigurationSource;
	}

//	@Bean
//	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//		return authenticationConfiguration.getAuthenticationManager();
//	}
	
	// 위의 코드 수정
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	    return this.authenticationConfiguration.getAuthenticationManager();
	}

	@Bean // IoC가 되요!!
	BCryptPasswordEncoder encodePWD() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// 세션으로 구현 시 세션이 고정되기 때문에 csrf를 허용해야한다
		// jwt를 이용한 stateless 방식을 사용하기 때문에 csrf 공격을 방어하지 않아도 된다
		http.csrf((auth) -> auth.disable());

		// From 로그인 방식 disable
		http.formLogin((auth) -> auth.disable());

		// http basic 인증 방식 disable
		http.httpBasic((auth) -> auth.disable());
		
		// cors 통신
		http.cors(cors -> cors.configurationSource(corsConfigurationSource));

		// 인증 주소 설정 (WEB-INF/** 추가해줘야 함. 아니면 인증이 필요한 주소로 무한 리다이렉션 일어남)
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/auth/**").permitAll()
				.requestMatchers("/api/ai/**").permitAll()
				.requestMatchers("/admin").hasRole("ADMIN") // ADMIN만 갈 수 있는 경로
				.anyRequest().authenticated()); // 위의 경로 빼고는 다 인증이 필요

		// At은 원하는 자리에 등록, Bofore는 필터전에, after는 이후에
		// LoginFilter를 UsernamePasswordAuthenticationFilter 부분에 넣겠다 (LoginFilter는 UsernamePasswordAuthenticationFilter를 커스터마이징 한 부분)
		http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil),
				UsernamePasswordAuthenticationFilter.class);

		// LoginFilter 이전에 JWTFilter 실행
		http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
		
		// 세션 설정 (STATELESS 상태 만들기)
		http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// 로그인 처리 프로세스 설정 (허용안되는 곳으로 갈 시 URL설정)
		// 로그인 처리 url은 login이네 (form 로그인 비활성화로 인한)
		// 현재 formlogin을 disable 했기 때문에 아래 코드는 필요 X
		// http.formLogin(f -> f.loginPage("http://localhost:8003/auth/loginForm")
				//.loginProcessingUrl("/auth/login")
				//.defaultSuccessUrl("http://localhost:8003") // 로그인 성공시 기본 페이지
		// );

		return http.build();
	}
}
