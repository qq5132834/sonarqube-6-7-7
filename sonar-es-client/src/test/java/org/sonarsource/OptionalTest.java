package org.sonarsource;

import org.junit.Test;

import java.util.Optional;
import java.util.function.*;

public class OptionalTest {

    /***
     * BinaryOperator<T> -两个T作为输入，返回一个T作为输出，对于“reduce”操作很有用
     */
    @Test
    public void binaryOperatorTest(){
        BinaryOperator<User> binaryOperator = (x, y) -> {
            return new User(x.getName() + " " + y.getName(), x.getAge() + y.getAge());
        };
        User user = binaryOperator.apply(new User("hello", 1), new User(" world" , 1));
        System.out.println(user.toString());
    }


    /***
     * Function<T, R> -T作为输入，返回的R作为输出
     */
    @Test
    public void functionTest(){
        Function<User, String> function = (t)->{
            return "hello, " + t.getName() + ", are you " + t.getAge() + " old ?";
        };
        String str = function.apply(new User("huangliao", 10));
        System.out.println(str);
    }

    /***
     * 提供者
     * Supplier<T> 没有输入，T作为返回
     */
    @Test
    public void supplierTest(){

        //方式一
        Supplier<User> userSupplier = ()->{
            return new User("hello", 1);
        };

        //方式二
        userSupplier = ()-> new User("hello", 1);

        User user = userSupplier.get();
        System.out.println(user.toString());
    }

    /***
     * 消费者
     * Consumer<T>  T作为输入，没有返回
     */
    @Test
    public void consumerTest(){
        User user = new User("hello", 1);
        Consumer<User> consumer = (u)->{
            System.out.println(u.toString());
        };
        consumer.accept(user);
    }

    /***
     * 谓语
     * Predicate<T> ，T作为输入，bool返回
     */
    @Test
    public void PredicateTest() {
        Predicate<User> predicate = (t)->{
            return t.getAge() > 18;
        };
        boolean st = predicate.test(new User("hello", 10));
        System.out.println(st);
    }

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

        @Override
        public String toString() {
            return "name:" + this.getName() + ",age=" + this.getAge();
         }
    }
}
