package com.training.employee.dao;

import com.training.employee.entity.Employee;

import java.util.List;
import java.util.Map;


/**
 * This interface defines the contract for accessing and managing employee data.
 */
public interface EmployeeDAO {
    /**
     * Retrieves a list of all employees.
     *
     * @return A list containing all employees.
     */
    List<Employee> getAllEmployees();

    /**
     * Adds an employee to the list.
     *
     * @param employee The employee object to add.
     * @return A list containing all employees, including the newly added one.
     */
    List<Employee> addEmployeeToList(Employee employee);

    /**
     * Retrieves an employee by their ID.
     *
     * @param id The ID of the employee to retrieve.
     * @return The employee with the specified ID, or null if not found.
     */
    Employee getEmployeeById(String id);

    /**
     * Deletes an employee by their ID.
     *
     * @param id The ID of the employee to delete.
     * @return A map containing a message and the deleted employee, or an empty map if not found.
     */
    Map<String, Employee> deleteEmployeeById(String id);

}
