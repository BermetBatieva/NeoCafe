package com.example.backend.service;

import com.example.backend.Enums.Status;
import com.example.backend.Enums.TableStatus;
import com.example.backend.dto.DeletedDTO;
import com.example.backend.dto.TableDTO;
import com.example.backend.dto.TableDtoForAdmin;
import com.example.backend.dto.TablesDtoForWaiter;
import com.example.backend.entity.*;
import com.example.backend.exception.AlreadyExistsException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.TableNotFoundException;
import com.example.backend.repository.BranchesRepository;
import com.example.backend.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TableService {

    @Autowired
    private TableRepository tableRepository;
    
    @Autowired
    private BranchesRepository branchesRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public List<TableDtoForAdmin> getAll(){
        List<Tables> tablesList = tableRepository.findAllByStatus(Status.ACTIVATE);
        List<TableDtoForAdmin> result = new ArrayList<>();
        for (Tables t: tablesList) {
            TableDtoForAdmin tableDtoForAdmin = new TableDtoForAdmin();
            tableDtoForAdmin.setId(t.getId());
            tableDtoForAdmin.setNameBranch(t.getBranches().getName());
            if (t.getUser()!=null){
                tableDtoForAdmin.setNameEmployee(t.getUser().getFirstName());
            }
            result.add(tableDtoForAdmin);
        }
        return result;
    }

    public TableDTO add(TableDTO tableDTO) throws AlreadyExistsException {
        if (!tableRepository.existsByQrCode(tableDTO.getQrCode())) {
            Tables table = new Tables();
            table.setTableStatus(TableStatus.FREE);
            table.setQrCode(tableDTO.getQrCode());
            table.setStatus(Status.ACTIVATE);
            table.setBranches(branchesRepository.findById(tableDTO.getBranchId()).orElseThrow(
                    () -> new ResourceNotFoundException("Филиал с такой id не существует! id = ", tableDTO.getBranchId())
            ));
            tableRepository.save(table);
            return tableDTO;
        }
        else
            throw new AlreadyExistsException("такой qr_code уже существует!");
    }

    public List<TablesDtoForWaiter> allTablesByUser(){
        List<Tables> list = tableRepository.findAllByUser(userDetailsService.getCurrentUser());
        List<TablesDtoForWaiter> resultList = new ArrayList<>();

        for(Tables tables: list){
            TablesDtoForWaiter model = new TablesDtoForWaiter();
            model.setTableId(tables.getId());
            model.setStatus(tables.getTableStatus());
            resultList.add(model);
        }
        return resultList;
    }

    public DeletedDTO delete(Long id){
        Tables tables = tableRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("Стол с такой id не найден! id = ",id)
        );
        tables.setStatus(Status.DELETED);
        tables.setQrCode("delete qr_code: "+ tables.getQrCode());
        return new DeletedDTO(id);
    }

    public Tables reserve(Long id) throws Exception{
        return tableRepository.findById(id)
                .map(reserveTable -> {
                    reserveTable.setTableStatus(TableStatus.OCCUPIED);
                    return tableRepository.save(reserveTable);
                }).orElseThrow( Exception::new);
    }

    public Tables freeUpTable(Long id) throws Exception{
        return tableRepository.findById(id)
                .map(freeUp -> {
                    freeUp.setTableStatus(TableStatus.FREE);
                    return tableRepository.save(freeUp);
                }).orElseThrow( Exception :: new);
    }

    public boolean isFree(Long tableId) throws Exception {
        Optional<Tables> table = tableRepository.findById(tableId);
        if(table.isPresent()){
            TableStatus status = tableRepository.findById(tableId).get().getTableStatus();
            return status == TableStatus.FREE;
        }else {
            throw new TableNotFoundException();
        }
    }

}
