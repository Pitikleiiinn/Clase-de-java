package cl.eventpass.tickets.service;

import cl.eventpass.tickets.dto.EmitirRequest;
import cl.eventpass.tickets.dto.TicketResponse;
import cl.eventpass.tickets.model.Ticket;
import cl.eventpass.tickets.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository tickets;
    private final CodigoGenerador generador;

    public TicketService(TicketRepository tickets, CodigoGenerador generador) {
        this.tickets = tickets;
        this.generador = generador;
    }

    /**
     * Genera los codigos de acceso de una orden (llamado por la Lambda tras reservar el aforo).
     * Idempotente: si la orden ya tiene tickets, devuelve los existentes sin crear nuevos.
     */
    @Transactional
    public List<TicketResponse> emitir(EmitirRequest req) {
        List<Ticket> existentes = tickets.findByOrdenIdOrderByNumero(req.ordenId());
        if (!existentes.isEmpty()) {
            return existentes.stream().map(TicketResponse::de).toList();
        }
        List<Ticket> nuevos = new ArrayList<>();
        for (int numero = 1; numero <= req.cantidad(); numero++) {
            nuevos.add(new Ticket(generador.generar(), req.ordenId(), numero, req.eventoId(), req.compradorId()));
        }
        return tickets.saveAll(nuevos).stream().map(TicketResponse::de).toList();
    }

    /** Codigos de acceso de los tickets ya emitidos al comprador. */
    @Transactional(readOnly = true)
    public List<TicketResponse> listarDe(String compradorId) {
        return tickets.findByCompradorIdOrderByEmitidoEnDesc(compradorId).stream().map(TicketResponse::de).toList();
    }
}
