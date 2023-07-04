package com.example.AttendanceManagement.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.example.AttendanceManagement.util.Division;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkTime{

	@Id
	@GeneratedValue
	private Long id; // 通し番号
	
	private String empId; // 社員ID
	
	private LocalDate workDate; // 出勤日
	
	private Division division; // 区分
	
	private LocalTime startTime; // 出勤時刻
	
	private LocalTime finishTime; // 退勤時刻
	
	private LocalTime breakStartTime; // 休憩開始時刻、2回目以降の休憩で上書き
	
	private LocalTime breakFinishTime; // 休憩終了時刻、2回目以降の休憩で上書き
	
	private Integer workingTime; // 実働時間（分）
	
	private Integer breakTime; // 休憩時間（分）
	
	private String note; // 備考
	
}
