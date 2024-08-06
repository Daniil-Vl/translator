package ru.tbank.translator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.tbank.translator.dao.repository.UserRepository;
import ru.tbank.translator.service.UsersService;

@Service
@RequiredArgsConstructor
@Log4j2
public class UsersServiceImpl implements UsersService {

    private final UserRepository userRepository;

    @Override
    public String addUser(String userIp) {
        if (!userRepository.isExists(userIp)) {
            log.info("New user with ip - {}", userIp);
            userRepository.addUser(userIp);
        }

        return userIp;
    }

    @Override
    public boolean isExists(String userIp) {
        return userRepository.isExists(userIp);
    }
}
