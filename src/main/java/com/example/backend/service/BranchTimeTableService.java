package com.example.backend.service;

import com.example.backend.Enums.Status;
import com.example.backend.dto.BranchWorkingTimeDto;
import com.example.backend.entity.Branches;
import com.example.backend.repository.BranchTimeTableRepository;
import com.example.backend.repository.BranchesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class BranchTimeTableService {

    @Autowired
    private BranchesRepository branchesRepository;

    private Calendar cal = Calendar.getInstance();

    public List<BranchWorkingTimeDto> getTodayInfo(){
        int d = cal.get(Calendar.DAY_OF_WEEK);
        List<Branches> branches = branchesRepository.findAllByStatus(Status.ACTIVATE);
        List<BranchWorkingTimeDto> res = new ArrayList<>();
        for (Branches branch : branches) {
            BranchWorkingTimeDto dto = new BranchWorkingTimeDto();
            dto.setBranchId(branch.getId());
            dto.setName(branch.getName());
            dto.setAddress(branch.getAddress());
            dto.setPhoneNumber(branch.getPhoneNumber());

            switch (d){
                case 1: dto.setWorkingTime(branch.getWorkingTime().getSunday()); break;
                case 2: dto.setWorkingTime(branch.getWorkingTime().getMonday()); break;
                case 3: dto.setWorkingTime(branch.getWorkingTime().getTuesday()); break;
                case 4: dto.setWorkingTime(branch.getWorkingTime().getWednesday()); break;
                case 5: dto.setWorkingTime(branch.getWorkingTime().getThursday()); break;
                case 6: dto.setWorkingTime(branch.getWorkingTime().getFriday()); break;
                case 7: dto.setWorkingTime(branch.getWorkingTime().getSaturday()); break;
            }
            res.add(dto);
        }

        return res;
    }
}
