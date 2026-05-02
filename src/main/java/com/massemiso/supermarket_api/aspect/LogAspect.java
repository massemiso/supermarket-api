package com.massemiso.supermarket_api.aspect;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class LogAspect {

  @Pointcut("execution(* com.massemiso.supermarket_api.service.*.*(..))")
  public void serviceLayerMethods(){}

  @Pointcut("execution(* com.massemiso.supermarket_api.controller.*.*(..))")
  public void controllerLayerMethods(){}

  @Around("serviceLayerMethods()") // Uses the pointcut defined above
  public Object logServiceActivity(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().getName();
    Object[] args = joinPoint.getArgs();

    log.info("SERVICE: Attempting method [{}] with arguments: {}",
        methodName, Arrays.toString(args));

    try{
      Object result = joinPoint.proceed();
      chooseAndLogServiceMethod(methodName, result);
      return result;
    } catch (Throwable e) {
      // Log that the service failed, but THROW it so GlobalHandler can catch it
      log.error("SERVICE: [{}] failed with error: {}", methodName, e.getMessage());
      throw e;
    }
  }

  private void chooseAndLogServiceMethod(String methodName, Object result) {
    // Custom logic for different method types
    if (methodName.startsWith("getAll")) {
      // If it's a Page, just log the size
      if (result instanceof Page<?> page) {
        log.info("SERVICE: Method [{}] returned a page with {} elements",
            methodName, page.getNumberOfElements());
      }
    } else if (methodName.startsWith("getById")) {
      log.info("SERVICE: Method [{}] executed successfully", methodName);
      // We don't log 'result' here to keep logs clean and secure
    } else {
      // For Create/Update/Delete, we DO want to see the result
      log.info("SERVICE: Method [{}] completed successfully. Data: {}",
          methodName, result);
    }
  }

  @Around("controllerLayerMethods()")
  public Object logControllerActivity(ProceedingJoinPoint joinPoint) throws Throwable {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
        .currentRequestAttributes()).getRequest();

    String httpMethod = request.getMethod();
    String requestUri = request.getRequestURI();
    String methodName = joinPoint.getSignature().getName();

    log.info("API-REQUEST: {} {} | Controller Method: {}",
        httpMethod, requestUri, methodName);

    long start = System.currentTimeMillis();
    long duration;
    try{
      Object result = joinPoint.proceed();
      duration = System.currentTimeMillis() - start;

      log.info("API-RESPONSE: {} {} | Time: {}ms",
          httpMethod, requestUri, duration);
      return result;
    } catch(Throwable e){
      duration = System.currentTimeMillis() - start;
      log.warn("API-FAILED: {} {} | Time: {}ms | Error: {}", httpMethod, requestUri, duration, e.getMessage());
      throw e;
    }
  }

}
