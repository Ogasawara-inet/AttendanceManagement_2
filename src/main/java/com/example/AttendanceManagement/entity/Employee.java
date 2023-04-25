package com.example.AttendanceManagement.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.AttendanceManagement.util.Auth;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Table(name="site_user", schema="public")
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

	@Id
	@GeneratedValue
	private Long id; // 通し番号
	
	private String empId; // 社員ID（自動設定）
	
	private Auth auth; // 権限

	@NotBlank
	private String lastName; // 苗字

	@NotBlank
	private String firstName; // 名前

	@NotBlank
	private String lastNameKana; // 苗字フリガナ

	@NotBlank
	private String firstNameKana; // 名前フリガナ

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthday; // 生年月日
	
	@Size(min = 8)
	private String password; // パスワード
	
	private String tel; // 携帯電話
	
	private String email; // メールアドレス
	
	private String location; // 勤務先
	
	private String dept; // 部署名
	
	// 変数名がjoinだとクエリのJOINとして扱われるためエラーになる
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate joining; // 入社日
	
}
