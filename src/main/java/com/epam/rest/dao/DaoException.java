package com.epam.rest.dao;

import java.sql.SQLException;

public class DaoException extends RuntimeException {
    public DaoException(Throwable cause) {
        super(cause);
    }
}
