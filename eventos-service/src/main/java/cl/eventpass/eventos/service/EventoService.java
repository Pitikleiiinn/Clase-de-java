package cl.eventpass.eventos.service;

import cl.eventpass.eventos.dto.EventoRequest;
import cl.eventpass.eventos.dto.EventoResponse;
import cl.eventpass.eventos.model.Evento;
import cl.eventpass.eventos.repository.EventoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventos;

    public EventoService(EventoRepository eventos) {
        this.eventos = eventos;
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> listar() {
        return eventos.findAllByOrderByFechaAsc().stream().map(EventoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public EventoResponse obtener(Long id) {
        return eventos.findById(id)
                .map(EventoResponse::de)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado"));
    }

    @Transactional
    public EventoResponse crear(EventoRequest req) {
        if (eventos.existsByNombre(req.nombre().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un evento con ese nombre");
        }
        Evento nuevo = new Evento(req.nombre().trim(), req.lugar().trim(), req.fecha(), req.aforo(), req.precio());
        return EventoResponse.de(eventos.save(nuevo));
    }
}
