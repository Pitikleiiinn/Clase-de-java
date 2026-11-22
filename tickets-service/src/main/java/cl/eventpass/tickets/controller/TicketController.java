package cl.eventpass.tickets.controller;

import cl.eventpass.tickets.dto.TicketResponse;
import cl.eventpass.tickets.service.TicketService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService servicio;

    public TicketController(TicketService servicio) {
        this.servicio = servicio;
    }

    /** COMPRADOR: consulta el codigo de acceso de sus tickets emitidos. */
    @GetMapping("/mis-tickets")
    public List<TicketResponse> misTickets(@AuthenticationPrincipal Jwt jwt) {
        return servicio.listarDe(jwt.getSubject());
    }
}
