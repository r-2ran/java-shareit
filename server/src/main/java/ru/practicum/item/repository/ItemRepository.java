package ru.practicum.item.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(" select i from Item as i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')) and i.isAvailable = true")
    List<Item> search(String text);

    List<Item> findAllByOwnerId(long userId);

    List<Item> findAllByRequestId(long requestId);

    Item findByIdAndRequestId(long itemId, long requestId);
}
