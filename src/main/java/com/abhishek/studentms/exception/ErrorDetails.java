package com.abhishek.studentms.exception;

import java.time.LocalDateTime;

public record ErrorDetails(LocalDateTime timestamp, int status, String message, String path) {
}
