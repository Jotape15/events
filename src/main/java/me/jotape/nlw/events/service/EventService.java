package me.jotape.nlw.events.service;

import me.jotape.nlw.events.model.Event;
import me.jotape.nlw.events.repository.event.EventRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepo eventRepo;

    /**
     * Cria um novo evento.
     *
     * @param event Evento a ser criado.
     * @return Salva o evento.
     * @throws IllegalArgumentException if the event is null or invalid.
     */
    public Event createNewEvent(Event event) {
        event.setPrettyName(event.getTitle().toLowerCase().replaceAll(" ", "-"));
        return eventRepo.save(event);
    }

    /**
     * Retorna todos os eventos existentes.
     *
     * @return Lista com todos os eventos.
     */
    public List<Event> getAllEvents() {
        return (List<Event>) eventRepo.findAll();
    }

    /**
     * Retorna um evento pelo nome.
     *
     * @param prettyName Nome do evento.
     * @return O evento pelo nome.
     */
    public Event getByPrettyName(String prettyName) {
        return eventRepo.findEventByPrettyName(prettyName);
    }
}
