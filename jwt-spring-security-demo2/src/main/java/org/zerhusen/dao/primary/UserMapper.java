package org.zerhusen.dao.primary;

import org.apache.ibatis.annotations.Param;
import org.zerhusen.core.Mapper;
import org.zerhusen.model.primary.User;

public interface UserMapper extends Mapper<User> {

   User findOneWithAuthoritiesByUsername(@Param("username") String username);

   User findOneWithAuthoritiesByEmailIgnoreCase(@Param("email") String email);

   User getUserWithAuthorities(@Param("username") String username);
}
