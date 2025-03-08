package me.jotape.nlw.events.controller;

import me.jotape.nlw.events.exception.EventNotFoundException;
import me.jotape.nlw.events.exception.RankingNotFoundException;
import me.jotape.nlw.events.exception.SubscriptionConflictException;
import me.jotape.nlw.events.exception.UserIndicatorNotFoundException;
import me.jotape.nlw.events.exception.dto.ErrorMessage;
import me.jotape.nlw.events.exception.dto.SubscriptionRankingItem;
import me.jotape.nlw.events.exception.dto.SubscriptionResponse;
import me.jotape.nlw.events.model.User;
import me.jotape.nlw.events.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscription") // Evita repetição de "/subscription" nos métodos
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    /**
     * Cria uma inscrição para um evento.
     *
     * @param prettyName Nome do evento.
     * @param subscriber Usuário que está se inscrevendo.
     * @param userId ID do usuário (opcional).
     * @return ResponseEntity com a resposta da inscrição ou mensagem de erro.
     */
    @PostMapping({"/{prettyName}", "/{prettyName}/{userId}"})
    public ResponseEntity<?> createSubscription(
            @PathVariable String prettyName,
            @RequestBody User subscriber,
            @PathVariable(required = false) Integer userId) {
        try {
            SubscriptionResponse subscription = subscriptionService.createSubscription(prettyName, subscriber, userId);
            if (subscription != null) {
                return ResponseEntity.ok(subscription);
            }
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorMessage(e.getMessage()));
        } catch (SubscriptionConflictException e) {
            return ResponseEntity.status(409).body(new ErrorMessage(e.getMessage()));
        } catch (UserIndicatorNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorMessage(e.getMessage()));
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Gera um ranking dos 3 participantes com mais indicações naquele evento.
     *
     * @param prettyName Nome do evento.
     * @return ResponseEntity com o ranking ou mensagem de erro.
     */
    @GetMapping("/{prettyName}/ranking")
    public ResponseEntity<?> generateRankingByEvent(@PathVariable String prettyName) {
        try {
            List<SubscriptionRankingItem> ranking = subscriptionService.getCompleteRanking(prettyName);
            int size = ranking.size();

            if (size == 0) {
                throw new RankingNotFoundException("Nenhum participante encontrado no ranking.");
            }

            return ResponseEntity.ok(ranking.subList(0, Math.min(size, 3)));
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(404).body(new ErrorMessage(e.getMessage()));
        } catch (RankingNotFoundException e) {
            return ResponseEntity.status(200).body(new ErrorMessage(e.getMessage()));
        }
    }

    /**
     * Mostra se há inscrições com indicação do usuário.
     *
     * @param prettyName Nome do evento.
     * @param userId ID do usuário.
     * @return ResponseEntity com o ranking do usuário ou mensagem de erro.
     */
    @GetMapping("/{prettyName}/ranking/{userId}")
    public ResponseEntity<?> generateRankingByEventAndUser(
            @PathVariable String prettyName,
            @PathVariable Integer userId) {
        try {
            return ResponseEntity.ok(subscriptionService.getRankingByUser(prettyName, userId));
        } catch (Exception ex) {
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        }
    }
}