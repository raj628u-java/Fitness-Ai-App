package com.apigateway.main.sync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.apigateway.main.user.RegisterRequest;
import com.apigateway.main.user.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class KeyCloakSyncFilter implements WebFilter {
	
	@Autowired
	private UserService userService;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		log.info("KeyCloakSyncFilter");
		  String token = exchange.getRequest().getHeaders().getFirst("Authorization");
	        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
	        RegisterRequest registerRequest = getUserDetails(token);

	        if (userId == null) {
	            userId = registerRequest.getKeycloakId();
	        }

	        if (userId != null && token != null){
	            String finalUserId = userId;
	            return userService.validateUserById(userId)
	                    .flatMap(exist -> {
	                        if (!exist) {
	                            // Register User

	                            if (registerRequest != null) {
	                                return userService.registerUser(registerRequest)
	                                        .then(Mono.empty());
	                            } else {
	                                return Mono.empty();
	                            }
	                        } else {
	                            log.info("User already exist, Skipping sync.");
	                            return Mono.empty();
	                        }
	                    })
	                    .then(Mono.defer(() -> {
	                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
	                                .header("X-User-ID", finalUserId)
	                                .build();
	                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
	                    }));
	        }
	        log.info("User ID or Token is null, skipping sync.",chain.filter(exchange));
	        return chain.filter(exchange);
	    }

	private RegisterRequest getUserDetails(String token) {
		// TODO Auto-generated method stub
		try {
			String tokenWithoutBearer = token.replace("Bearer ", "").trim();
			SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
			JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
			
			RegisterRequest registerRequest = new RegisterRequest();
			registerRequest.setKeycloakId(claimsSet.getStringClaim("sub"));
			registerRequest.setFirstName(claimsSet.getStringClaim("given_name"));
			registerRequest.setLastName(claimsSet.getStringClaim("family_name"));
			registerRequest.setEmail(claimsSet.getStringClaim("email"));
			registerRequest.setPassword("dummy@123123");
			
			return registerRequest;
		}catch(Exception e) {
			log.error("Error while getting user details from KeyCloak", e);
			return null;
		}
	}
}