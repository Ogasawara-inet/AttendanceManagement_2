package com.example.AttendanceManagement.entity;

import lombok.Getter;
import lombok.Setter;

/*
 * 社員情報の入力フォームでパスワードの入力に使用するエンティティ
 * ここに格納された値をエンコードしてEmployeeのpasswordに格納する
 */

@Getter
@Setter
public class Passwords {

	// パスワード
	private String password;
	
	// 再入力パスワード
	private String passwordAgain;
	
}
