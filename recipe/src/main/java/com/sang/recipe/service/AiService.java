package com.sang.recipe.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.sang.recipe.config.auth.PrincipalDetail;
import com.sang.recipe.dto.AiRequest;
import com.sang.recipe.dto.AiResponse;

import reactor.core.publisher.Mono;

@Service
public class AiService {

    private final WebClient webClient;

    public AiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<AiResponse> ai요리(AiRequest request, PrincipalDetail principalDetail) {
        System.out.println("[ai요리 시작] Thread: " + Thread.currentThread().getName());
        System.out.println("[ai요리 시작] 사용자: " + (principalDetail != null ? principalDetail.getUsername() : "없음"));

        return this.webClient.post()
            .uri("/api/question")
            .bodyValue(request)
            .retrieve() // 실제 요청 응답 처리
            .bodyToMono(String.class) // 응답 String 타입을 Mono<String>으로 감싸기
            .doOnNext(content -> {
                System.out.println("[FastAPI 응답 도착] Thread: " + Thread.currentThread().getName());
                System.out.println("[FastAPI 응답 내용] " + content);
            })
            .map(responseContent -> new AiResponse( // 응답을 AiResponse 객체로 변환
                request.getCookingMethod(),
                request.getCookingCategory(),
                request.getIngredients(),
                responseContent != null ? responseContent : "나중에 다시 시도해주세요."
            ))
            .onErrorResume(error -> {
                System.err.println("[WebClient 오류] " + error.getMessage());
                return Mono.just(new AiResponse(
                    request.getCookingMethod(),
                    request.getCookingCategory(),
                    request.getIngredients(),
                    "AI 응답 중 오류가 발생했습니다."
                ));
            });
    }
}