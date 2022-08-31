package com.artifact.jdbc.starter.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Ticket {

    private Long id;
    private String passengerNo;
    private String passengerName;
    private Long flightId;
    private String seatNo;
    // типа double но не округляет
    private BigDecimal cost;
}
