package com.miniblog.miniblog.models.data;

import com.miniblog.miniblog.models.Status;
import com.miniblog.miniblog.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatusRepository extends CrudRepository<Status, Integer> {

    ArrayList<Status> findAllByUserId(Integer user_id);

    Optional<Status> findById(Integer post_id);

    Iterable<Status> findAll();
}
