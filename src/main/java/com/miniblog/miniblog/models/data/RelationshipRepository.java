package com.miniblog.miniblog.models.data;

import com.miniblog.miniblog.models.UserRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationshipRepository extends JpaRepository<UserRelationship, Integer> {
    Iterable<UserRelationship> findAllByUserIdOneId(int id);
    Iterable<UserRelationship> findAllByUserIdTwoId(int id);
}

