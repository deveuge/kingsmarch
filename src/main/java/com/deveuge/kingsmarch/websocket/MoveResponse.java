package com.deveuge.kingsmarch.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MoveResponse {

	private ResponseType responseType;
	private String gameFEN;
	private boolean refresh;
	
	public MoveResponse(boolean correct) {
		super();
		this.responseType = correct ? ResponseType.OK : ResponseType.SNAPBACK;
	}
	
}
