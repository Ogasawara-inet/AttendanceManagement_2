package com.example.AttendanceManagement.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import com.example.AttendanceManagement.entity.MonthlyReport;

@Mapper
public interface MonthlyReportMapper {

	// 挿入
	void insert(MonthlyReport report);
	
	// 更新（id以外全て）
	void update(MonthlyReport report);
	
	// なければ挿入、あれば更新
	void upsert(MonthlyReport report);
	
	
	
	// submittedをtrueに
	void submit(String empId, LocalDate indexMonth);
	
	// submittedをfalseに
	void cancelSubmit(String empId, LocalDate indexMonth);
	
	
	
	// 従業員IDと月から取得
	Optional<MonthlyReport> find(String empId, LocalDate indexMonth);
	
	// 全件取得
	List<MonthlyReport> findAll();
	
	
	
	// 以下の検索メソッドはいずれも「提出済み」(submitted == true)のみ
	
	// 従業員IDで検索
	List<MonthlyReport> findByEmpId(String empId);
	
	// 従業員IDで検索（未承認のみ）
	List<MonthlyReport> findByEmpIdNotApproved(String empId);
	
	// 名前で検索
	// 苗字と名前をつなげた値にnameが含まれるもの
	List<MonthlyReport> findByName(String name);
	
	// 名前で検索（未承認のみ）
	List<MonthlyReport> findByNameNotApproved(String name);
	
	
	
	// 従業員IDと日付から要素を削除
	@Transactional
	void delete(String empId, LocalDate indexMonth);
	
	
}
