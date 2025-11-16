package br.com.saintmc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CommonConst {

    public static final Gson GSON = new GsonBuilder().serializeNulls().create();

    public static final int MAX_DEFAULT_SERVER_PLAYERS = 100;

    public static final String API_URL = "https://api.saintmc.com.br/api/v1";

}
