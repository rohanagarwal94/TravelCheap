package com.codeslayers.hack.travelcheap.api;

import com.codeslayers.hack.travelcheap.Model.Route;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Url;

public interface CheapApi {
	@GET
	Call<List<Route>> getRoutes(@Url String url);
}
