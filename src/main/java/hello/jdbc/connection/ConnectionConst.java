package hello.jdbc.connection;

// 상수 모아둔 것 -> 객체 생성하면 안됨
public abstract class ConnectionConst {

    public static final String URL = "jdbc:h2:tcp://localhost/~/test";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "";
}
