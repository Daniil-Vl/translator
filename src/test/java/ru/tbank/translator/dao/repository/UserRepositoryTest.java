package ru.tbank.translator.dao.repository;

import org.junit.jupiter.api.Test;
import ru.tbank.translator.DomainAbstractTest;
import ru.tbank.translator.dao.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class UserRepositoryTest extends DomainAbstractTest {

    protected UserRepository userRepository;

    protected abstract void initRepository();

    @Test
    void givenUserIp_whenRepositoryAddUser_thenUserSuccessfullyAdded() {
        String userIp = "127.0.0.2/32";
        userRepository.addUser(userIp);

        Optional<User> optionalUser = jdbcClient
                .sql("SELECT * FROM users WHERE ip = ?::cidr")
                .param(userIp)
                .query(User.class)
                .optional();

        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get().ip()).isEqualTo(userIp);
    }

    @Test
    void givenUser_whenRepositoryIsExists_thenReturnsTrue() {
        String userIp = "127.0.0.3/32";
        userRepository.addUser(userIp);

        boolean actual = userRepository.isExists(userIp);
        assertThat(actual).isTrue();
    }

    @Test
    void givenNonExistentUser_whenRepositoryIsExists_thenReturnsFalse() {
        String userIp = "127.0.0.4/32";

        boolean actual = userRepository.isExists(userIp);
        assertThat(actual).isFalse();
    }
}