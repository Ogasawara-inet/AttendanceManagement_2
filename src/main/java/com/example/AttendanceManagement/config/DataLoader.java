package com.example.AttendanceManagement.config;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.AttendanceManagement.entity.Employee;
import com.example.AttendanceManagement.entity.MonthlyReport;
import com.example.AttendanceManagement.entity.WorkTime;
import com.example.AttendanceManagement.repository.EmployeeRepository;
import com.example.AttendanceManagement.repository.MonthlyReportRepository;
import com.example.AttendanceManagement.repository.WorkTimeRepository;
import com.example.AttendanceManagement.util.Auth;
import com.example.AttendanceManagement.util.Division;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class DataLoader implements ApplicationRunner {

	private final EmployeeRepository employeeRepository;
	private final WorkTimeRepository workTimeRepository;
	private final MonthlyReportRepository monthlyReportRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		// TODO 自動生成されたメソッド・スタブ

		
		/*
		 * 以下はテスト用
		 */
		
		// 管理者ユーザーadminを作成
		Employee admin = new Employee();
		admin.setEmpId("admin");
		admin.setAuth(Auth.ADMIN);
		admin.setLastName("阿戸");
		admin.setFirstName("民太郎");
		admin.setLastNameKana("アド");
		admin.setFirstNameKana("ミンタロウ");
		admin.setBirthday(LocalDate.of(1990, 1, 1));
		admin.setPassword(passwordEncoder.encode("password"));
		admin.setLocation("本社");
		admin.setDept("○○部");
		admin.setJoining(LocalDate.of(2020, 4, 1));
		employeeRepository.save(admin);
		
		
		
		// 一般ユーザーemp01を作成
		Employee emp01 = new Employee();
		emp01.setEmpId("emp01");
		emp01.setAuth(Auth.EMPLOYEE);
		emp01.setLastName("遠藤");
		emp01.setFirstName("楓子");
		emp01.setLastNameKana("エンドウ");
		emp01.setFirstNameKana("フウコ");
		emp01.setBirthday(LocalDate.of(2000, 1, 1));
		emp01.setPassword(passwordEncoder.encode("password"));
		emp01.setLocation("本社");
		emp01.setDept("××部");
		emp01.setJoining(LocalDate.of(2022, 4, 1));
		employeeRepository.save(emp01);
		
		
		
		// 出勤時間を設定
		WorkTime workTime = new WorkTime();
		workTime.setEmpId("emp01");
		workTime.setWorkDate(LocalDate.of(2023, 3, 1));
		workTime.setStartTime(LocalTime.of(10, 0, 0));
		workTime.setFinishTime(LocalTime.of(17, 0, 0));
		workTime.setWorkingTime(6 * 60);
		workTime.setBreakTime(1 * 60);
		workTimeRepository.save(workTime);
		
		WorkTime workTime2 = new WorkTime();
		workTime2.setEmpId("emp01");
		workTime2.setWorkDate(LocalDate.of(2023, 3, 2));
		workTime2.setDivision(Division.有給);
		workTimeRepository.save(workTime2);
		
		
		
		// 承認設定
		MonthlyReport teport = 
				new MonthlyReport("emp01", "遠藤 楓子", LocalDate.of(2023, 3, 1));
		teport.setSubmitted(true);
		monthlyReportRepository.save(teport);
		
		

		
		
		
		
	}

}
