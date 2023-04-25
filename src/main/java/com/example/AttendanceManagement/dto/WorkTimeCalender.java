package com.example.AttendanceManagement.dto;

import java.util.List;

import com.example.AttendanceManagement.entity.WorkTime;

import lombok.Getter;
import lombok.Setter;

// 勤怠管理票を修正したあと、それを登録するためのDTO
@Getter
@Setter
public class WorkTimeCalender{

	private List<WorkTime> workTimeList;
	
}
