package tw.edu.ntub.imd.birc.practice.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import tw.edu.ntub.birc.common.exception.ProjectException;
import tw.edu.ntub.birc.common.exception.UnknownException;
import tw.edu.ntub.imd.birc.practice.exception.NotFoundException;
import tw.edu.ntub.birc.common.exception.date.ParseDateException;
import tw.edu.ntub.birc.common.util.ClassUtils;
import tw.edu.ntub.imd.birc.practice.exception.ConvertPropertyException;
import tw.edu.ntub.imd.birc.practice.exception.MethodNotSupportedException;
import tw.edu.ntub.imd.birc.practice.exception.NullRequestBodyException;
import tw.edu.ntub.imd.birc.practice.exception.RequiredParameterException;
import tw.edu.ntub.imd.birc.practice.exception.file.FileNotExistException;
import tw.edu.ntub.imd.birc.practice.exception.file.UploadFileTooLargeException;
import tw.edu.ntub.imd.birc.practice.exception.form.InvalidFormDateFormatException;
import tw.edu.ntub.imd.birc.practice.exception.form.InvalidFormException;
import tw.edu.ntub.imd.birc.practice.exception.form.InvalidFormNumberFormatException;
import tw.edu.ntub.imd.birc.practice.exception.form.InvalidRequestFormatException;
import tw.edu.ntub.imd.birc.practice.util.http.ResponseEntityBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

@Log4j2
@ControllerAdvice
public class ExceptionHandleController {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return ResponseEntityBuilder.error(e)
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("表單驗證失敗");
        return ResponseEntityBuilder.error(new InvalidFormException(message))
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<String> handleProjectException(ProjectException e) {
        return ResponseEntityBuilder.error(e)
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidFormatException(HttpMessageNotReadableException e) {
        if (e.getCause() instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) e.getCause();
            List<JsonMappingException.Reference> referenceList = invalidFormatException.getPath();
            String message = "";
            if (referenceList.size() > 0) {
                JsonMappingException.Reference reference = referenceList.get(0);
                Object from = reference.getFrom();
                String fieldName = reference.getFieldName();
                Class<?> fromClass = from.getClass();
                Field declaredField = null;
                while (declaredField == null) {
                    try {
                        declaredField = fromClass.getDeclaredField(fieldName);
                    } catch (NoSuchFieldException ignored) {
                        fromClass = fromClass.getSuperclass();
                    }
                }
                String description = declaredField.getName();
                if (ClassUtils.isCanCast(invalidFormatException.getTargetType(), Number.class)) {
                    message = description + " - \"" + invalidFormatException.getValue() + "\"輸入的文字中包含非數字文字";
                } else {
                    throw new UnknownException(e);
                }
            }
            return ResponseEntityBuilder.error(new InvalidRequestFormatException(message))
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        } else if (e.getRootCause() instanceof ParseDateException) {
            ParseDateException rootCause = (ParseDateException) e.getRootCause();
            return ResponseEntityBuilder.error(new InvalidFormDateFormatException(rootCause))
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        } else if (e.getRootCause() instanceof NumberFormatException) {
            NumberFormatException rootCause = (NumberFormatException) e.getRootCause();
            return ResponseEntityBuilder.error(new InvalidFormNumberFormatException(rootCause))
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        } else {
            return ResponseEntityBuilder.error(new NullRequestBodyException(e))
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(FileNotExistException.class)
    public void handleFileNotExistException(FileNotExistException e) {
        log.error("找不到檔案", e);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntityBuilder.error()
                .status(HttpStatus.FORBIDDEN)
                .errorCode("User - AccessDenied")
                .message("您並無此操作之權限，請嘗試重新登入")
                .build();
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return ResponseEntityBuilder.error(new UploadFileTooLargeException(e))
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleHttpRequestMethodNotSupportedException(
            HttpServletRequest request,
            HttpRequestMethodNotSupportedException e
    ) {
        return ResponseEntityBuilder.error(new MethodNotSupportedException(
                request.getRequestURL().toString(),
                request.getMethod(),
                e
        )).status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @ExceptionHandler(InvalidPropertyException.class)
    public ResponseEntity<String> handleInvalidPropertyException(InvalidPropertyException e) {
        return ResponseEntityBuilder.error(new ConvertPropertyException(e))
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        ConstraintViolation<?> constraintViolation = constraintViolations.stream().findAny().orElseThrow();
        return ResponseEntityBuilder.error(new InvalidFormException(constraintViolation.getMessage()))
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResponseEntityBuilder.error(new RequiredParameterException(e.getParameterName()))
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnknownException(Exception e) {
        return ResponseEntityBuilder.error(new UnknownException(e))
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}
