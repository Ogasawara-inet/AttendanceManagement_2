package com.example.AttendanceManagement.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.AttendanceManagement.dto.WorkTimeCalender;
import com.example.AttendanceManagement.entity.Employee;
import com.example.AttendanceManagement.entity.MonthlyReport;
import com.example.AttendanceManagement.entity.WorkTime;
import com.example.AttendanceManagement.repository.EmployeeRepository;
import com.example.AttendanceManagement.repository.MonthlyReportRepository;
import com.example.AttendanceManagement.repository.WorkTimeRepository;
import com.example.AttendanceManagement.util.Division;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AttendanceController {

	/*
	 * 社員IDの取得はuser.getUsername()で行う。
	 */
	
	
	private final WorkTimeRepository workTimeRepository;
	private final EmployeeRepository employeeRepository;
	private final MonthlyReportRepository monthlyReportRepository;
	
	// 休憩時間のデフォルト値
	private final Integer defaultBreakTime = 0;
	
	
	
	// タイムカードのページ
	@GetMapping("/attendance")
	public String attendance(@AuthenticationPrincipal UserDetails user, 
			Model model) {
		
		model.addAttribute("user", user);
		
		
		
		// 日付と時間を渡す
		model.addAttribute("now", LocalDateTime.now());
		
		// 勤怠管理表の今日の項目を取得し、今の状態を判別して渡す
		String status = "";
		WorkTime workTime = workTimeRepository.findByEmpIdAndWorkDate(user.getUsername(), LocalDate.now());
		if(workTime == null || workTime.getStartTime() == null) {
			// workTimeが存在しない、または出勤していない場合は「beforeWork」
			status = "beforeWork";
		} else if (workTime.getFinishTime() != null) {
			// 退勤済みの場合は「afterWork」
			status = "afterWork";
		} else if (workTime.getBreakStartTime() == null || workTime.getBreakFinishTime() != null) {
			// （出勤済みであり、）休憩していないまたは休憩が終了している場合は「working」
			status = "working";
		} else if (workTime.getBreakStartTime() != null && workTime.getBreakFinishTime() == null) {
			// 休憩しており、休憩が終了していない場合は「onBreak」
			status = "onBreak";
		} else {
			// それ以外の場合は「other」
			// 勤怠管理票の入力で正しくない状態になった場合はこの値になる
			status = "other";
		}
		
		model.addAttribute("status", status);
		
		return "attendance";
	}
	
	
	
	// 出勤
	@GetMapping("/start")
	public String start(@AuthenticationPrincipal UserDetails user, 
			Model model) {
		
		// 現在の日時を日本時間に直し、日付と時間を取得
		LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Japan"));
		LocalDate date = dateTime.toLocalDate();
		LocalTime time = dateTime.toLocalTime();
		
		WorkTime workTime = workTimeRepository.findByEmpIdAndWorkDate(user.getUsername(), date);
		if (workTime == null) {
			// データがない場合は新規に作成（社員IDと今日の日付を指定する）
			workTime = new WorkTime();
			workTime.setEmpId(user.getUsername());
			workTime.setWorkDate(date);
		}
		workTime.setStartTime(time);
		
		workTimeRepository.save(workTime);
		
		
		
		// 月次報告があれば未提出にする
		clearReport(user.getUsername(), date);
		
		return "redirect:/attendance";
	}
	
	
	
	// 退勤
	@GetMapping("/finish")
	public String finish(@AuthenticationPrincipal UserDetails user, 
			Model model) {
		
		// 現在の日時を日本時間に直し、日付と時間を取得
		LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Japan"));
		LocalDate date = dateTime.toLocalDate();
		LocalTime time = dateTime.toLocalTime();
		
		WorkTime workTime = workTimeRepository.findByEmpIdAndWorkDate(user.getUsername(), date);
		workTime.setFinishTime(time);
		
		// 退勤時は実働時間（分）を計算する
		// ただしその前に、休憩時間が未入力ならデフォルトの値を入力する（ここでは0）
		// 実働時間は開始時間と終了時間の差から休憩時間を引いた値
		if(workTime.getBreakTime() == null) workTime.setBreakTime(defaultBreakTime);
		
		if(workTime.getStartTime() != null) {
				workTime.setWorkingTime(Integer.valueOf(
						(int)ChronoUnit.MINUTES.between(workTime.getStartTime(), time)
								- workTime.getBreakTime()));
		}
		
		
		
		workTimeRepository.save(workTime);
		
		
		
		// 月次報告があれば未提出にする
		clearReport(user.getUsername(), date);
		
		return "redirect:/attendance";
	}
	
	
	
	// 休憩
	@GetMapping("/breakStart")
	public String breakStart(@AuthenticationPrincipal UserDetails user, 
			Model model) {
		
		// 現在の日時を日本時間に直し、日付と時間を取得
		LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Japan"));
		LocalDate date = dateTime.toLocalDate();
		LocalTime time = dateTime.toLocalTime();
		
		WorkTime workTime = workTimeRepository.findByEmpIdAndWorkDate(user.getUsername(), date);
		workTime.setBreakStartTime(time);
		
		// 2回目以降の休憩の場合を考え、休憩終了時間をnullにする
		workTime.setBreakFinishTime(null);
		
		workTimeRepository.save(workTime);
		
		
		
		// 月次報告があれば未提出にする
		clearReport(user.getUsername(), date);
		
		
		return "redirect:/attendance";
	}
	
	
	
	// 休憩終了
	@GetMapping("/breakFinish")
	public String breakFinish(@AuthenticationPrincipal UserDetails user,
			Model model) {
		
		// 現在の日時を日本時間に直し、日付と時間を取得
		LocalDateTime dateTime = LocalDateTime.now(ZoneId.of("Japan"));
		LocalDate date = dateTime.toLocalDate();
		LocalTime time = dateTime.toLocalTime();
		
		WorkTime workTime = workTimeRepository.findByEmpIdAndWorkDate(user.getUsername(), date);
		LocalTime breakFinishTime = time;
		workTime.setBreakFinishTime(breakFinishTime);
		
		// 休憩時間を計算
		// 2回目以降の休憩の場合は、今までの休憩時間に今回の休憩時間を加算する
		Integer breakTimeSum = workTime.getBreakTime() != null ? workTime.getBreakTime() : 0;
		workTime.setBreakTime(Integer.valueOf(
				(int)ChronoUnit.MINUTES.between(workTime.getBreakStartTime(), breakFinishTime))
						+ breakTimeSum);
		
		workTimeRepository.save(workTime);
		
		
		
		// 月次報告があれば未提出にする
		clearReport(user.getUsername(), date);
		
		return "redirect:/attendance";
	}
	
	
	
	// 勤怠管理表
	@GetMapping("/attendance_record")
	public String attendance_record(@AuthenticationPrincipal UserDetails user, 
			@RequestParam(name="month", required=false) LocalDate month, 
			Model model) {
		
		model.addAttribute("user", user);
		
		model.addAttribute("status", "show_record");
		
		
		
		// ユーザ情報を取得して渡す
		Employee employee 
				= employeeRepository.findByEmpId(user.getUsername())
						.orElseThrow();
		model.addAttribute("employee", employee);
		
		
		
		// 出勤時間の票を作成して送信
		makeRecord(employee, month, model);
		
		
		
		return "attendance_record";
	}
	
	
	
	// 勤怠管理表の修正
	@GetMapping("/attendance_record_revice")
	public String attendance_record_revice(@AuthenticationPrincipal UserDetails user,
			@RequestParam(required=false) LocalDate month,
			Model model) {
		
		model.addAttribute("user", user);
		
		// ユーザ情報を取得して渡す
		Employee employee 
				= employeeRepository.findByEmpId(user.getUsername())
						.orElseThrow();
		model.addAttribute("employee", employee);
		
		
		// 勤怠管理表を作成
		makeRecord(employee, month, model);
		
		
		
		return "attendance_record_revice";
		
	}
	
	
	
	// 修正された勤怠管理表を保存
	/*
	 *  ここで受け取るDTOであるcalenderは、ModelAttributeで受け取ること。
	 *  RequestParamで受け取ろうとするとnullになってしまい失敗する。
	 *  （逆に、DTO以外をModelAttributeで受け取ることはできない）
	 */
	@PostMapping("/attendance_record_save")
	public String attendance_record_save(@AuthenticationPrincipal UserDetails user,
			WorkTimeCalender calender, 
			@RequestParam(required=false) String location, 
			@RequestParam(required=false) String dept,
			RedirectAttributes redirectAttributes,
			Model model) {
		
		
		if(calender == null) {
			return "redirect:/attendance_record";
		}
		
		
		
		// 修正した勤怠管理表を保存する
		List<WorkTime> list = calender.getWorkTimeList();
		for(WorkTime workTime : list) {
			
			// 実働時間を入力
			// 実働時間は開始時間と終了時間の差から休憩時間を引いた値
			if(workTime.getStartTime() != null && workTime.getFinishTime() != null) {
				
				workTime.setWorkingTime(Integer.valueOf(
						(int)ChronoUnit.MINUTES
								.between(workTime.getStartTime(), workTime.getFinishTime())
						- (workTime.getBreakTime() != null 
								? workTime.getBreakTime() : 0)));
				
			}
			
			workTimeRepository.save(workTime);
			
		}
		
		// 月を取得
		LocalDate month = list.get(0).getWorkDate().withDayOfMonth(1);
		

		
		
		// 月次報告があれば削除する
		// その後locationとdeptを設定（未承認で）
		monthlyReportRepository.deleteByEmpIdAndIndexMonth(
				user.getUsername(), month);
		
		MonthlyReport report = new MonthlyReport(
				user.getUsername(), null, month);
		report.setLocation(location);
		report.setDept(dept);
		
		monthlyReportRepository.save(report);
		
		
		
		// 遷移先に送る
		redirectAttributes.addFlashAttribute("month", month);
		redirectAttributes.addFlashAttribute("msg", "修正内容を保存しました");
		
		return "redirect:/attendance_record?month=" + month;
	}
	
	
	
	// 月次報告
	@GetMapping("/monthly_report")
	public String monthly_report(@AuthenticationPrincipal UserDetails user,
			Model model){
		
		model.addAttribute("user", user);

		
		
		// 月次報告を提出済みの月のリスト
		List<LocalDate> approvalList = 
				monthlyReportRepository.findByEmpIdAndSubmittedTrueOrderByIndexMonthDesc(user.getUsername())
				.stream()
				.map(mr -> mr.getIndexMonth().withDayOfMonth(1))
				.toList();
		
		// 勤怠管理表が存在する月の一覧をリストにする（月次報告を提出済みの月は除く）
		List<LocalDate> monthList = 
				workTimeRepository.findByEmpId(user.getUsername()).stream()
				.filter(wt -> wt.getWorkDate() != null)
				.map(wt -> wt.getWorkDate().withDayOfMonth(1)).distinct()
				.filter(wt -> !(approvalList.contains(wt)))
				.toList();
				
		model.addAttribute("monthList", monthList);
		
		return "monthly_report";
	}
	
	
	
	// 提出する月次報告を確認
	@GetMapping("/monthly_report_confirm")
	public String monthly_report_confirm(@AuthenticationPrincipal UserDetails user,
			@RequestParam LocalDate month, Model model){
		
		model.addAttribute("user", user);
		
		// statusを設定
		model.addAttribute("status", "report");
		
		Employee employee = employeeRepository.findByEmpId(user.getUsername()).orElseThrow();
		
		// 勤怠管理表を表示
		makeRecord(employee, month, model);
		
		return "attendance_record";
	}
	
	
	
	// 月次報告を提出
	@GetMapping("/submit")
	public String submit(@AuthenticationPrincipal UserDetails user,
			@RequestParam LocalDate month,
			RedirectAttributes redirectAttributes){
		
		Optional<MonthlyReport> reportOpt = 
				monthlyReportRepository.findByEmpIdAndIndexMonth(user.getUsername(), month);

		// 月次報告があればそれを取得し、なければ新規作成する
		MonthlyReport report;
		Employee employee = 
				employeeRepository.findByEmpId(user.getUsername()).orElseThrow();
		if(reportOpt.isEmpty()) {
			report = new MonthlyReport(
							user.getUsername(),					
							employee.getLastName() + " " + employee.getFirstName(),
							month);
		} else {
			report = reportOpt.get();
		}
		
		// 名前を設定する。その後勤務先と部署が設定されていなければ
		// 自分の現在の勤務先と部署を入力する。
		report.setName(employee.getLastName() + " " + employee.getFirstName());
		if(report.getLocation() == null) report.setLocation(employee.getLocation());
		if(report.getDept() == null) report.setDept(employee.getDept());
		
		
		
		// submittedをtrueにして提出
		report.setSubmitted(true);
		monthlyReportRepository.save(report);
		
		
		
		// メッセージ
		redirectAttributes.addFlashAttribute("msg", "月次報告を提出しました");
		
		return "redirect:/monthly_report";
	}
	
	
	
	// 月次報告確認・承認
	@GetMapping("/admin/approval_confirm")
	public String approval_confirm(@AuthenticationPrincipal UserDetails user,
			@RequestParam String empId, @RequestParam LocalDate month, Model model) {
		
		model.addAttribute("user", user);
		
		// 月次報告が承認済みかどうか確認し、その結果を渡す
		Optional<MonthlyReport> reportOpt 
				= monthlyReportRepository.findByEmpIdAndIndexMonth(empId, month);
		if(reportOpt.isPresent() && reportOpt.get().getApprovalId() != null) {
			model.addAttribute("status", "approved");
		} else {
			model.addAttribute("status", "approval");
		}
		
		
		
		Employee employee = employeeRepository.findByEmpId(empId).orElseThrow();
		
		// 勤怠管理表を表示
		makeRecord(employee, month, model);
		
		return "attendance_record";
	}
	
	
	
	
	
	
	/*
	 *  通常メソッド（共通部分）===================================================================================
	 */
	
	// 月次報告を未提出状態へ
	// 勤務先と部署はそのまま残す
	private void clearReport(String empId, LocalDate date) {
		
		// 月次報告を取得
		Optional<MonthlyReport> reportOpt
				= monthlyReportRepository.findByEmpIdAndIndexMonth(
						empId, date.withDayOfMonth(1));
		
		// 月次報告が存在するなら未提出状態にする
		if(reportOpt.isPresent()) {
			
			MonthlyReport report = reportOpt.get();
			
			// 値を「未提出」に変更
			report.setSubmitted(false);
			
			// 承認済みの場合、承認に関する値を削除
			if(report.getApprovalId() != null) {
				report.setApprovalId(null);
				report.setApprovalName(null);
				report.setApprovalDate(null);
			}
			
			// 変更を保存
			monthlyReportRepository.save(report);
			
		}
		
		
	}
	
	
	
	// 勤怠管理の表（カレンダー）を作成して送信
	/*
	 *  勤怠管理の表はList<WorkTime>型だが、これをそのままhtmlに渡しても
	 *  そこからcontrollerに渡すことができない。
	 *  そのためDTO(Data Transfar Object)としてList<WorkTime>を格納するWorkTimeCalenderを用意し、
	 *  それを使って表を渡す。（DTOにはgetter,setterを定義する）
	 *  	参考：https://medium-company.com/springboot-thymeleaf-list/
	 */
	private void makeRecord(Employee employee, LocalDate month, Model model) {
		
		
		
		// 「期間を選択」に格納する、月の一覧を作成
		// 範囲は入社した月（の第1日）から今月まで、並び順はその逆順
		LocalDate firstMonth = employee.getJoining() != null ? 
					employee.getJoining().withDayOfMonth(1) : LocalDate.now().withDayOfMonth(1);
		List<LocalDate> monthList = new ArrayList<>();
		for(LocalDate m = LocalDate.now(); !(m.isBefore(firstMonth)); m = m.minusMonths(1)) {
			monthList.add(m);
		}
		model.addAttribute("monthList", monthList);
		
		
		
		// 出勤記録を取得する月を指定
		if(month == null) month = LocalDate.now();
		LocalDate start = month.withDayOfMonth(1);
		LocalDate end = month.withDayOfMonth(month.lengthOfMonth());
		
		// 指定した範囲を渡す
		model.addAttribute("month", month);
		
		
		
		// employeeを渡す
		model.addAttribute("employee", employee);
		
		
		
		// 月次報告の情報とその状態を渡す
		// 勤怠管理表に対応する月次報告の状態：reportStatus
		// 未提出ならnull、提出済みなら「submitted」、承認済みなら「approved」
		Optional<MonthlyReport> reportOpt = 
				monthlyReportRepository.findByEmpIdAndIndexMonth(
						employee.getEmpId(), start);
		
		MonthlyReport report = null;
		String reportStatus = null;
		
		if(reportOpt.isPresent()) {
			
			report = reportOpt.get();
			
			if(!(report.isSubmitted())) { // 未提出
				reportStatus = null;
			}else if(report.getApprovalId() == null) { // 提出済みで未承認
				reportStatus = "submitted";
			}else {	// 承認済み
				reportStatus = "approved";
			}
			
		} else {
			report = new MonthlyReport();
		}
		
		model.addAttribute("report", report);
		model.addAttribute("reportStatus", reportStatus);
		
		
		
		// 祝日の一覧を取得
		List<String> holidays = null;
		
		try {
			
			Resource resource = new UrlResource("https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv");
			
			try (InputStream in = resource.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in, "SJIS"))) {
				
				// 取得した要素を1行ずつListに格納
				List<String> list = new ArrayList<>();
				String line = null;
				while((line = reader.readLine()) != null) {
					list.add(line);
				}
				
				// streamで使用する定数を定義
				final int year = month != null ? month.getYear() : LocalDate.now().getYear();
				final Function<String, String> changeFormat = (s -> 
						LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy/M/d"))
								.format(DateTimeFormatter.ISO_LOCAL_DATE));
				
				// 日付のみ取得して新しいListに格納
				holidays = list.stream()
						.map(s -> Arrays.asList(s.split(",")).get(0)) // 最初の要素（日付）を取得
						.filter(s -> s.matches(year + "[0-9/]*")) // 指定された年から始まるもののみにする
																  // タイトル行の削除と次の工程の軽量化のため
						.map(changeFormat) // フォーマットの変更
						.toList();
				
			}
			
		}catch(Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		}

		model.addAttribute("holidays", holidays);
		
		
		
		// 指定された期間の出勤記録をMapとして取得
		Map<LocalDate, WorkTime> workTimeMap
				= workTimeRepository.findByEmpIdAndWorkDateBetween(employee.getEmpId(), start, end)
						.stream().collect(Collectors.<WorkTime, LocalDate, WorkTime>toMap(
								wt -> wt.getWorkDate(),
								wt -> wt));
		
		// workTimeの一覧を作成（出勤していない日は日付のみ）
		List<WorkTime> workTimeList = new ArrayList<>();
		for(LocalDate date = start; !(date.isAfter(end)); 
				date = date.plusDays(1)) {
			
			// workTimeMapに入力値がある場合、その値を追加
			// そうでない場合は社員IDと日付のみが入力されたWorkTimeを追加
			if(workTimeMap.containsKey(date)) {
				workTimeList.add(workTimeMap.get(date));
			} else {
				WorkTime blank = new WorkTime();
				blank.setEmpId(employee.getEmpId());
				blank.setWorkDate(date);
				workTimeList.add(blank);
			}

		}
		
		// WorkTimeCalenderに格納して渡す
		WorkTimeCalender calender = new WorkTimeCalender();
		calender.setWorkTimeList(workTimeList);
		model.addAttribute("calender", calender);
		
		
		
		// 区分のリストを渡す
		List<Division> divisionList = List.of(Division.values());
		model.addAttribute(divisionList);
		
		
		// 区分ごとの日数をMapに格納
		Map<String, Integer> sum = new LinkedHashMap<>();
		
		// 出勤日数
		sum.put("出勤日数", workTimeRepository.countByEmpIdAndWorkingTimeGreaterThanEqualAndWorkDateBetween(
				employee.getEmpId(), 0, start, end));
		
		// 休暇日数
		for (Division div : Division.values()) {
			Integer count = workTimeRepository
					.countByEmpIdAndDivisionAndWorkDateBetween(employee.getEmpId(), div, start, end);
			sum.put(div.toString(), count);
		}
		model.addAttribute("sum", sum);
		
	}
	
	
	
	
	
}
