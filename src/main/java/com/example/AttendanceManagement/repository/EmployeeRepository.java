package com.example.AttendanceManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.AttendanceManagement.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String>{
	
	// 社員IDで検索
	public Optional<Employee> findByEmpId(String empId);
	
	// 苗字で検索
	public List<Employee> findByLastNameLikeOrderByEmpId(String lastName);
	
	// 名前で検索
	public List<Employee> findByFirstNameLikeOrderByEmpId(String firstName);
	
	
	
	// 社員IDで削除
	@Transactional
	public void deleteByEmpId(String empId);
	
}
