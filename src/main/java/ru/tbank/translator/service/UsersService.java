package ru.tbank.translator.service;

public interface UsersService {
    /**
     * Add new user to database
     *
     * @param userIp - user to be added
     * @return user ip
     */
    String addUser(String userIp);

    /**
     * Checks whether user in database or not
     *
     * @param userIp - user to be checked
     * @return true, if user in database, otherwise false
     */
    boolean isExists(String userIp);
}
