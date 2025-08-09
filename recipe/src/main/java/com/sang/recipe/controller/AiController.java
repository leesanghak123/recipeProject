package com.sang.recipe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sang.recipe.config.auth.PrincipalDetail;
import com.sang.recipe.dto.AiRequest;
import com.sang.recipe.dto.AiResponse;
import com.sang.recipe.service.AiService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/service")
    public Mono<ResponseEntity<AiResponse>> recipe(
            @Valid @RequestBody AiRequest request,
            @AuthenticationPrincipal PrincipalDetail principalDetail,
            HttpServletRequest httpRequest) { // Http 요청 정보

        System.out.println("[컨트롤러] 현재 사용자: " + (principalDetail != null ? principalDetail.getUsername() : "인증되지 않음"));
        System.out.println("[컨트롤러] Thread: " + Thread.currentThread().getName());
        System.out.println("[컨트롤러] HTTP Method: " + httpRequest.getMethod());
        System.out.println("[컨트롤러] Request URI: " + httpRequest.getRequestURI());
        System.out.println("[컨트롤러] Content-Type: " + httpRequest.getContentType());

        // 현재 스레드의 SecurityContext 조회
        //SecurityContext securityContext = SecurityContextHolder.getContext();

        // aiService 호출 후, Reactor 체인에 SecurityContext를 주입
        return aiService.ai요리(request, principalDetail)
                //.contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                .map(response -> {
                    System.out.println("[컨트롤러] 받은 응답: " + response);
                    return ResponseEntity.ok(response);
                })
                .doOnSuccess(responseEntity -> {
                    System.out.println("[컨트롤러] 성공적으로 응답 전송: " + responseEntity.getStatusCode());
                })
                .doOnError(error -> {
                    System.err.println("[컨트롤러] 에러 발생: " + error.getMessage());
                })
                .onErrorResume(error -> {
                    System.err.println("AI 서비스 호출 실패: " + error.getMessage());
                    return Mono.just(ResponseEntity.internalServerError()
                        .body(new AiResponse("", "", "", "서비스 호출 중 오류가 발생했습니다.")));
                });
    }
}