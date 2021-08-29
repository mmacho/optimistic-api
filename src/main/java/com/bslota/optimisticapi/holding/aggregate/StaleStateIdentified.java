package com.bslota.optimisticapi.holding.aggregate;

import java.util.UUID;

public class StaleStateIdentified extends RuntimeException {

	private static final long serialVersionUID = -8449659856953767236L;

	private StaleStateIdentified(UUID id) {
		super(String.format("Aggregate of id %s is stale", id));
	}

	public static StaleStateIdentified forAggregateWith(UUID id) {
		return new StaleStateIdentified(id);
	}
}
