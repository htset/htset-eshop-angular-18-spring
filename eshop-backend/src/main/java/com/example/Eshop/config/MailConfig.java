package com.example.Eshop.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class MailConfig {
  @Value("${mail.smtp.host}")
  private String smtpHost;

  @Value("${mail.smtp.port}")
  private int smtpPort;

  @Value("${mail.smtp.username}")
  private String smtpUsername;

  @Value("${mail.smtp.password}")
  private String smtpPassword;

  @Value("${mail.smtp.auth}")
  private boolean smtpAuth;

  @Value("${mail.smtp.starttls.enable}")
  private boolean starttlsEnable;

  @Value("${mail.smtp.ssl.trust}")
  private String sslTrust;
}
