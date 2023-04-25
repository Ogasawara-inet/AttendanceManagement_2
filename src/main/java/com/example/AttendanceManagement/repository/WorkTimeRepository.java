package com.example.AttendanceManagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.AttendanceManagement.entity.WorkTime;
import com.example.AttendanceManagement.util.Division;

public interface WorkTimeRepository extends JpaRepository<WorkTime, Long> {
	
	/*
	 * これらのメソッドはすべて従業員ごとのデータを取得する。
	 */

	// （指定した従業員の）すべてのデータを取得
	List<WorkTime> findByEmpId(String empId);
	
	// 指定された期間のWorkTimeを返す
	List<WorkTime> findByEmpIdAndWorkDateBetween(String empId, LocalDate start, LocalDate end);
	
	// 指定日のWorkTimeを返す
	WorkTime findByEmpIdAndWorkDate(String empId, LocalDate date);
	
	// 指定された期間内での、出勤日数を返す
	Integer countByEmpIdAndWorkingTimeGreaterThanEqualAndWorkDateBetween(String empId, Integer workingTime, LocalDate start, LocalDate end);
	
	// 指定された期間内での、区分が一致する件数を返す
	Integer countByEmpIdAndDivisionAndWorkDateBetween(String empId, Division div, LocalDate start, LocalDate end);
	
}
