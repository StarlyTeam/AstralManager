package kr.starly.manager.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.nio.sctp.IllegalReceiveException;
import kr.starly.manager.AstralManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class AstralPluginUtil {

    private AstralPluginUtil() {}

    public static Map<String, JsonElement> fetchPluginList() throws IOException {
        JsonObject jsonObject = requestBody("https://starly.kr/api/v1/plugins");

        String status = jsonObject.get("status").getAsString();
        if (!status.equals("DONE")) {
            throw new IllegalReceiveException("Unexpected status: " + status + " [" + jsonObject + "]");
        }

        JsonObject data = jsonObject.get("data").getAsJsonObject();
        return data.asMap();
    }

    public static Map<String, JsonElement> fetchInstalledPluginList() throws IOException {
        PluginManager pluginManager = AstralManager.getInstance().getServer().getPluginManager();

        Map<String, JsonElement> astralPlugins = fetchPluginList();

        Map<String, JsonElement> result = new HashMap<>();
        astralPlugins.forEach((pluginName, pluginData) -> {
            Plugin plugin = pluginManager.getPlugin(pluginName);
            if (plugin == null) return;

            result.put(pluginName, pluginData);
        });

        return result;
    }

    public static Map<String, JsonElement> fetchPlugin(String ENName) throws IOException {
        JsonObject jsonObject = requestBody("https://starly.kr/api/v1/plugins/" + ENName);

        String status = jsonObject.get("status").getAsString();
        if (!status.equals("DONE")) {
            throw new IllegalReceiveException("Unexpected status: " + status + " [" + jsonObject + "]");
        }

        JsonObject data = jsonObject.get("data").getAsJsonObject();
        return data.asMap();
    }


    private static JsonObject requestBody(String url) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
        conn.setDoOutput(false);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");

        InputStream is;
        if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            is = conn.getInputStream();
        } else {
            is = conn.getErrorStream();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        JsonObject jsonObject = JsonParser
                .parseString(
                        br.lines()
                                .collect(Collectors.joining(""))
                )
                .getAsJsonObject();

        is.close();
        br.close();
        return jsonObject;
    }
}