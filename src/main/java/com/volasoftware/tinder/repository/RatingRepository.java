package com.volasoftware.tinder.repository;

import com.volasoftware.tinder.model.Rating;
import com.volasoftware.tinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Rating findByUserAndFriend(User user, User friend);
}
