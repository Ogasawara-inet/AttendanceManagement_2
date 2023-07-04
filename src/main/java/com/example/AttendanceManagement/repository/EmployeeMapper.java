package com.example.AttendanceManagement.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.example.AttendanceManagement.entity.Employee;

@Mapper
public interface EmployeeMapper {
	
	// 社員IDで検索
	Optional<Employee> find(String empId);

	// 社員IDで検索
	Optional<Employee> findById(Long id);
	
	// 全件取得
	List<Employee> findAll();
	
	// 名前で検索（苗字、名前）
	List<Employee> search(String searchword);
	
	// 新規登録
//	void insert(Employee employee);
	
	// 更新
//	void update(Employee employee);
	
	// あれば更新、なければ作成
	void upsert(Employee employee);
	
	// 社員IDで削除
	void delete(String empId);

	// IDで削除
	void deleteById(Long id);
	
}
