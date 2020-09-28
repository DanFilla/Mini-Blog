package com.miniblog.miniblog.models.data;

import com.miniblog.miniblog.models.Comment;
import com.miniblog.miniblog.models.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Integer> {

    ArrayList<Comment> findAllByStatusId(Integer post_id);
}
