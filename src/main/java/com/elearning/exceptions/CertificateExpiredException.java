package com.elearning.exceptions;

public class CertificateExpiredException extends BusinessException {
    public CertificateExpiredException(String message) {
        super(410, message);
    }
}
