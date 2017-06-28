package com.dewes.odonto.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gabriel on 28/06/2017.
 */

public final class Utils {

    static Map<String, String> humanRoles = new HashMap<String, String>() {{
        put("ROLE_ADMIN", "Administrador");
        put("ROLE_USER", "Usuário");
        put("ROLE_CONTRIBUTOR", "Contribuinte");
    }};

    public static String roleToHuman(String role) {
        return humanRoles.get(role);
    }

    public static String rolesToHuman(List<String> roles) {
        String result = "";
        for (int i=0; i<roles.size(); i++) {
            result += humanRoles.get(roles.get(i));
            if (i+1 != roles.size())
                result += ", ";
        }
        return result;
    }
}
