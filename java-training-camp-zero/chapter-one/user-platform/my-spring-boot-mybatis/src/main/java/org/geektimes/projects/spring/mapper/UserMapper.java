package org.geektimes.projects.spring.mapper;

import org.geektimes.projects.spring.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserMapper
 *
 * @author yuancome
 * @date 2021/5/26
 */
@Repository
public interface UserMapper {

    User findUserById(Integer id) throws Exception;

    List<User> findList();
}
