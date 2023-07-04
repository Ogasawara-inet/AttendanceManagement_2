package com.example.AttendanceManagement.entity;

import java.time.LocalDate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// 勤怠管理表の一覧と、それが承認されているかを示すエンティティ
public class MonthlyReport {

	@Id
	@GeneratedValue
	public Long id; // 通し番号
	
	public boolean submitted; // 提出済みかどうか
	
	public String empId; // 従業員ID
	
	public String name; // 従業員の名前（苗字と名前の間は半角スペース）
	
	public LocalDate indexMonth; // 勤怠管理表の月（日にちは1日）
	
	public String location; // 勤務先
	
	public String dept; // 部署
	
	public String approvalId; // 承認した管理者のID
	
	public String approvalName; // 承認者の名前（苗字と名前の間は半角スペース）
	
	public LocalDate approvalDate; // 承認日
	
	
	
	// 月次報告提出時に使用するコンストラクタ
	public MonthlyReport(String empId, String name, LocalDate indexMonth){
		this.empId = empId;
		this.name = name;
		this.indexMonth = indexMonth;
	}
	
	
}
