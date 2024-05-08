package com.imaginnovate.EmployeeSalaryCaluculation.service;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.cglib.core.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.imaginnovate.EmployeeSalaryCaluculation.EmployeeServiceException;
import com.imaginnovate.EmployeeSalaryCaluculation.model.Employee;
import com.imaginnovate.EmployeeSalaryCaluculation.model.EmployeeTds;

import ch.qos.logback.core.testUtil.RandomUtil;

@Service
public class EmployeeService {
	
	private  Map<Long ,Employee> employeeObjects = new HashMap();
	
	public boolean validatePhoneNumbers(Employee employee) {
		Pattern ptrn = Pattern.compile("(0/91)?[7-9][0-9]{9}"); 
		for(String phone:employee.getPhoneNumber()) {
	   
		Matcher match = ptrn.matcher(phone);  
		if(!(match.find() && match.group().equals(phone))) {
			return false;
		}
		}
		return true;
	}
	
	
	public boolean validateEmail(Employee employee) {
		
		 Pattern VALID_EMAIL_ADDRESS_REGEX =    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
		 Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(employee.getEmail());
		 return matcher.matches();
	}
	
	public Employee createEmployee(Employee employee) {
	  Long employeeId = new SecureRandom().nextLong();	
	  while(employeeObjects.containsKey(employeeId)) {
		  employeeId = new SecureRandom().nextLong();
	  }
	  employee.setEmployeeId(employeeId);
	  employeeObjects.put(employeeId, employee);
	  return employee;
	}
	
	public EmployeeTds getEmployeeTDS(Long employeeId) throws ParseException {
		
		Employee employee = employeeObjects.get(employeeId);
		EmployeeTds employeeTds = new EmployeeTds();
		if(ObjectUtils.isEmpty(employee)) {
			throw new EmployeeServiceException("Employee Object Not found for the id" + employeeId);
		}
		calculate(employee,employeeTds);
		return employeeTds;
		
	}
	
	public void calculate(Employee employee,EmployeeTds employeeTds ) throws ParseException {
        double totalSalary = caluculateYearlySalary(employee.getSalary(),employee.getDoj());
        double tax = 0;
        double cess = 0;
        double totalSalaryFortax= 0;
        if(totalSalary <= 250000) {
            tax = 0;
        }
        if(totalSalary > 250000 && totalSalary <= 500000) {
        	totalSalaryFortax = totalSalary - 250000;
            tax = tax + (double)(totalSalaryFortax*(5/100.0f));
        }
         if(totalSalary > 500000 && totalSalary <= 1000000) {
        	totalSalaryFortax = totalSalary -  500000;
            tax = tax + (double)(totalSalaryFortax*(10/100.0f))+(double)(250000*(5/100.0f)) ;
        }
         if(totalSalary > 1000000) {
        	 totalSalaryFortax = totalSalary -  1000000;
            tax = tax + (double)(totalSalaryFortax*(20/100.0f))+ (double)(500000*(10/100.0f))+(double)(250000*(5/100.0f)) ;
            if(totalSalary > 2500000) {
            	
            	cess =  (double)((totalSalary-2500000)*(2/100.0f));
            }
        }
        employeeTds.setEmployeeId(employee.getEmployeeId());
        employeeTds.setFirstName(employee.getFirstName());
        employeeTds.setLastName(employee.getLastName());
        employeeTds.setYearlySalary(totalSalary);
        employeeTds.setTaxAmount(tax);
        employeeTds.setCessAmount(cess);
    }
	
	public double caluculateYearlySalary(double salary,Date doj) throws ParseException {
		   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
	        Date date1 = simpleDateFormat.parse("2024-04-01");
	        if(doj.before(date1)) {
	        	return 12 * salary;
	        }else {
	        	long monthsBetween = ChronoUnit.MONTHS.between(
	        	        LocalDate.parse(getStringDate(doj)).withDayOfMonth(1),
	        	        LocalDate.parse("2025-03-31").withDayOfMonth(1));
	        	System.out.println(monthsBetween);
	        	int joinDate =doj.getDate();
	        	int salariedDays = 30- joinDate;
	        	
	        	return salary * monthsBetween + (salary/30*salariedDays);
			}
	}
	
	
	public String getStringDate(Date doj) {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		String dateString = df.format(doj);
		return dateString;
	}

}
