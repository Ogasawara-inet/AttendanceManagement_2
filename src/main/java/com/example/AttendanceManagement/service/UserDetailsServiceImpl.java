package com.example.AttendanceManagement.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.AttendanceManagement.entity.Employee;
import com.example.AttendanceManagement.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final EmployeeRepository employeeRepository;
	
	
	
	@Override
	public UserDetails loadUserByUsername(String empId) throws UsernameNotFoundException {
		// TODO 自動生成されたメソッド・スタブ
		
		Employee employee 
				= employeeRepository.findByEmpId(empId)
						.orElseThrow(() -> new UsernameNotFoundException(
								empId + " is not found"));
		
		return new User(
				employee.getEmpId(), 
				employee.getPassword(),
				AuthorityUtils.createAuthorityList(employee.getAuth().toString())
			);
		
	}

}
