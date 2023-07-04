package com.example.AttendanceManagement.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import com.example.AttendanceManagement.entity.Employee;
import com.example.AttendanceManagement.entity.MonthlyReport;
import com.example.AttendanceManagement.entity.Passwords;
import com.example.AttendanceManagement.repository.EmployeeMapper;
import com.example.AttendanceManagement.repository.MonthlyReportMapper;
import com.example.AttendanceManagement.util.Auth;
import com.example.AttendanceManagement.validator.EmployeeValidator;
import com.example.AttendanceManagement.validator.PasswordsValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

	private final EmployeeMapper employeeMapper;
	private final MonthlyReportMapper monthlyReportMapper;
	
	private final PasswordEncoder passwordEncoder;
	
	// カスタムバリデーター
	private final EmployeeValidator employeeValidator;
	private final PasswordsValidator passwordsValidator;
	
	// バリデーター登録
	@InitBinder("employee")
	public void employeeInitBinder(WebDataBinder binder) {
		binder.addValidators(employeeValidator);
	}
	@InitBinder("passwords")
	public void passwordsInitBinder(WebDataBinder binder) {
		binder.addValidators(passwordsValidator);
	}
	
	
	
	// 従業員一覧で表示する件数
	private final int empListSize = 10;
	
	
	
	// 管理者ログイン画面
	/*
	 *  /admin/login ：MainControllerに記述
	 */
	
	
	
	// 管理者ログイン画面からログインした場合の遷移先
	@GetMapping("/home")
	public String home(@AuthenticationPrincipal UserDetails user, 
			Model model) {
		
		model.addAttribute("user", user);
		
		return "admin/home";
	}
	
	
	
	// 従業員登録画面
	@GetMapping("/register")
	public String register(@AuthenticationPrincipal UserDetails user, 
			@ModelAttribute Employee employee, 
			@ModelAttribute Passwords passwords,
			@RequestParam(required=false) String empId, 
			@RequestParam(required=false) String status,
			Model model) {
		
		// statusが未設定の場合、値を設定（入力済みの場合は保持する）
		// その際、empIdが指定されていれば登録情報をDBから取得
		/*
		 * statusの値は、
		 * 		新規登録：register
		 *		他のユーザの登録情報変更：revice
		 *		従業員による、自身の登録情報変更：empRevice
		 *		管理者による、自身の登録情報変更：adminRevice
		 */
		if(status == null || status.isBlank()) {
			
			// empIdが指定されていたら「登録内容変更」
			// 指定されていないなら「新規登録」
			if(empId != null && !(empId.isBlank())) {
				employee = employeeMapper.find(empId).orElseThrow();
				model.addAttribute("status", "revice");
				
			} else {
				model.addAttribute("status", "register");
			}
			
		} else {
			model.addAttribute("status", status);
		}
		
		// 入社日が未入力の場合、今日の日付を設定
		if(employee == null) employee = new Employee();
		if(employee.getJoining() == null) {
			employee.setJoining(LocalDateTime.now(ZoneId.of("Japan")).toLocalDate());
		}
		
		
		
		// 確認用のAuth権限
		model.addAttribute("adminAuth", Auth.ADMIN);
		
		
		
		model.addAttribute("passwords", passwords);
		
		model.addAttribute("employee", employee);
		
		model.addAttribute("user", user);
		
		return "admin/register";
	}
	
	
	
	// 従業員登録画面（確認画面から戻った場合の処理）
	// Postで受け取るのはemployeeに格納された値を保持するため
	/*
	 * このメソッドを呼び出すにはCSRFトークンを取得していなければならない
	 * トークンを取得させるには、register_confirm側のformのinputで設定するか、
	 * SecurityConfigのSecurityFilterChainで設定する。
	 */
	@PostMapping("/register")
	public String register_from_confirm(@AuthenticationPrincipal UserDetails user, 
			@ModelAttribute Employee employee,  
			@ModelAttribute Passwords passwords, 
			@RequestParam(required=false) String status,
			Model model) {
		
		// GetMappingと同じ処理をする
		return register(user, employee, passwords, null, status, model);
		
	}
	
	
	
	// 登録確認
	@PostMapping("/register_confirm")
	public String register_confirm(@AuthenticationPrincipal UserDetails user, 
			@Validated @ModelAttribute Employee employee, 
			BindingResult result,
			@Validated @ModelAttribute Passwords passwords,
			BindingResult passwordsResult,
			@RequestParam(required=false) String adminCheck,
			@RequestParam(required=false) String nameReverseOrder,
			@RequestParam(required=false) String addMiddleName,
			@RequestParam(required=false) String status,
			Model model) {
		
		model.addAttribute("user", user);
		
		// statusがnullの場合はregisterを格納（基本は起こらない）
		if(status == null || status.isBlank()) status = "register";
		model.addAttribute("status", status);
		
		
		
		// 姓名順とミドルネームの有無の格納（前のページに戻ったとき用）
		model.addAttribute("nameReverseOrder", nameReverseOrder);
		model.addAttribute("addMiddleName", addMiddleName);
		
		// ミドルネームが無効なら、入力されているミドルネームを削除
		if(addMiddleName == "checked") employee.setMiddleName(null);
		
		// フルネームの格納
		String fullName = "";
		String fullNameKana = "";
		
		String middleName = employee.getMiddleName();

		if(nameReverseOrder == null) { // 姓→名の順
			if(middleName == null || middleName.isBlank()) { // ミドルネームなし
				fullName = employee.getLastName() + " " + employee.getFirstName();
				fullNameKana = employee.getLastNameKana() + " " + employee.getFirstNameKana();
			} else { // ミドルネームあり
				fullName = employee.getLastName() + " " + employee.getMiddleName() 
						+ " " + employee.getFirstName();
				fullNameKana = employee.getLastNameKana() + " " + employee.getMiddleName() 
						+ " " + employee.getFirstNameKana();
			}
		} else { // 名→姓の順
			if(middleName == null || middleName.isBlank()) { // ミドルネームなし
				fullName = employee.getFirstName() + " " + employee.getLastName();
				fullNameKana = employee.getFirstNameKana() + " " + employee.getLastNameKana();
			} else { // ミドルネームあり
				fullName = employee.getFirstName() + " " + employee.getMiddleName() 
						+ " " + employee.getLastName();
				fullNameKana = employee.getFirstNameKana() + " " + employee.getMiddleName() 
						+ " " + employee.getLastNameKana();
			}
		}
		
		employee.setFullName(fullName);
		employee.setFullNameKana(fullNameKana);
		
		
		
		// 権限を設定
		employee.setAuth(adminCheck != null && adminCheck.equals("checked") 
				? Auth.ADMIN : Auth.EMPLOYEE);
		
		// 確認用のAuth権限
		model.addAttribute("adminAuth", Auth.ADMIN);
		
		
		
		// 入力エラーが発生した場合は元の画面へ
		if(result.hasErrors() || passwordsResult.hasErrors()) {
			model.addAttribute("employee", employee);
			return "admin/register";
		}
		
		
		
		// 隠したパスワードを作成
		String hiddenPassword
				= StringUtils.repeat("*", employee.getPassword().length());
		model.addAttribute("hiddenPassword", hiddenPassword);
		
		model.addAttribute("employee", employee);
		model.addAttribute("passwords", passwords);
		model.addAttribute("complete", false);
		
		return "admin/register_confirm";
	}
	
	
	
	// 従業員登録
	@PostMapping("/save_employee")
	public String save_employee(@AuthenticationPrincipal UserDetails user, 
			@ModelAttribute Employee employee, 
			@ModelAttribute Passwords passwords,
			@RequestParam(required=false) String status, 
			RedirectAttributes redirectAttributes, Model model) {
		
		model.addAttribute("user", user);
		
		// 隠したパスワードを作成
		String hiddenPassword
				= StringUtils.repeat("*", employee.getPassword().length());
		model.addAttribute("hiddenPassword", hiddenPassword);
		
		// パスワードを暗号化して入れ直す
		String encodedPassword = passwordEncoder.encode(passwords.getPassword());
		employee.setPassword(encodedPassword);
		
		// DBに登録
		employeeMapper.upsert(employee);
		
		
		
		// statusによって遷移先を変更
		if(status.equals("register")) {
			
			model.addAttribute("employee", employee);
			model.addAttribute("status", status);
			model.addAttribute("complete", true);
			return "admin/register_confirm";
			
		} else {
			
			redirectAttributes.addFlashAttribute("msg", "編集が完了しました");
			return "redirect:/admin/employee_list";
			
		}
		
	}
	
	
	
	// 従業員削除
	/*
	 * このメソッド以外では従業員IDを使って管理しているが、
	 * 何かしらの原因で同一の従業員IDのアカウントが重複して
	 * 作成されてしまった場合を考え
	 * このメソッドではID（通し番号）を使用して削除を行う。
	 */
	@PostMapping("/delete_employee")
	public String delete_employee(@AuthenticationPrincipal UserDetails user, 
			@RequestParam Long id, RedirectAttributes redirectAttributes,
			Model model) {
		
		Employee employee = employeeMapper.findById(id).orElseThrow();
		
		employeeMapper.deleteById(id);
		
		redirectAttributes.addFlashAttribute("msg", "削除が完了しました：" 
				+ employee.getLastName() + employee.getFirstName()
				+ "(" + employee.getEmpId() + ")");
		
		return "redirect:/admin/employee_list";
	}
	
	
	
	// 従業員一覧表示
	@GetMapping("/employee_list")
	public String employee_list(@AuthenticationPrincipal UserDetails user,
			@RequestParam(required=false) String searchword,
			@PageableDefault(size=empListSize) Pageable pageable, 
			Model model) {
		
		model.addAttribute("user", user);
		model.addAttribute("searchword", searchword);
		
		
		
		// 検索機能
		List<Employee> empList = new ArrayList<>();
		
		if(searchword != null && !(searchword.isBlank())) {
			
			// 検索ワードを社員IDとして検索
			Optional<Employee> empOpt
					= employeeMapper.find(searchword);
			
			// IDとして検索して見つかった場合はそれをListに格納
			if (empOpt.isPresent()) empList.add(empOpt.get());

			// キーワードを名前として検索、結果を格納
			empList.addAll(employeeMapper.search(searchword));
			
		} else {

			// キーワードが未入力なら全件表示し、社員IDの昇順で並び替え
			empList = employeeMapper.findAll();
			Collections.sort(empList, (e1, e2) -> e1.getEmpId().compareTo(e2.getEmpId()));
			
		}
		
		
		
		// ページネーションのPageを作成
		Page<Employee> page = new PageImpl<>(
				empList.subList(
						(int)pageable.getOffset(),
						(int)Math.min(pageable.getOffset() + pageable.getPageSize(), 
								empList.size())),
				pageable,
				empList.size());
		
		model.addAttribute("page", page);
		
		return "admin/employee_list";
	}
	
	
	
	// 月次報告の一覧を表示
	@GetMapping("/approval_report")
	public String approval(@AuthenticationPrincipal UserDetails user,
			@RequestParam(required=false) String searchword, // 検索する従業員ID
			@RequestParam(required=false) String showApproved, // 承認済みも表示
			Model model) {
		
		boolean searchAll = showApproved != null && showApproved.equals("checked");
		
		model.addAttribute("user", user);
		model.addAttribute("searchword", searchword);
		model.addAttribute("showApproved", searchAll);
		
		// 月次報告の一覧を取得して渡す
		// 取得した際にはgetListをtrueにして、取得してない状態と区別するようにする
		List<MonthlyReport> monthlyReportList = new ArrayList<>();
		boolean getList = false; // 検索を実行したかどうかの判定
		if (searchword != null) {
			
			if(!(searchword.isBlank())) {
				
				// 社員IDとして検索
				if (searchAll) {
					monthlyReportList.addAll(monthlyReportMapper.findByEmpId(searchword));
				} else {
					monthlyReportList.addAll(monthlyReportMapper.findByEmpIdNotApproved(searchword));
				}
				
				// 社員名として検索
				if (searchAll) {
					monthlyReportList.addAll(monthlyReportMapper.findByName(searchword));
				} else {
					monthlyReportList.addAll(monthlyReportMapper.findByNameNotApproved(searchword));
				}
				
			} else {
				
				// 検索ワードが空欄の場合は全件取得
				monthlyReportList = monthlyReportMapper.findAll();
				
			}
			
			getList = true;
			
		}
		
		model.addAttribute("monthlyReportList", monthlyReportList);
		model.addAttribute("getList", getList);
		
		
		return "admin/approval_report";
	}
	
	
	
	// 月次報告確認・承認
	/*
	 *  /admin/approval_confirm ：AttendanceControllerに記述
	 */
	
	
	
	// 月次報告承認
	@GetMapping("/approval_save")
	public String approval_save(@AuthenticationPrincipal UserDetails user,
			RedirectAttributes redirectAttributes,
			@RequestParam String empId, @RequestParam LocalDate month) {
		
		// 自身の登録情報を取得
		Employee employee 
				= employeeMapper.find(user.getUsername()).orElseThrow();
		
		
		
		// Approvalを取得して承認
		MonthlyReport report = monthlyReportMapper.find(empId, month).orElseThrow();
		report.setApprovalId(employee.getEmpId());
		report.setApprovalName(employee.getLastName() + " " + employee.getFirstName());
		report.setApprovalDate(LocalDateTime.now(ZoneId.of("Japan")).toLocalDate());
		monthlyReportMapper.update(report);
		
		redirectAttributes.addFlashAttribute(
				"msg", "月次報告を承認しました：" + report.getName()
				+ " (" + report.getEmpId() + ") "
				+ report.getIndexMonth().format(
						DateTimeFormatter.ofPattern("yyyy-MM")));
		
		return "redirect:/admin/approval_confirm?empId="+ empId + "&month=" + month ;
	}
	
	
	
	
	
	
	
}
