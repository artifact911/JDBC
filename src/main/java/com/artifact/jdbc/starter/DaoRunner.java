package com.artifact.jdbc.starter;

import com.artifact.jdbc.starter.dao.TicketDao;
import com.artifact.jdbc.starter.entity.Ticket;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class DaoRunner {

    private static final TicketDao ticketDao = TicketDao.getInstance();

    public static void main(String[] args) {
        System.out.println(findAllTest());
    }

    private static List<Ticket> findAllTest() {
        return ticketDao.findAll();
    }

    private static Optional<Ticket> updateTest() {
        var maybeTicket = ticketDao.findById(2L);

        maybeTicket.ifPresent(ticket -> {
            ticket.setCost(BigDecimal.valueOf(188.88));
            ticketDao.update(ticket);
        });
        return maybeTicket;
    }

    private static boolean deleteTest(Ticket savedTicket) {
        return ticketDao.delete(savedTicket.getId());
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
