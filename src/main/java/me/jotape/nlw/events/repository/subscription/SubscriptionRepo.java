package me.jotape.nlw.events.repository.subscription;

import me.jotape.nlw.events.exception.dto.SubscriptionRankingItem;
import me.jotape.nlw.events.model.Event;
import me.jotape.nlw.events.model.Subscription;
import me.jotape.nlw.events.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscriptionRepo extends CrudRepository<Subscription, Integer> {
    Subscription findSubscriptionByEventAndSubscriber(Event event, User subscriber);

    @Query(value = "select count(subscription_number) as quantidade, indication_user_id, user_name" +
            " from tbl_subscription inner join tbl_user" +
            " on tbl_subscription.indication_user_id = tbl_user.user_id " +
            " where indication_user_id is not null" +
            "    and event_id = :eventId" +
            " group by indication_user_id" +
            " order by quantidade desc", nativeQuery = true)
     List<SubscriptionRankingItem> generateRanking(@Param("eventId") Integer eventId);
}
