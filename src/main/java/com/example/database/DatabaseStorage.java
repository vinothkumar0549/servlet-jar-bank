package com.example.database;

import java.util.List;

import com.example.pojo.Activity;
import com.example.pojo.User;
import com.example.util.DatabaseConnection;

public class DatabaseStorage implements Storage{

    DatabaseConnection connection = DatabaseConnection.getInstance();

    @Override
    public boolean writeUser(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeUser'");
    }

    @Override
    public boolean writeActivity(Activity activity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'writeActivity'");
    }

    @Override
    public User getUser(int userid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public boolean updateUser(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    @Override
    public List<Activity> getActivity(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getActivity'");
    }

    @Override
    public List<User> TopNBalance(int n) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'TopNBalance'");
    }


    
}
