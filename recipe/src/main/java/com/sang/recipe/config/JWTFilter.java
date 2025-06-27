package com.sang.recipe.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sang.recipe.config.auth.PrincipalDetail;
import com.sang.recipe.model.RoleType;
import com.sang.recipe.model.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
				
		//request에서 Authorization 헤더를 찾음
        String authorization= request.getHeader("Authorization");
				
		//Authorization 헤더 검증 (null 이거나 접두사가 이상한 경우)
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            System.out.println("token null");
            //doFilter를 통해 종료하고 다음 필터로 request, response를 던져준다
            filterChain.doFilter(request, response);
						
            //조건이 해당되면 메소드 종료 (필수)
            return;
        }
			
        System.out.println("authorization now");
		//Bearer 부분 제거 후 순수 토큰만 획득(split을 사용해서 두번째 인덱스)
        String token = authorization.split(" ")[1];
        System.out.println("token : " + token);
			
		//토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {

            System.out.println("token expired");
            // 만료시 이 메서드 종료 후 다음 필터한테 request, response 전송
            filterChain.doFilter(request, response);

			//조건이 해당되면 메소드 종료 (필수)
            return;
        }

        // 토큰을 기반으로 일시적인 세션을 만들어서 시큐리티 홀더에 넣어줄 것
        // jwt 에서는 json형식으로 안가져오고 문자열로 파싱
        // 토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        System.out.println("Role = " + role);
				
		//userEntity를 생성하여 값 set
        User user = new User();
        user.setUsername(username);
        user.setPassword("temppassword"); // DB에서 비번을 찾으면 매번 조회해야하기 때문에 정확한 비번을 안넣고 임의로 넣음
        // role이 String 타입일 때, 이를 RoleType enum으로 변환하여 설정
        user.setRole(RoleType.valueOf(role));

				
        //UserDetails에 회원 정보 객체 담기
        PrincipalDetail customUserDetails = new PrincipalDetail(user);

		// 스프링 시큐리티 인증 토큰 생성 (인증자 정보, 비밀번호 정보, 사용자 권한 목록)
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, token, customUserDetails.getAuthorities());
		//세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}