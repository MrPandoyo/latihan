package com.artivisi.kampus.latihan.dao;

import com.artivisi.kampus.latihan.entity.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserDao extends PagingAndSortingRepository<User,String> {

    User findByUsername(String username);

}
