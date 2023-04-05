package com.app.spotify.security;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import com.google.gson.Gson;
import io.jsonwebtoken.*;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.Claims;

import java.util.Base64;
import java.util.Objects;
import java.util.function.Function;
import java.util.Date;

public class JwtUtils {
    private String jwtSecret = "secret";

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public String getPayloadFromToken(String token)
    {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String payload = new String(decoder.decode(chunks[1]));

        return payload;
    }

    public String getUserNameFromToken(String token)
    {
        String payload = getPayloadFromToken(token);
        Gson g = new Gson();
        Jwt jwt = g.fromJson(payload, Jwt.class);
        return jwt.getSub().split(":")[1];
    }

    public boolean isTokenExpired(String token){

        try{
            String payload = getPayloadFromToken(token);
            Gson g = new Gson();
            Jwt jwt = g.fromJson(payload, Jwt.class);


            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/y H:m:s");
            LocalDateTime now = LocalDateTime.now();
            String now_formatted = dtf.format(now);

            //data curenta
            SimpleDateFormat sf = new SimpleDateFormat("M/d/y H:m:s");
            Date dateTime_now = sf.parse(now_formatted);

            //data din token
            Date dateTime_token = sf.parse(jwt.getExp());

            if (dateTime_now.compareTo(dateTime_token) > 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }catch(Exception e)
        {
            System.out.println(e.getMessage());
            return true;
        }

    }

    public String[] getRolesFromToken(String token)
    {
        String payload = getPayloadFromToken(token);
        Gson g = new Gson();
        Jwt jwt = g.fromJson(payload, Jwt.class);

        String roles = jwt.getRole();
        String[] roles_list = roles.split(" ");
        return roles_list;
    }

    public boolean isUserContentManager(String token)
    {
        String[] roles = getRolesFromToken(token);
        for(String role:roles)
        {
            if (Objects.equals(role, "21"))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isUserClient(String token)
    {
        String[] roles = getRolesFromToken(token);
        for(String role:roles)
        {
            if (Objects.equals(role, "19"))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isUserArtist(String token)
    {
        String[] roles = getRolesFromToken(token);
        for(String role:roles)
        {
            if (Objects.equals(role, "18"))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isUserAdministratorAplicatie(String token)
    {
        String[] roles = getRolesFromToken(token);
        for(String role:roles)
        {
            if (Objects.equals(role, "20"))
            {
                return true;
            }
        }
        return false;
    }

}
