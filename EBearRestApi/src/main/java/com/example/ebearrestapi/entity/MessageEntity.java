package com.example.ebearrestapi.entity;

import com.example.ebearrestapi.etc.Validate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MESSAGE")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MessageEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageNo;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private boolean isRead;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Validate validate = Validate.UNCHECK;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "messageRoomNo")
    private MessageRoomEntity messageRoom;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultationNo")
    private ConsultaionEntity consultation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userNo", nullable = false)
    private UserEntity user;
    
    // 비즈니스 로직
    public void markAsRead() {
        this.validate = Validate.CHECK;
    }

    public void updateRead() {
        this.isRead = true;
    }
}
