package ru.tbank.translator.dao.repository;

public interface UserRepository {
    /**
     * Creates new user
     *
     * @param userIp - user's ip
     */
    void addUser(String userIp);

    /**
     * Checks, that user exists in database
     *
     * @param userIp - user's ip to check
     * @return true, if user exists otherwise false
     */
    boolean isExists(String userIp);
}
