package com.example.Eshop.controllers;

import com.example.Eshop.dtos.LogMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/remoteLogging")
public class RemoteLoggingController {
  private final Logger logger
      = LoggerFactory.getLogger(RemoteLoggingController.class);
  @PostMapping
  public ResponseEntity<?> postLog(@RequestBody LogMessageDTO logMessage) {
    switch(logMessage.getLevel()){
      case "Info":
        logger.info(logMessage.getMessage());
        break;
      case "Warn":
        logger.warn(logMessage.getMessage());
        break;
      case "Error":
        logger.error(logMessage.getMessage()
            + " - Stack trace: " + logMessage.getStackTrace());
        break;
      default:
        logger.info(logMessage.getMessage());
        break;
    }

    return ResponseEntity.ok().build();
  }
}
