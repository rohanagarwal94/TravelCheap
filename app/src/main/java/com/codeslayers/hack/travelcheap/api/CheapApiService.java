package com.codeslayers.hack.travelcheap.api;

import com.codeslayers.hack.travelcheap.Model.Route;

import java.io.IOException;
import java.util.List;

public class CheapApiService extends APIServices {
	private static CheapApiService instance;
	private final CheapApi cheapApi;

	public CheapApiService() {
		cheapApi = retrofit.create(CheapApi.class);
	}

	public static CheapApiService getInstance() {
		if (instance == null) {
			instance = new CheapApiService();
		}
		return instance;
	}

    public retrofit.Response<List<Route>> getFeedData(String url) throws IOException{
        return cheapApi.getRoutes(UBER_END_POINT + url).execute();
    }
}
