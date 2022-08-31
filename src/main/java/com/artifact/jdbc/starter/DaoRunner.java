package com.artifact.jdbc.starter;

import com.artifact.jdbc.starter.dao.TicketDao;
import com.artifact.jdbc.starter.entity.Ticket;

import java.math.BigDecimal;

public class DaoRunner {

   private static final TicketDao ticketDao = TicketDao.getInstance();

    public static void main(String[] args) {
        Ticket savedTicket = saveTest();
        System.out.println(savedTicket);
        var result = ticketDao.delete(savedTicket.getId());
        System.out.println(result);
    }

    private static Ticket saveTest() {

        var ticket = Ticket.builder()
                           .passengerNo("1234567")
                           .passengerName("Test")
                           .flightId(3L)
                           .seatNo("B3")
                           .cost(BigDecimal.TEN)
                           .build();

        return ticketDao.save(ticket);
    }
}
