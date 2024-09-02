package com.mrkuhne.websocket_service.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class SimpleMessage {
    
    private String body;
    private String recipient;
    private BigDecimal value;

}
