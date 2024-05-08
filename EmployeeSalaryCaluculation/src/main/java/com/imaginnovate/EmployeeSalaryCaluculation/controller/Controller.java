package com.imaginnovate.EmployeeSalaryCaluculation.controller;

import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import com.imaginnovate.EmployeeSalaryCaluculation.EmployeeServiceException;
import com.imaginnovate.EmployeeSalaryCaluculation.model.Employee;
import com.imaginnovate.EmployeeSalaryCaluculation.model.EmployeeTds;
import com.imaginnovate.EmployeeSalaryCaluculation.service.EmployeeService;

import jakarta.websocket.server.PathParam;

@RestController
public class Controller {
	
	@Autowired
	EmployeeService employeeService;
	
	@PostMapping("/v1/api/employee")
	public Employee createEmployee(@RequestBody @Valid Employee employee) {
		
		if(!employeeService.validatePhoneNumbers(employee)) {
			throw new EmployeeServiceException("Invalid phone number");
		}
		if(!employeeService.validateEmail(employee)) {
			throw new EmployeeServiceException("Invalid email address");
		}
		return employeeService.createEmployee(employee);
		
	}
	
	@GetMapping("/v1/api/employeeTDS/{employeeId}")
	public EmployeeTds getEmployeeTDS(@PathVariable Long employeeId) throws ParseException{
		
		return employeeService.getEmployeeTDS(employeeId);
		
	}

}
