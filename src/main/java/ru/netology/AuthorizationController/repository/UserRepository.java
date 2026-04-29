package ru.netology.AuthorizationController.repository;

import org.springframework.stereotype.Repository;
import ru.netology.AuthorizationController.model.Authorities;

import java.util.*;

@Repository
public class UserRepository {

    private final Map<String, Map<String, List<Authorities>>> userDatabase = new HashMap<>();

    public UserRepository() {
        initTestData();
    }

    private void initTestData() {
        Map<String, List<Authorities>> user1Data = new HashMap<>();
        user1Data.put("123pass", Arrays.asList(Authorities.READ, Authorities.WRITE));
        userDatabase.put("sergey", user1Data);

        Map<String, List<Authorities>> user2Data = new HashMap<>();
        user2Data.put("1pass1", Arrays.asList(Authorities.READ, Authorities.WRITE, Authorities.DELETE));
        userDatabase.put("alex", user2Data);

        Map<String, List<Authorities>> user3Data = new HashMap<>();
        user3Data.put("333pass9", Collections.singletonList(Authorities.READ));
        userDatabase.put("ivan", user3Data);
    }

    public List<Authorities> getUserAuthorities(String user, String password) {
        if (!userDatabase.containsKey(user)) {
            return Collections.emptyList();
        }
        Map<String, List<Authorities>> userData = userDatabase.get(user);
        if (!userData.containsKey(password)) {
            return Collections.emptyList();
        }
        return userData.get(password);
    }

}
