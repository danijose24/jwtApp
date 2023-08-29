package com.training.employee.controller;

import com.training.employee.entity.Employee;
import com.training.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.training.constants.ApplicationConstants.*;

@RestController
@RequestMapping(EMPLOYEES)
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    /**
     * Retrieves a list of all employees.
     *
     * @return A list containing all employees.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(GET_EMPLOYEES)
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    /**
     * Retrieves an employee by their ID.
     *
     * @param id The ID of the employee to retrieve.
     * @return The employee with the specified ID.
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(GET_EMPLOYEE_BY_ID + "/{id}")
    public Employee getEmployeeById(@PathVariable String id) {
        return employeeService.getEmployeeById(id);
    }

    /**
     * Adds a new employee to the list.
     *
     * @return A list containing all employees including the newly added one.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(ADD_EMPLOYEE_TO_LIST)
    public List<Employee> addEmployeeById() {
        Employee empObj = new Employee("I103", "Daniel", "Engineer");
        return employeeService.addEmployeeToList(empObj);
    }

    /**
     * Deletes an employee by their ID.
     *
     * @param id The ID of the employee to delete.
     * @return A map containing a message and the deleted employee.
     */
    @DeleteMapping(DELETE_BY_ID + "{id}")
    public Map<String, Employee> deleteEmployeeById(@PathVariable String id) {
        return employeeService.deleteEmployeeById(id);
    }

}
