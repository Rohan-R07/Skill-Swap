package com.example.backend.repository;

import com.example.backend.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @EntityGraph(attributePaths = "sender")
    Page<Message> findByConversationId(Long conversationId, Pageable pageable);

    @EntityGraph(attributePaths = {"sender", "conversation"})
    @Query("select m from Message m where m.conversation.id in :conversationIds and m.createdAt = (select max(latest.createdAt) from Message latest where latest.conversation.id = m.conversation.id) and m.id = (select max(tie.id) from Message tie where tie.conversation.id = m.conversation.id and tie.createdAt = m.createdAt)")
    List<Message> findLatestForConversations(@Param("conversationIds") Collection<Long> conversationIds);
}
