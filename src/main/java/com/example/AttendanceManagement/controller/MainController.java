package com.example.AttendanceManagement.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.AttendanceManagement.entity.Employee;
import com.example.AttendanceManagement.entity.Passwords;
import com.example.AttendanceManagement.repository.EmployeeRepository;
import com.example.AttendanceManagement.validator.PasswordsValidator;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {

	private final EmployeeRepository employeeRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	// カスタムバリデーター
	private final PasswordsValidator passwordsValidator;
	
	// バリデーター登録
	@InitBinder("passwords")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(passwordsValidator);
	}
	
	
	
	/*
	 * 社員IDの取得はuser.getUsername()で行う。
	 */
	
	
	
	
	// ホーム画面
	@GetMapping("/")
	public String index(@AuthenticationPrincipal UserDetails user, 
			Model model) {
		
		// ユーザ情報を渡す
		model.addAttribute("user", user);
		
		return "home";
	}

	
	
	// 従業員ログイン画面
	@GetMapping("/login")
	public String login(@AuthenticationPrincipal UserDetails user,
			Model model) {
		
		model.addAttribute("user", user);
		
		return "login";
	}
	
	
	
	// ログイン失敗
	@GetMapping("/login/error")
	public String login_error(RedirectAttributes redirectAttributes,
			Model model) {
		
		// メッセージを渡す
		redirectAttributes.addFlashAttribute("warn", "ログインに失敗しました");
		
		return "redirect:/login";
	}
	
	
	
	// エラー画面
	@GetMapping("/error")
	public String error(@AuthenticationPrincipal UserDetails user,
			Model model) {
		
		model.addAttribute("user", user);
		
		return "error";
	}
	
	
	// ログアウト処理
	@GetMapping("/logout")
	public String logout(RedirectAttributes redirectAttributes,
			Model model) {
		
		System.out.println("logout");
		
		// メッセージを渡す
		redirectAttributes.addFlashAttribute("msg", "ログアウトしました");
		
		return "redirect:/login";
	}
	
	
	
	// 従業員ホーム画面
	@GetMapping("/home")
	public String home(@AuthenticationPrincipal UserDetails user, 
			HttpServletResponse response, Model model) {
		
		model.addAttribute("user", user);
		
		return "home";
	}
	
	
	
	// 登録内容変更
	@GetMapping("/revice")
	public String revice(@AuthenticationPrincipal UserDetails user, 
			@ModelAttribute Employee employee, 
			@ModelAttribute Passwords passwords,
			Model model) {
		
		model.addAttribute("user", user);
		
		// 自分の登録内容を取得して送信
		employee = employeeRepository.findByEmpId(
				user.getUsername()).orElseThrow();
		model.addAttribute("employee", employee);
		
		model.addAttribute("passwords", passwords);
		
		String status 
				= (user.getAuthorities().toArray()[0].toString().equals("ADMIN")
						? "adminRevice" : "empRevice");
		model.addAttribute("status", status);
		
		return "admin/register";
	}
	
	
	
	// 変更内容を保存
	@PostMapping("/revice")
	public String save(@AuthenticationPrincipal UserDetails user, 
			@Validated @ModelAttribute Employee employee, 
			BindingResult result,
			@Validated @ModelAttribute Passwords passwords, 
			BindingResult passwordsResult,
			@RequestParam(required = false) String status,
			RedirectAttributes redirectAttributes, Model model) {
		
		model.addAttribute("user", user);
		
		
		
		// エラーが発生した場合は元の画面へ
		if(result.hasErrors() || passwordsResult.hasErrors()) {
			model.addAttribute("status", status);
			model.addAttribute("employee", employee);
			return "admin/register";
		}
		
		
		
		// パスワードを暗号化して格納
		employee.setPassword(passwordEncoder.encode(passwords.getPassword()));
		
		employeeRepository.save(employee);
		
		redirectAttributes.addFlashAttribute("msg", "登録内容を変更しました");
		
		return "redirect:/revice";
	}
	
	
}
