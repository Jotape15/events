package me.jotape.nlw.events.service;

import me.jotape.nlw.events.exception.EventNotFoundException;
import me.jotape.nlw.events.exception.SubscriptionConflictException;
import me.jotape.nlw.events.exception.UserIndicatorNotFoundException;
import me.jotape.nlw.events.exception.dto.SubscriptionRankingByUser;
import me.jotape.nlw.events.exception.dto.SubscriptionRankingItem;
import me.jotape.nlw.events.exception.dto.SubscriptionResponse;
import me.jotape.nlw.events.model.Event;
import me.jotape.nlw.events.model.Subscription;
import me.jotape.nlw.events.model.User;
import me.jotape.nlw.events.repository.event.EventRepo;
import me.jotape.nlw.events.repository.subscription.SubscriptionRepo;
import me.jotape.nlw.events.repository.user.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class SubscriptionService {

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubscriptionRepo subscriptionRepo;

    /**
     * Cria uma inscrição para um evento.
     *
     * @param eventName Nome do evento.
     * @param user Usuário que está se inscrevendo no evento.
     * @param userId ID do usuário que fez a indicação (opcional).
     * @return SubscriptionResponse contendo os detalhes da inscrição.
     * @throws EventNotFoundException Se o evento não for encontrado.
     * @throws UserIndicatorNotFoundException Se o usuário indicador não for encontrado.
     * @throws SubscriptionConflictException Se o usuário já estiver inscrito no evento.
     */
    public SubscriptionResponse createSubscription(String eventName, User user, Integer userId) {
        Event eventByPrettyName = eventRepo.findEventByPrettyName(eventName);

        if (eventByPrettyName == null) {
            throw new EventNotFoundException("Evento com o nome " + eventName + " não encontrado");
        }

        User userByEmail = userRepo.findByEmail(user.getEmail());
        if (userByEmail == null) {
            userByEmail = userRepo.save(user);
        }

        User userById = null;
        if (userId != null) {
            userById = userRepo.findById(userId).orElse(null);
            if (userById == null) {
                throw new UserIndicatorNotFoundException("Usuário de id " + userId + " não encontrado");
            }
        }

        Subscription subscription = new Subscription();
        subscription.setEvent(eventByPrettyName);
        subscription.setSubscriber(userByEmail);
        subscription.setIndication(userById);

        Subscription byEventAndSubscriber = subscriptionRepo.findSubscriptionByEventAndSubscriber(eventByPrettyName, userByEmail);
        if (byEventAndSubscriber != null) {
            throw new SubscriptionConflictException(userByEmail + " já tem uma inscrição");
        }

        Subscription save = subscriptionRepo.save(subscription);
        return new SubscriptionResponse(save.getSubscriptionNumber(), "https://codecraft.com/subscription/"
                + save.getEvent().getPrettyName()
                + "/"
                + save.getSubscriber().getId());
    }

    /**
     * Retorna o ranking de usuários baseado no número de indicações que eles possuem em um evento.
     *
     * @param prettyName Nome do evento.
     * @return Lista de SubscriptionRankingItem representando o ranking dos usuários.
     * @throws EventNotFoundException Se o evento não for encontrado.
     */
    public List<SubscriptionRankingItem> getCompleteRanking(String prettyName) {
        Event event = eventRepo.findEventByPrettyName(prettyName);

        if (event == null) {
            throw new EventNotFoundException("Ranking do evento " + prettyName + " não existe");
        }

        return subscriptionRepo.generateRanking(event.getEventId());
    }

    /**
     * Retorna a posição no ranking de um usuário em um evento.
     *
     * @param prettyName Nome do evento.
     * @param userId ID do usuário a ser buscado no ranking.
     * @return SubscriptionRankingByUser com detalhes do usuário no ranking e a posição.
     * @throws UserIndicatorNotFoundException Se o usuário não tiver nenhuma indicação no evento.
     */
    public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId) {
        List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyName);
        SubscriptionRankingItem item = ranking.stream().filter(i -> i.userId().equals(userId)).findFirst().orElse(null);

        if (item == null) {
            throw new UserIndicatorNotFoundException("Não há inscrições com indicação do usuário " + userId);
        }

        int position = IntStream.range(0, ranking.size())
                .filter(pos -> ranking.get(pos).userId().equals(userId))
                .findFirst().getAsInt();
        return new SubscriptionRankingByUser(item, position + 1);
    }
}