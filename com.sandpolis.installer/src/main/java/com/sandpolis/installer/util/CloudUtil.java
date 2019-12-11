//============================================================================//
//                                                                            //
//                Copyright © 2015 - 2020 Subterranean Security               //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation at:                                //
//                                                                            //
//    https://mozilla.org/MPL/2.0                                             //
//                                                                            //
//=========================================================S A N D P O L I S==//
package com.sandpolis.installer.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

public final class CloudUtil {

	public static final class ListenResult {

		public final int status;

		public final String config;

		private ListenResult(int status) {
			this.status = status;
			this.config = null;
		}

		private ListenResult(String config) {
			this.status = 200;
			this.config = config;
		}
	}

	public static ListenResult listen(String token) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://api.sandpolis.com/v1/cloud/client/listen")).timeout(Duration.ofSeconds(30))
				.POST(BodyPublishers.ofString("{token: \"" + token + "\"}")).build();

		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			return new ListenResult(response.statusCode());
		}

		return new ListenResult(response.body());
	}

	private CloudUtil() {
	}
}
