package com.example.AttendanceManagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.AttendanceManagement.entity.MonthlyReport;

public interface MonthlyReportRepository extends JpaRepository<MonthlyReport, Long>{
	
	// 以下の検索メソッドはいずれも「提出済み」(submitted == true)のみ
	
	// 従業員IDで検索
	public List<MonthlyReport> findByEmpIdAndSubmittedTrueOrderByIndexMonthDesc(String empId);
	
	// 従業員IDで検索（未承認のみ）
	public List<MonthlyReport> findByEmpIdAndSubmittedTrueAndApprovalIdIsNullOrderByIndexMonthDesc(String empId);
	
	// 名前で検索
	public List<MonthlyReport> findByNameLikeAndSubmittedTrueOrderByIndexMonthDesc(String name);
	
	// 名前で検索（未承認のみ）
	public List<MonthlyReport> findByNameLikeAndSubmittedTrueAndApprovalIdIsNullOrderByIndexMonthDesc(String name);
	
	
	
	// 従業員IDと月から取得
	public Optional<MonthlyReport> findByEmpIdAndIndexMonth(String empId, LocalDate indexMonth);
	
	
	
	// 従業員IDと日付から要素を削除
	@Transactional
	public void deleteByEmpIdAndIndexMonth(String empId, LocalDate indexMonth);
	
	
}
