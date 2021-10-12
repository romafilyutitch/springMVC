package com.epam.esm.dao;

/**
 * DAO layer exception class to define exceptions that occur
 * wit DAO layer operations
 */
public class DaoException extends RuntimeException {
    public DaoException(Throwable cause) {
        super(cause);
    }

    public DaoException() {
    }
}
