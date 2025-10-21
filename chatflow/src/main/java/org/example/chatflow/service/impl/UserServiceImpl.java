package org.example.chatflow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.chatflow.model.entity.User;
import org.example.chatflow.service.UserService;
import org.example.chatflow.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @author by zzr
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




