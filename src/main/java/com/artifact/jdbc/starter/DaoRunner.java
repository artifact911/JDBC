package com.artifact.jdbc.starter;

import com.artifact.jdbc.starter.dao.TicketDao;
import com.artifact.jdbc.starter.dto.TicketFilter;
import com.artifact.jdbc.starter.entity.Ticket;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class DaoRunner {

    private static final TicketDao ticketDao = TicketDao.getInstance();

    public static void main(String[] args) {
        var ticket = ticketDao.findById(5L);
        System.out.println(ticket);

    }

    private static void filterTest() {
        var ticketFilter = new TicketFilter(3, 0, "Евгений Кудрявцев", "A1");
        var tickets = ticketDao.findAll(ticketFilter);
        System.out.println(tickets);
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
//                           .flight(3L)
                           .seatNo("B3")
                           .cost(BigDecimal.TEN)
                           .build();

        return ticketDao.save(ticket);
    }
}
