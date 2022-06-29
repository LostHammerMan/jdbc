package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection(){
        try{
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            log.info("get connection={}, class={}", connection, connection.getClass()); // 객체 정보와 타입 정보 출력
            return connection;
        }catch (SQLException e){
            throw new IllegalStateException(e); // 런타임 exception으로 예외 처리
        }
    }
}
