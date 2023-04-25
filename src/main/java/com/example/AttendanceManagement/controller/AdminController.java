package com.example.AttendanceManagement.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.StringUtils;

import com.example.AttendanceManagement.entity.Employee;
import com.example.AttendanceManagement.entity.MonthlyReport;
import com.example.AttendanceManagement.repository.EmployeeRepository;
import com.example.AttendanceManagement.repository.MonthlyReportRepository;
import com.example.AttendanceManagement.util.Auth;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

	private final EmployeeRepository employeeRepository;
	private final MonthlyReportRepository monthlyReportRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	
	
	// 従業員一覧で表示する件数
	private final int empListSize = 20;
	
	
	
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
				employee = employeeRepository.findByEmpId(empId).orElseThrow();
				model.addAttribute("status", "revice");
				
			} else {
				model.addAttribute("status", "register");
			}
			
		} else {
			model.addAttribute("status", status);
		}
		
		// 入社日が未入力の場合、今日の日付を設定
		if(employee == null) employee = new Employee();
		if(employee.getJoining() == null) employee.setJoining(LocalDate.now());
		
		// 確認用のAuth権限
		model.addAttribute("adminAuth", Auth.ADMIN);
		
		
		
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
			@RequestParam(required=false) String status,
			Model model) {
		
		// GetMappingと同じ処理をする
		return register(user, employee, null, status, model);
		
	}
	
	
	
	// 登録確認
	@PostMapping("/register_confirm")
	public String register_confirm(@AuthenticationPrincipal UserDetails user, 
			@Validated @ModelAttribute Employee employee, 
			BindingResult result,
			@RequestParam(required=false) String passwordAgain,
			@RequestParam(required=false) String adminCheck,
			@RequestParam(required=false) String status,
			Model model) {
		
		model.addAttribute("user", user);

		
		
		// statusがnullの場合はregisterを格納（基本は起こらない）
		if(status == null || status.isBlank()) status = "register";
		model.addAttribute("status", status);
		
		// 権限を設定
		employee.setAuth(adminCheck != null && adminCheck.equals("checked") 
				? Auth.ADMIN : Auth.EMPLOYEE);
		
		// 確認用のAuth権限
		model.addAttribute("adminAuth", Auth.ADMIN);
		
		
		
		// パスワードと確認用パスワードが一致しない場合にバリデーションを追加
		/*
		 * Stringは参照型として扱われる場合があり、
		 * その場合は「==」で比較した結果は正しくなくなるため
		 * 比較にはequalsを用いる。
		 */
		if (!(passwordAgain.equals(employee.getPassword()))) {
			result.addError(new FieldError(
					result.getObjectName(),
					"password", 
					"再入力されたパスワードが正しくありません"));
		}
		
		// 入力エラーが発生した場合は元の画面へ
		if(result.hasErrors()) {
			model.addAttribute("employee", employee);
			return "admin/register";
		}
		
		
		
		// 隠したパスワードを作成
		String hiddenPassword
				= StringUtils.repeat("*", employee.getPassword().length());
		model.addAttribute("hiddenPassword", hiddenPassword);
		
		model.addAttribute("employee", employee);
		model.addAttribute("complete", false);
		
		return "admin/register_confirm";
	}
	
	
	
	// 従業員登録
	@PostMapping("/save_employee")
	public String save_employee(@AuthenticationPrincipal UserDetails user, 
			@ModelAttribute Employee employee, 
			@RequestParam(required=false) String status, 
			RedirectAttributes redirectAttributes, Model model) {
		
		model.addAttribute("user", user);
		
		// 隠したパスワードを作成
		String hiddenPassword
				= StringUtils.repeat("*", employee.getPassword().length());
		model.addAttribute("hiddenPassword", hiddenPassword);
		
		
		
		// パスワードを暗号化して入れ直す
		String encodedPassword = passwordEncoder.encode(employee.getPassword());
		employee.setPassword(encodedPassword);
		
		
		// 従業員IDがない場合は生成
		if(employee.getEmpId() == null || employee.getEmpId().isBlank()) {
			
			String empId;
			boolean isAdmin = (employee.getAuth() == Auth.ADMIN);
			long num = employeeRepository.count();
			do {
				empId = String.format("%s%08d", 
						(isAdmin ? "A" : "E"), num);
				num++;
				if(num >= 100_000_000) num = 0; // 9桁になったら0に戻す
			}while(employeeRepository.findByEmpId(empId).isPresent());
			
			employee.setEmpId(empId);
			model.addAttribute("msg", "従業員IDを発行しました（ID:" + empId + "）");
			
		}
		
		// DBに登録
		employeeRepository.save(employee);
		
		
		
		// statusによって遷移先を変更
		if(status.equals("register")) {
			
			model.addAttribute("employee", employee);
			model.addAttribute("status", status);
			model.addAttribute("complete", true);
			return "/admin/register_confirm";
			
		}else {
			
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
			@RequestParam String id, RedirectAttributes redirectAttributes,
			Model model) {
		
		Employee employee = employeeRepository.findById(id).orElseThrow();
		
		employeeRepository.deleteById(id);
		
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
			
			// 検索ワードをIDとして検索
			Optional<Employee> empOpt
					= employeeRepository.findByEmpId(searchword);
			
			// IDとして検索して見つかった場合はそれをListに格納
			// 見つからなかった場合はキーワードを名前として検索
			if (empOpt.isPresent()) {
				empList.add(empOpt.get());
			} else {
				
				// 検索ワードの1文字目を取得
				String s = searchword.substring(0,1);
				
				// 1文字目で苗字と名前を検索
				empList = employeeRepository
						.findByLastNameLikeOrderByEmpId("%" + s + "%");
				empList.addAll(employeeRepository
						.findByFirstNameLikeOrderByEmpId("%" + s + "%"));
				
				// その中から重複要素を削除し、
				// フルネームに検索ワードが含まれるもののみに絞る
				empList = empList.stream()
						.distinct()
						.filter(e -> e != null
								&& e.getLastName().concat(e.getFirstName())
										.contains(searchword))
						.toList();
				
			}
			
		} else {

			// キーワードが未入力なら全件表示
			empList = employeeRepository.findAll();
			
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
			
			// 従業員IDとして検索
			if (searchAll) {
				monthlyReportList = monthlyReportRepository
						.findByEmpIdAndSubmittedTrueOrderByIndexMonthDesc(searchword);
			} else {
				monthlyReportList = monthlyReportRepository
						.findByEmpIdAndSubmittedTrueAndApprovalIdIsNullOrderByIndexMonthDesc(searchword);
			}
			
			// 従業員IDとして検索して見つからなかった場合、
			// 従業員名として検索する
			if (monthlyReportList == null || monthlyReportList.size() == 0) {
				
				// 検索ワードの1文字目を取得
				// 検索ワードが空文字なら空文字を返す。
				String s = !(searchword.isBlank()) ? searchword.substring(0,1) : "";
				
				// 1文字目で従業員名を検索
				if (searchAll) {
					monthlyReportList = monthlyReportRepository
							.findByNameLikeAndSubmittedTrueOrderByIndexMonthDesc("%" + s + "%");
				} else {
					monthlyReportList = monthlyReportRepository
							.findByNameLikeAndSubmittedTrueAndApprovalIdIsNullOrderByIndexMonthDesc("%" + s + "%");
				}

				// その中から重複要素を削除し、
				// 従業員名に検索ワードが含まれるもののみに絞る
				// 従業員名の苗字と名前の間には半角スペースがあるので、それを除外して検索
				monthlyReportList = monthlyReportList.stream()
						.distinct()
						.filter(m -> m != null && m.getName().replaceAll(" ", "")
								.contains(searchword))
						.toList();
				
			
			} else {
				
				// 検索ワードが空欄の場合は全件取得
				monthlyReportList = monthlyReportRepository.findAll();
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
				= employeeRepository.findByEmpId(user.getUsername()).orElseThrow();
		
		
		// Approvalを取得して承認
		MonthlyReport report = monthlyReportRepository.findByEmpIdAndIndexMonth(empId, month).orElseThrow();
		report.setApprovalId(employee.getEmpId());
		report.setApprovalName(employee.getLastName() + " " + employee.getFirstName());
		report.setApprovalDate(LocalDate.now());
		monthlyReportRepository.save(report);
		
		redirectAttributes.addFlashAttribute(
				"msg", "月次報告を承認しました：" + report.getName()
				+ " (" + report.getEmpId() + ") "
				+ report.getIndexMonth().format(
						DateTimeFormatter.ofPattern("yyyy-MM")));
		
		return "redirect:/admin/approval_confirm?empId="+ empId + "&month=" + month ;
	}
	
	
	
	
	
	
	
}
