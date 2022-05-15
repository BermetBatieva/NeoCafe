package com.example.backend.service;

import com.example.backend.dto.NotificationsDTO;
import com.example.backend.entity.Notifications;
import com.example.backend.entity.User;
import com.example.backend.repository.NotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationsService {

    @Autowired
    private NotificationsRepository notificationsRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public Notifications create(Notifications notification) {

        return notificationsRepository.save(notification);
    }

    public List<NotificationsDTO> findAllByCurrentUser() {

        User user = userDetailsService.getCurrentUser();
        List<NotificationsDTO> notificationsDTOList = new ArrayList<>();
        for (Notifications n : notificationsRepository.findAllByUser(user)) {
            NotificationsDTO notificationsDTO = new NotificationsDTO();
            notificationsDTO.setId(n.getId());
            notificationsDTO.setMessage(n.getMessage());
            notificationsDTO.setTitle(n.getTitle());
            notificationsDTO.setTime(n.getTime());
            notificationsDTOList.add(notificationsDTO);
        }
        return notificationsDTOList;
    }

    public void delete(Long id) {

        notificationsRepository.deleteById(id);

    }

    public void deleteAll(){
        notificationsRepository.deleteAll(notificationsRepository.findAllByUser(userDetailsService.getCurrentUser()));
    }

}
