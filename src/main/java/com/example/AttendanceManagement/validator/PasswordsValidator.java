package com.example.AttendanceManagement.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.AttendanceManagement.entity.Passwords;

@Component
public class PasswordsValidator implements Validator{
	
	@Override
	public boolean supports(Class<?> clazz) {
		
		return Passwords.class.isAssignableFrom(clazz);
		
	}
	
	
	
	@Override
	public void validate(Object target, Errors errors) {
		
		Passwords passwords = (Passwords)target;
		
		String password = passwords.getPassword();
		String passwordAgain = passwords.getPasswordAgain();
		
		// パスワードの最小文字数
		int length = 8;
		
		// 記号を含む文字列の正規表現
		String signs = Pattern.quote("!\"#$%&\'()-=^~\\|@`[{;+:*]},<.>/?_");
		String includingSign = ".*[" + signs + "].*";
		String safePattern = "[0-9a-zA-z" + signs + "]*";
		
		
		
		// パスワードが短い場合はエラー
		if(password.length() < length) {
			
			errors.rejectValue("password", "password_length_error", 
					"パスワードは" + length + "文字以上で入力してください");
			errors.rejectValue("passwordAgain", "password_length_error");
			
			return;
		}
		
		// パスワードのパターンが正しくない場合はエラー
		List<String> list = new ArrayList<>();
		
		if(!(password.matches(".*[A-Z].*"))) list.add("英大文字");
		if(!(password.matches(".*[a-z].*"))) list.add("英小文字");
		if(!(password.matches(".*[0-9].*"))) list.add("数字");
		if(!(password.matches(includingSign))) list.add("記号");
		
		if(list.size() != 0) {
			errors.rejectValue("password", "password_pattern_error", 
					"パスワードに" + String.join("、", list) + "が含まれていません");
			errors.rejectValue("passwordAgain", "password_pattern_error");
			
			return;
		}
			
		
		// パスワードに使用できない文字が含まれている場合はエラー
		if(!(password.matches(safePattern))) {
			
			errors.rejectValue("password", "password_forbidden", 
					"パスワードに使用できない文字が含まれています");
			errors.rejectValue("passwordAgain", "password_forbidden");
			
			return;
		}
		
		// パスワードが再入力したパスワードと一致しない場合はエラー
		if(!(password.equals(passwordAgain))) {
			
			errors.rejectValue("password", "password_unmatch", 
					"パスワードが再入力された値と一致しません");
			errors.rejectValue("passwordAgain", "password_unmatch");
			
			return;
		}
		
		
		
	}

}
