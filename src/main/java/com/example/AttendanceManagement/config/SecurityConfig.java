package com.example.AttendanceManagement.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.AttendanceManagement.util.Auth;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		
		http.authorizeHttpRequests(auth -> auth
			
					// 静的リソースのアクセス許可
					.requestMatchers(PathRequest.toStaticResources().atCommonLocations())
							.permitAll()
					
					// 全員に許可するページ
					.requestMatchers("/login","/logout", "/error", "/webjars/**")
							.permitAll()
					
					// 管理者のみに許可するページ
					.requestMatchers("/admin/**").hasAuthority(Auth.ADMIN.toString())
					
					// その他のリクエストのアクセス制限
					.anyRequest().authenticated())
					
			.formLogin(login -> login
					
					// ログインページ
					.loginPage("/login")
					
					// ログイン実行URL
					.loginProcessingUrl("/login")
					
					// ログイン後の遷移先
					.defaultSuccessUrl("/home")
					
					// ログイン失敗時の遷移先
					.failureUrl("/login/error")
					
					// ログインページのアクセス許可
					.permitAll())
			
			// ログアウト後の遷移先
			.logout(logout -> logout.logoutSuccessUrl("/logout"))
			
			// ログイン状態を維持
			.rememberMe();
			
		return http.build();
	}
	
}
