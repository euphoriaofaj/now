package com.morago.backend.mapper;

import com.morago.backend.entity.Theme;
import com.morago.backend.entity.User;
import org.mapstruct.ObjectFactory;
import org.springframework.stereotype.Component;

@Component
public class  RefMappers {
    public User mapUserId(Long id) {
        if (id == null) return null;
        var u = new User();
        u.setId(id);
        return u;
    }

    public Theme mapThemeId(Long id) {
        if (id == null) return null;
        var t = new Theme();
        t.setId(id);
        return t;
    }

    @ObjectFactory public User toUser(Long id) { return mapUserId(id); }
    @ObjectFactory public Theme toTheme(Long id) { return mapThemeId(id); }
}