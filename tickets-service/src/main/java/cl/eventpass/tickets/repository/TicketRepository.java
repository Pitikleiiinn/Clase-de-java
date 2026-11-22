package cl.eventpass.tickets.repository;

import cl.eventpass.tickets.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByOrdenIdOrderByNumero(String ordenId);

    List<Ticket> findByCompradorIdOrderByEmitidoEnDesc(String compradorId);

    Optional<Ticket> findByCodigo(String codigo);
}
