package com.yar.microservices.microservice_a.controller.rest;


import com.yar.microservices.microservice_a.bundle.MessageCodes;
import com.yar.microservices.microservice_a.bundle.MessageService;
import com.yar.microservices.microservice_a.dto.GeneralResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api")
public class MicroserviceResource {

    private final MessageService messageService;



    public MicroserviceResource(MessageService messageService) {
        this.messageService = messageService;
    }



    @GetMapping("/process")
    public ResponseEntity<?> process() {

        return ResponseEntity.ok(new GeneralResponse(MessageCodes.PROCESS_SUCCESSFUL,messageService.getMessage(MessageCodes.PROCESS_SUCCESSFUL)));
    }

}
