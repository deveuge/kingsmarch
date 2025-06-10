package com.deveuge.kingsmarch.infra.websocket;

import com.deveuge.kingsmarch.domain.engine.types.Colour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

	private String content;
	private MessageType type;
	private Colour colour;
	private MoveResponse moveResponse;
	private int players;
}
