public package com.yourcompany.exam;

import com.yourcompany.exam.model.SolutionRequest;
import com.yourcompany.exam.model.WebhookRequest;
import com.yourcompany.exam.model.WebhookResponse;
import com.yourcompany.exam.service.ExamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ExamApplication {

    private static final Logger log = LoggerFactory.getLogger(ExamApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ExamApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ExamService examService) {
        return args -> {
            log.info("Starting application startup process...");
            // Create the initial POST request body
            WebhookRequest webhookRequest = new WebhookRequest("John Doe", "REG12347", "john@example.com");

            // Step 1 & 2: Send POST request and handle response
            WebhookResponse response = examService.generateWebhook(webhookRequest).block();

            if (response != null) {
                log.info("Webhook URL: {}", response.getWebhookUrl());
                log.info("Access Token: {}", response.getAccessToken());

                // Step 3: Determine the question and solve
                String regNoLastTwoDigits = webhookRequest.getRegNo().substring(webhookRequest.getRegNo().length() - 2);
                int number = Integer.parseInt(regNoLastTwoDigits);
                String sqlQuery = getSqlQueryBasedOnRegNo(number); // Logic for the SQL query

                if (sqlQuery != null) {
                    SolutionRequest solutionRequest = new SolutionRequest(sqlQuery);

                    // Step 4: Submit the solution
                    examService.submitSolution(response.getWebhookUrl(), response.getAccessToken(), solutionRequest).block();
                } else {
                    log.error("Could not determine SQL question based on RegNo.");
                }
            } else {
                log.error("Failed to generate webhook. Response was null.");
            }
            log.info("Application startup process finished.");
        };
    }

    private String getSqlQueryBasedOnRegNo(int regNo) {
        // Based on your documentation, a different SQL query is required for odd and even registration numbers
        // You must add your specific SQL logic here.
        if (regNo % 2 != 0) {
            // This is for Odd number RegNo
            // TODO: Implement the SQL logic for the odd question
            return "YOUR_SQL_QUERY_FOR_ODD_REG_NO_HERE";
        } else {
            // This is for Even number RegNo
            // TODO: Implement the SQL logic for the even question
            return "YOUR_SQL_QUERY_FOR_EVEN_REG_NO_HERE";
        }
    }
} ExamApplication {
    
}
