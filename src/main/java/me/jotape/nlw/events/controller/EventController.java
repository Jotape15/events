package me.jotape.nlw.events.controller;

import me.jotape.nlw.events.model.Event;
import me.jotape.nlw.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events") // Evita repetição de "/events" nos métodos
public class EventController {

    @Autowired
    private EventService eventService;

    /**
     * Método para criar um novo evento.
     *
     * @param event Objeto do tipo Event contendo os dados do evento a ser criado.
     * @return O evento criado e salvo no banco de dados.
     */
    @PostMapping
    public Event createNewEvent(@RequestBody Event event) {
        return eventService.createNewEvent(event);
    }

    /**
     * Retorna uma lista com todos os eventos cadastrados.
     *
     * @return Lista contendo todos os eventos.
     */
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    /**
     * Busca um evento pelo nome.
     *
     * @param prettyName Nome do evento.
     * @return ResponseEntity com o evento encontrado ou status "Not Found" se ele não existir.
     */
    @GetMapping("/{prettyName}")
    public ResponseEntity<Event> getEventByPrettyName(@PathVariable String prettyName) {
        Event event = eventService.getByPrettyName(prettyName);
        if (event != null) {
            return ResponseEntity.ok().body(event);
        }
        return ResponseEntity.notFound().build();
    }
}