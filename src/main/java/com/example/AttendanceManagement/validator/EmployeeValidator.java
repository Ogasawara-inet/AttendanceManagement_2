package com.example.AttendanceManagement.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.example.AttendanceManagement.entity.Employee;
import com.example.AttendanceManagement.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeeValidator implements Validator{

	private final EmployeeRepository employeeRepository;
	
	@Override
	public boolean supports(Class<?> clazz) {

		return Employee.class.isAssignableFrom(clazz);
		
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		String empId = ((Employee)target).getEmpId();
		
		// 社員IDが未入力の場合はエラー
		if(empId == null || empId.isBlank()) {
			errors.rejectValue("empId", "empId_input_error", 
					"社員IDが未入力です");
			return;
		}
		
		// その社員IDを持つ社員がすでに登録されている場合はエラー
		if(employeeRepository.findByEmpId(empId).isPresent()) {
			errors.rejectValue("empId", "registered_empId", 
					"その社員IDはすでに使用されています");
			return;
		}
		
	}

}
