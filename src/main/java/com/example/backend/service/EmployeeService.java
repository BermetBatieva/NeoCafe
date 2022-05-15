package com.example.backend.service;

import com.example.backend.Enums.ERole;
import com.example.backend.Enums.Status;
import com.example.backend.dto.*;
import com.example.backend.entity.*;
import com.example.backend.exception.AlreadyExistsException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.BranchesRepository;
import com.example.backend.repository.TableRepository;
import com.example.backend.repository.TimeTableRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchesRepository branchesRepository;

    @Autowired
    private TimeTableRepository timeTableRepository;

    @Autowired
    private SmsService smsService;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public EmployeeDto registrationEmployee(EmployeeDto employeeDto) throws AlreadyExistsException {
        if (userDetailsService.existsByPhoneNumber(employeeDto.getPhoneNumber())) {
            throw new AlreadyExistsException("такой номер уже существует!");
        } else {
            return saver(employeeDto, ERole.getRole(employeeDto.getRoleId()));
        }
    }

    private EmployeeDto saver(EmployeeDto dto, ERole role) {
        TimeTable timeTable = new TimeTable();
        timeTable.setMonday(dto.getTimeTable().getMonday());
        timeTable.setTuesday(dto.getTimeTable().getTuesday());
        timeTable.setWednesday(dto.getTimeTable().getWednesday());
        timeTable.setThursday(dto.getTimeTable().getThursday());
        timeTable.setFriday(dto.getTimeTable().getFriday());
        timeTable.setSaturday(dto.getTimeTable().getSaturday());
        timeTable.setSunday(dto.getTimeTable().getSunday());
        timeTableRepository.save(timeTable);
        User user = new User();
        user.setTime(timeTable);
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(role);
        user.setStatus(Status.ACTIVATE);
        user.setBranches(branchesRepository.findById(dto.getBranchId()).orElseThrow(
                () -> new ResourceNotFoundException("Филиал с такой id не существует! id = ", dto.getBranchId())));
        user.setActive(true);
        user.setCompleted(false);
        user.setRating(0L);
        userRepository.save(user);
        dto.setId(userRepository.findByPhoneNumber(user.getPhoneNumber()).get().getId());
        if (dto.getTableId().size()!=0){
            addWaiterTables(new WaiterTableDto(dto.getTableId(),dto.getPhoneNumber()));
        }
        return dto;
    }

    @Transactional
    public EmployeeForAdmin updateEmployee(EmployeeUpdateDTO employeeDto){
        User user = userRepository.findById(employeeDto.getId()).orElseThrow(
                ()-> new ResourceNotFoundException("Сотрудник с такой id не существует! id = ", employeeDto.getId())
        );
        if (null != user.getFirstName()){
            user.setFirstName(employeeDto.getName());
        }
        user.setRole(ERole.getRole(employeeDto.getRoleId()));
        user.setBDate(employeeDto.getBDate());
        user.setPhoneNumber(employeeDto.getPhoneNumber());
        user.setBranches(branchesRepository.findById(employeeDto.getBranchId()).orElseThrow(
                ()->new ResourceNotFoundException("Филиал с такой id не существует! id = ",employeeDto.getBranchId())
        ));

        TimeTable timeTable = new TimeTable();
        timeTable.setMonday(employeeDto.getTimeTable().getMonday());
        timeTable.setTuesday(employeeDto.getTimeTable().getTuesday());
        timeTable.setWednesday(employeeDto.getTimeTable().getWednesday());
        timeTable.setThursday(employeeDto.getTimeTable().getThursday());
        timeTable.setFriday(employeeDto.getTimeTable().getFriday());
        timeTable.setSaturday(employeeDto.getTimeTable().getSaturday());
        timeTable.setSunday(employeeDto.getTimeTable().getSunday());
        timeTableRepository.save(timeTable);
        user.setTime(timeTable);

        if (employeeDto.getTablesId()!=null){
            addWaiterTables(new WaiterTableDto(employeeDto.getTablesId(), user.getPhoneNumber()));
        }

        userRepository.save(user);
        addWaiterTables(new WaiterTableDto(employeeDto.getTablesId(),employeeDto.getPhoneNumber()));
        User userForEmployee = userRepository.getById(employeeDto.getId());

        EmployeeForAdmin employee = new EmployeeForAdmin();
        employee.setId(userForEmployee.getId());
        employee.setName(userForEmployee.getFirstName());
        employee.setRole(userForEmployee.getRole().name);
        employee.setPhoneNumber(userForEmployee.getPhoneNumber());
        employee.setBranch(userForEmployee.getBranches().getName());
        employee.setTimeTable(userForEmployee.getTime());
        employee.setTablesId(employeeDto.getTablesId());
        return employee;
    }

    @Transactional
    public void
    addWaiterTables(WaiterTableDto waiterTableDto) {
        User user = userRepository.findByPhoneNumber(waiterTableDto.getWaiterPhoneNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Официант с таким номером не существует!"));
        List<Tables> tablesList = tableRepository.findAllById(waiterTableDto.getTablesId());
        tablesList.forEach(t -> t.setUser(user));
        tableRepository.saveAll(tablesList);
    }

    public DeletedDTO deleteEmployee(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("сотрудник с такой id не существует! id = ", id)
        );
        user.setPhoneNumber("delete number: "+ user.getPhoneNumber());
        user.setStatus(Status.DELETED);
        userRepository.save(user);
        return new DeletedDTO(user.getId());
    }

    public List<EmployeeForAdmin> getAllEmployee(Long id) {
        List<User> userListBarista = userRepository.findAllByStatusAndBranchesAndRole(Status.ACTIVATE,
                branchesRepository.findById(id).orElseThrow(
                        ()-> new ResourceNotFoundException("филиал с такой id не существует! id = ", id)
                ),ERole.ROLE_BARISTA);

        List<User> userListWaiter = userRepository.findAllByStatusAndBranchesAndRole(Status.ACTIVATE,
                branchesRepository.findById(id).orElseThrow(
                        ()-> new ResourceNotFoundException("филиал с такой id не существует! id = ", id)
                ),ERole.ROLE_WAITER);
        List<User> employeesList = new ArrayList<>();

        employeesList.addAll(userListBarista);
        employeesList.addAll(userListWaiter);

        List<EmployeeForAdmin> employeeList = new ArrayList<>();

        for (User user: employeesList) {
            EmployeeForAdmin employee = new EmployeeForAdmin();
            employee.setId(user.getId());
            employee.setName(user.getFirstName());
            employee.setRole(user.getRole().name);
            employee.setPhoneNumber(user.getPhoneNumber());
            employee.setBranch(user.getBranches().getName());
            employee.setTimeTable(user.getTime());

            List<Tables> tablesList = tableRepository.findAllByUser(user);
            List<Long> tablesId = new ArrayList<>();
            for (Tables t: tablesList) {
                tablesId.add(t.getId());
            }
            employee.setTablesId(tablesId);
            employeeList.add(employee);
        }
        return employeeList;
    }

}