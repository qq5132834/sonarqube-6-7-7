package org.sonarsource;

import org.junit.Test;

import java.util.Optional;

public class OptionalTest {


    @Test
    public void optionalTest(){
//        Optional<String> str = Optional.of(null); //不允许对象为null
//        Optional<String> str = Optional.ofNullable(null); //允许为空
//        Optional<String> str = Optional.empty();
        User user = null;
        Optional<User> optionalUser = Optional.ofNullable(user);
        System.out.println(optionalUser.isPresent());
        optionalUser = Optional.empty();
        System.out.println(optionalUser.isPresent());
        optionalUser = Optional.ofNullable(new User("huangliao", 100));

        User user0 = Optional.ofNullable(user).orElse(new User("joke", 10));
        System.out.println(user0.getName() + "," + user0.getAge());

        Optional<String> name = optionalUser.map(m->m.getName());
        System.out.println(name.get());

    }

    private class User{

        private final String name;
        private final Integer age;

        private User(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }
    }
}
