package com.elearning.exceptions;

public class CertificateRevokedException extends BusinessException {
    public CertificateRevokedException(String message) {
        super(400, message);
    }
}
