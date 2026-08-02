package com.example.ebearrestapi.repository;

import com.example.ebearrestapi.entity.ConsultaionEntity;
import com.example.ebearrestapi.entity.MessageEntity;
import com.example.ebearrestapi.entity.MessageRoomEntity;
import com.example.ebearrestapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByConsultation(ConsultaionEntity consultation);

    List<MessageEntity> findAllByIsRead(boolean isRead);

    List<MessageEntity> findAllByIsReadAndMessageRoom(boolean isRead, MessageRoomEntity messageRoom);

    List<MessageEntity> findAllByIsReadAndMessageRoomAndUser(boolean isRead, MessageRoomEntity messageRoom, UserEntity user);
}
