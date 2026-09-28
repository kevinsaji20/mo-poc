import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class Employee {
    public String name;
    public Integer salary;
    public String department;

    public Employee(String name, Integer salary, String department) {
        this.name = name;
        this.salary = salary;
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public Integer getSalary() {
        return salary;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public String toString() {
        return "name: " + name + " salary: " + salary;
    }
}

public class SortEmployeeBySalary {
    public static void main(String[] args) {
        List<Employee> emp = new ArrayList<>();

        emp.add(new Employee("A",130, "IT"));
        emp.add(new Employee("B",80, "IT"));
        emp.add(new Employee("C",200, "HR"));
        emp.add(new Employee("D",30, "HR"));
        emp.add(new Employee("E",150, "IT"));
        emp.add(new Employee("F",120, "IT"));
        emp.add(new Employee("G",160, "HR"));
        emp.add(new Employee("H",180, "HR"));
        emp.add(new Employee("I",120, "IT"));


        List<Employee> result = emp.stream()
                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                .toList();

        Employee employee = emp.stream()
                .max(Comparator.comparing(Employee::getSalary))
                .orElse(null);

        Map<String, Optional<Employee>> depRes = emp.stream()
                        .collect(Collectors.groupingBy(
                                Employee::getDepartment,
                                Collectors.maxBy(
                                        Comparator.comparing(Employee::getSalary)
                                )
                        ));

        Map<String, List<String>> grpRes = emp.stream()
                        .collect(Collectors.groupingBy(
                                Employee::getDepartment,
                                Collectors.mapping(Employee::getName, Collectors.toList())
                        ));

        Map<String, Long> grpEmpCount = emp.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.counting()
                ));

        Map<String, Double> avgSlrDept = emp.stream()
                        .collect(Collectors.groupingBy(
                                Employee::getDepartment,
                                Collectors.averagingInt(Employee::getSalary)
                        ));

        Map<String, List<String>> avgGtDept = emp.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.filtering(
                                employee1 -> employee1.getSalary() > avgSlrDept.get(employee1.getDepartment()),
                                Collectors.mapping(Employee::getName, Collectors.toList())
                        )
                ));

        Map<String, List<Employee>> top3High = emp.stream()
                        .collect(Collectors.groupingBy(
                                Employee::getDepartment,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list ->
                                                list.stream()
                                                        .sorted(Comparator.comparing(Employee::getSalary).reversed())
                                                        .limit(3)
                                                        .toList()
                                )
                        ));
        Map<String, Integer> secondHig = emp.stream()
                        .collect(Collectors.groupingBy(
                                Employee::getDepartment,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream()
                                                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                                                .skip(1)
                                                .map(Employee::getSalary)
                                                .findFirst()
                                                .orElse(null)
                                )
                        ));



        System.out.println(result);
        System.out.println(employee);
        System.out.println(depRes);
        System.out.println(grpRes);
        System.out.println(grpEmpCount);
        System.out.println(avgSlrDept);
        System.out.println(avgGtDept);
        System.out.println(top3High);
        System.out.println(secondHig);
    }
}
