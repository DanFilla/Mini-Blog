package com.miniblog.miniblog.models.data;

import com.miniblog.miniblog.models.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends CrudRepository<Status, Integer> {
    Optional<Status> findById(Integer id);

    Iterable<Status> findAll();
}
