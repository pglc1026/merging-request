package com.springboot.sample.mapper;

import com.springboot.sample.bean.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersDao extends JpaRepository<Users, Long> {

    @Query(value = "select IFNULL(SUM(money), 0) as money from t_users where id between ?1 and ?2", nativeQuery = true)
    List<Users> selectSum(Integer fromId, Integer endId);
}
