package com.example.backend.controller;

import com.example.backend.dto.NotificationsDTO;
import com.example.backend.service.NotificationsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/notifications")
public class NotificationsController {

    @Autowired
    private NotificationsService notificationsService;

    @ApiOperation("Получение всех уведомлений текущего пользователя")
    @GetMapping
    public List<NotificationsDTO> findAllByCurrentUser(){
        return notificationsService.findAllByCurrentUser();
    }

    @ApiOperation("Удаление уведомления по id")
    @DeleteMapping("/notifications/delete/{notificationId}")
    public void deleteNotificationsById(@PathVariable Long notificationId){
        notificationsService.delete(notificationId);
    }

    @ApiOperation("Удаление всех уведомлений пользовтеля")
    @DeleteMapping("/notifications/deleteAll")
    public void deleteAllNotifications(){
        notificationsService.deleteAll();
    }
}
