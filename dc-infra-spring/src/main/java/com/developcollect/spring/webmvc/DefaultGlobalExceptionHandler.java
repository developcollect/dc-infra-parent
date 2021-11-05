package com.developcollect.spring.webmvc;

import com.developcollect.core.exception.DcException;
import com.developcollect.core.exception.DcRuntimeException;
import com.developcollect.core.exception.IExceptionInfo;
import com.developcollect.core.web.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.env.Environment;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class DefaultGlobalExceptionHandler implements EnvironmentAware {


    private Set<String> profiles;

    @ExceptionHandler(DcException.class)
    public R jcGlobalException(DcException e) {
        return processJcException(e);
    }

    @ExceptionHandler(DcRuntimeException.class)
    public R jcGlobalException(DcRuntimeException e) {
        return processJcException(e);
    }

    private R processJcException(IExceptionInfo e) {
        Throwable t = (Throwable) e;
        if (this.profiles.contains("dev") || this.profiles.contains("test")) {
            log.info("全局异常处理", t);
        } else {
            log.info("全局异常处理: {}: msg[{}]", t.getClass().getCanonicalName(), t.getMessage());
        }
        // 根据异常代码获取异常的本地化（国际化）提示消息。
//        String msg = MessageResolver.getMessage(String.valueOf(e.getCode()), e.getMessage());
        return R.fail(e.getCode(), t.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = bindExceptionMessage(bindingResult, e.getMessage());

        log.info("全局异常处理: {}", message);
        return R.fail(R.COMMON_CLIENT_FAIL_CODE, message);
    }

    @ExceptionHandler(BindException.class)
    public R bindExceptionHandler(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = bindExceptionMessage(bindingResult, e.getMessage());
        log.info("全局异常处理: {}", message);
        return R.fail(R.COMMON_CLIENT_FAIL_CODE, message);
    }


    private String bindExceptionMessage(BindingResult bindingResult, String exceptionMsg) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        String message;
        if (allErrors.isEmpty()) {
            message = exceptionMsg;
        } else {
            StringBuilder sb = new StringBuilder();
            for (ObjectError error : allErrors) {
                Object[] arguments = error.getArguments();
                if (arguments == null) {
                    continue;
                }
                for (Object argument : arguments) {
                    if (argument instanceof MessageSourceResolvable) {
                        sb.append(((MessageSourceResolvable) argument).getDefaultMessage()).append(": ");
                        break;
                    }
                }
                sb.append(error.getDefaultMessage()).append("; ");
            }
            if (sb.length() > 0) {
                sb.delete(sb.length() - 2, sb.length());
            }

            message = sb.toString();
        }

        return message;
    }


    @ExceptionHandler(ValidationException.class)
    public R validationExceptionHandler(ValidationException e) {
        String message = e.getMessage();
        return R.fail(R.COMMON_CLIENT_FAIL_CODE, message);
    }


    @ExceptionHandler(ServletRequestBindingException.class)
    public R ServletRequestBindingExceptionHandler(ValidationException e) {
        String message = e.getMessage();
        return R.fail(R.COMMON_CLIENT_FAIL_CODE, message);
    }


    @ExceptionHandler(Throwable.class)
    public R throwable(Throwable e) {
        if (this.profiles.contains("dev") || this.profiles.contains("test")) {
            log.info("全局异常处理", e);
        } else {
            log.info("全局异常处理: {}: msg[{}]", e.getClass().getCanonicalName(), e.getMessage());
        }
        return R.failMsg(e.getMessage());
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.profiles = new HashSet<>(Arrays.asList(environment.getActiveProfiles()));
    }
}
