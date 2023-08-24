package com.training.employee.serviceImpl;

import com.training.employee.dao.EmployeeDAO;
import com.training.employee.entity.Employee;
import com.training.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDAO employeeDAO;

    @Autowired
    public EmployeeServiceImpl(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }
    @Override
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }

    @Override
    public List<Employee> addEmployeeToList(Employee employee) {
        return employeeDAO.addEmployeeToList(employee);
    }

    /**
     * Method to get Employee by id
     * @param id
     * @return
     */
    @Override
    public Employee getEmployeeById(String id) {
        return employeeDAO.getEmployeeById(id);
    }

    /**
     * Method to delete Employee by Id
     * @param id
     * @return
     */
    @Override
    public Map<String,Employee> deleteEmployeeById(String id) {
        return employeeDAO.deleteEmployeeById(id);
    }

}
