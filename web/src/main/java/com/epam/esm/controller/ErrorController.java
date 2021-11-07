package com.epam.esm.controller;

import com.epam.esm.error.Error;
import com.epam.esm.error.ErrorCode;
import com.epam.esm.service.*;
import com.epam.esm.validation.InvalidCertificateException;
import com.epam.esm.validation.InvalidTagException;
import com.epam.esm.validation.InvalidUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST controller that handles all
 * occured exceptions in certificate and user controllers
 */
@ControllerAdvice
@RestController
public class ErrorController {
    private final MessageSource messageSource;

    @Autowired
    public ErrorController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Handles {@link ClassNotFoundException}
     * @param exception occuredException
     * @param locale client locale
     * @return error response
     * @throws ResourceNotFoundException if certificate is not found
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @ExceptionHandler(CertificateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error certificateNotFound(CertificateNotFoundException exception, Locale locale) throws ResourceNotFoundException, PageOutOfBoundsException {
        String message = messageSource.getMessage("certificate.notFound", new Object[]{exception.getResourceId()}, locale);
        return new Error(ErrorCode.NOT_FOUND.getCode(), message);
    }

    /**
     * Handles {@link TagNotFoundException}
     * @param exception occured exception
     * @param locale client locale
     * @return error response
     * @throws ResourceNotFoundException if certificate is not found
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @ExceptionHandler(TagNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error tagNotFound(TagNotFoundException exception, Locale locale) throws ResourceNotFoundException, PageOutOfBoundsException {
        String message = messageSource.getMessage("tag.notFound", new Object[]{exception.getResourceId()}, locale);
        return new Error(ErrorCode.NOT_FOUND.getCode(), message);
    }

    /**
     * Handles {@link OrderNotFoundException}
     * @param exception occured exception
     * @param locale client locale
     * @return error response
     * @throws ResourceNotFoundException if certificate is not found
     * @throws PageOutOfBoundsException if pga number is less then on and greater then pages amount
     */
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error orderNotFound(OrderNotFoundException exception, Locale locale) throws ResourceNotFoundException, PageOutOfBoundsException {
        String message = messageSource.getMessage("order.notFound", new Object[]{exception.getResourceId()}, locale);
        return new Error(ErrorCode.NOT_FOUND.getCode(), message);
    }

    /**
     * Handles {@link UserNotFoundException}
     * @param exception occured exception
     * @param locale client locale
     * @return error response
     * @throws ResourceNotFoundException if user is not found
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error userNotFound(UserNotFoundException exception, Locale locale) throws ResourceNotFoundException, PageOutOfBoundsException {
        String message = messageSource.getMessage("user.notFound", new Object[]{exception.getResourceId()}, locale);
        return new Error(ErrorCode.NOT_FOUND.getCode(), message);
    }

    /**
     * Handles {@link InvalidCertificateException}
     * @param exception occured exception
     * @param locale client locale
     * @return error response
     * @throws ResourceNotFoundException if certificate is not found
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @ExceptionHandler(InvalidCertificateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error invalidCertificate(InvalidCertificateException exception, Locale locale) throws ResourceNotFoundException, PageOutOfBoundsException {
        String message = messageSource.getMessage("certificate.invalid", new Object[]{}, locale);
        return new Error(ErrorCode.INVALID.getCode(), message);
    }

    /**
     * Handles {@link InvalidTagException}
     * @param exception occured exception
     * @param locale client locale
     * @return error response
     * @throws ResourceNotFoundException if certificate is not found
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @ExceptionHandler(InvalidTagException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error invalidTag(InvalidTagException exception, Locale locale) throws ResourceNotFoundException, PageOutOfBoundsException {
        String message = messageSource.getMessage("tag.invalid", new Object[]{}, locale);
        return new Error(ErrorCode.INVALID.getCode(), message);
    }

    /**
     * Handles {@link InvalidUserException}
     * @param exception occured exception
     * @param locale client locle
     * @return error response
     * @throws ResourceNotFoundException if users is not found
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @ExceptionHandler(InvalidUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error invalidUser(InvalidUserException exception, Locale locale) throws ResourceNotFoundException, PageOutOfBoundsException {
        String message = messageSource.getMessage("user.invalid", new Object[]{}, locale);
        return new Error(ErrorCode.INVALID.getCode(), message);
    }

    /**
     * Handles {@link PageOutOfBoundsException}
     * @param exception occured exception
     * @param locale client locale
     * @return error response
     * @throws ResourceNotFoundException if user of certificate is not found
     * @throws PageOutOfBoundsException if page number is less then one and greater then pages amount
     */
    @ExceptionHandler(PageOutOfBoundsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error pageOutOfBounds(PageOutOfBoundsException exception, Locale locale) throws ResourceNotFoundException, PageOutOfBoundsException {
        String message = messageSource.getMessage("page.outOfBounds", new Object[] {exception.getCurrentOffset(), exception.getTotalElements()}, locale);
        return new Error(ErrorCode.PAGE_OUT_OF_BOUNDS.getCode(), message);
    }

    /**
     * Handled {@link InvalidPageException}
     * @param exception occured exception
     * @param locale client locale
     * @return error response
     */
    @ExceptionHandler(InvalidPageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error invalidPage(InvalidPageException exception, Locale locale) {
        String message = messageSource.getMessage("page.invalid", new Object[] {exception.getOffset(), exception.getLimit()}, locale);
        return new Error(ErrorCode.INVALID.getCode(), message);
    }
}
