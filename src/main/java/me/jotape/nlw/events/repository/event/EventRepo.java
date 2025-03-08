package me.jotape.nlw.events.repository.event;

import me.jotape.nlw.events.model.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepo extends CrudRepository<Event, Integer> {
    Event findEventByPrettyName(String prettyName);
}
