package com.training.employee.daoImpl;

import com.training.employee.dao.EmployeeDAO;
import com.training.employee.entity.Employee;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class EmployeeDAOImpl implements EmployeeDAO {

    List<Employee> employeeList = new ArrayList<>(Arrays.asList(
            new Employee("I100", "John Doe", "Manager"),
            new Employee("I101", "Jane Smith", "Developer"),
            new Employee("I102", "Michael Johnson", "Designer")
    ));

    @Override
    public List<Employee> getAllEmployees() {
        return employeeList;
    }

    /**
     * @param employee
     * @return
     */
    @Override
    public List<Employee> addEmployeeToList(Employee employee) {
        employeeList.add(employee);
        return employeeList;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Employee getEmployeeById(String id) {
        Map<String, Employee> employeeMap = employeeList.stream()
                .collect(Collectors.toMap(Employee::getId, employee -> employee));
        return employeeMap.get(id);
    }

    /**
     * @param id
     * @return updated List after Delete the Object from the list
     */
    @Override
    public Map<String, Employee> deleteEmployeeById(String id) {
        Map<String, Employee> employeeMap = employeeList.stream()
                .collect(Collectors.toMap(Employee::getId, employee -> employee));
        employeeMap.remove(id);
        return employeeMap;
    }


}
