package com.example.SmsValidator.service;

import com.example.SmsValidator.bean.user.UserInfoResponse;
import com.example.SmsValidator.bean.user.UserTaskHistoryResponse;
import com.example.SmsValidator.dto.task.TaskForUserDto;
import com.example.SmsValidator.entity.User;
import com.example.SmsValidator.exception.customexceptions.user.UserNotFoundException;
import com.example.SmsValidator.repository.TaskEntityRepository;
import com.example.SmsValidator.repository.UserRepository;
import com.example.SmsValidator.specification.TaskSpecification;
import com.example.SmsValidator.utils.TaskMappingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TaskEntityRepository taskEntityRepository;

    private User getUserFromContext() throws UserNotFoundException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found", this.getClass()));
    }

    public UserInfoResponse getUserInfo()
            throws UserNotFoundException {
        User user = getUserFromContext();
        return new UserInfoResponse(
                user.getUsername(),
                user.getBalance()
        );
    }

    public UserTaskHistoryResponse getTaskHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskForUserDto> tasks = taskEntityRepository.
                findAll(TaskSpecification.hasUser_Email(email)
                        .and(TaskSpecification.hasReady(true))
                        .and(TaskSpecification.withModemEntity()))
                .stream()
                .map(entity -> TaskMappingUtils.mapToTaskForUserDto(entity, entity.getModemEntity().getPhoneNumber()))
                .toList();
        return new UserTaskHistoryResponse(tasks);
    }

    public UserTaskHistoryResponse getActiveTaskHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TaskForUserDto> tasks = taskEntityRepository.
                findAll(TaskSpecification.hasUser_Email(username)
                        .and(TaskSpecification.hasReady(true))
                        .and(TaskSpecification.hasDone(false))
                        .and(TaskSpecification.withModemEntity()))
                .stream()
                .map(entity -> TaskMappingUtils.mapToTaskForUserDto(entity, entity.getModemEntity().getPhoneNumber()))
                .toList();
        return new UserTaskHistoryResponse(tasks);
    }

    public User loadByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
