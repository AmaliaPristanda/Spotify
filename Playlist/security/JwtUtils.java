package com.app.playlist.security;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

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

    public Integer getUserIdFromToken(String token)
    {
        String payload = getPayloadFromToken(token);
        Gson g = new Gson();
        Jwt jwt = g.fromJson(payload, Jwt.class);
        System.out.println("aici6");
        System.out.println(Integer.parseInt(jwt.getSub().split(":")[0]));
        return Integer.parseInt(jwt.getSub().split(":")[0]);
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
            System.out.println("data curenta - " + now_formatted);

            //data curenta
            SimpleDateFormat sf = new SimpleDateFormat("M/d/y H:m:s");
            Date dateTime_now = sf.parse(now_formatted);
            System.out.println("aici1");
            //data din token
            Date dateTime_token = sf.parse(jwt.getExp());
            System.out.println("aici2");
            if (dateTime_now.compareTo(dateTime_token) > 0)
            {
                System.out.println("aici3");
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

        System.out.println("roluri: " + roles);
        String[] roles_list = roles.split(" ");

        return roles_list;
    }

    public boolean isUserContentManager(String token)
    {
        String[] roles = getRolesFromToken(token);
        for(String role:roles)
        {
            if (role == "21")
            {
                return true;
            }
        }
        return false;
    }

    public boolean isUserClient(String token)
    {
        String[] roles = getRolesFromToken(token);
        System.out.println(Arrays.toString(roles));
        for(String role:roles)
        {
            System.out.println("rol: " + role);
            if (Objects.equals(role, "19"))
            {
                System.out.println("aici4");
                return true;
            }
        }
        System.out.println("aici5");
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
            System.out.println("rol: " + role);
            if (Objects.equals(role, "20"))
            {
                return true;
            }
        }
        return false;
    }

}
