package com.example.AttendanceManagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.AttendanceManagement.entity.WorkTime;
import com.example.AttendanceManagement.util.Division;

@Mapper
public interface WorkTimeMapper{
	
	void insert(WorkTime workTime);
	
	void update(WorkTime workTime);
	
	void upsert(WorkTime workTime);
	
	
	
	/*
	 * これらのメソッドはすべて従業員ごとのデータを取得する。
	 */

	// （指定した従業員の）すべてのデータを取得
	List<WorkTime> findAll(String empId);
	
	// 指定された期間のWorkTimeを返す
	List<WorkTime> findByPeriod(String empId, LocalDate start, LocalDate end);
	
	// 指定日のWorkTimeを返す
	WorkTime find(String empId, LocalDate workDate);
	
	// 指定された期間内での、出勤日数を返す
	Integer countWorkingDays(String empId, LocalDate start, LocalDate end);
	
	// 指定された期間内での、区分が一致する件数を返す
	Integer countDivision(String empId, Division division, LocalDate start, LocalDate end);
	
}
