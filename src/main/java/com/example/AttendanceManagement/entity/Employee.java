package com.example.AttendanceManagement.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.AttendanceManagement.util.Auth;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Table(name="site_user", schema="public")
@Getter
@Setter
//@Entity
@NoArgsConstructor
public class Employee {
	
	@Id
	@GeneratedValue
	private Long id; // 通し番号
	
	private String empId; // 社員ID
	
	private Auth auth; // 権限

	@NotBlank
	private String lastName; // 苗字
	
	private String middleName; // ミドルネーム

	@NotBlank
	private String firstName; // 名前
	
//	private boolean nameReverseOrder; // trueなら名→姓の順にする
	
	// フルネーム
	// ミドルネームや姓名順などがありフルネームの生成が
	// 複雑になったので追加。
	// lastName, middleName, firstNameから自動生成する。
	private String fullName;
	
	@NotBlank
	private String lastNameKana; // 苗字フリガナ

	private String middleNameKana; // ミドルネームフリガナ
	
	@NotBlank
	private String firstNameKana; // 名前フリガナ
	
	private String fullNameKana; // フルネームフリガナ
	
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthday; // 生年月日
	
	// パスワードはエンコードして格納するため、
	// 入力値の検証はエンコード前の値に対して行う
	private String password; // パスワード
	
	private String tel; // 携帯電話
	
	private String email; // メールアドレス
	
	private String location; // 勤務先
	
	private String dept; // 部署名
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate joining; // 入社日
	
}
