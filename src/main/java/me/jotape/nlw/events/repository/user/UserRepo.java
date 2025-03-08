package me.jotape.nlw.events.repository.user;

import me.jotape.nlw.events.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Integer> {
    User findByEmail(String email);
}
