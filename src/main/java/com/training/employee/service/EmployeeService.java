package com.training.employee.service;

import com.training.employee.entity.Employee;

import java.util.List;
import java.util.Map;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    List<Employee> addEmployeeToList(Employee employee);
    Employee getEmployeeById(String id);
    Map<String,Employee> deleteEmployeeById(String id);

}
