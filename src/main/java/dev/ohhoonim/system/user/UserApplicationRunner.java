package dev.ohhoonim.system.user;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import dev.ohhoonim.system.user.model.UserId;

@Component
public class UserApplicationRunner implements ApplicationRunner{

    @Autowired
    private JdbcClient jdbcClient; 

    @Autowired
    PasswordEncoder encoder;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        // UserId testUserId = UserId.Creator.generate();
        // // 테스트 데이터 삽입
        // jdbcClient.sql("""
        //             INSERT INTO system_users (
        //                 user_id, external_id, status, username, employee_no, email, password,
        //                 created_at, created_by, modified_at, modified_by
        //             ) VALUES (
        //                 :id, :externalId, 'ACTIVE', 'tester', 'EMP001', 'test@example.com', :password,
        //                 NOW(), :createdBy, NOW(), :modifiedBy 
        //             )
        //         """)
        //         .param("id", testUserId.internalId())
        //         .param("externalId", testUserId.externalId()) 
        //         .param("password", encoder.encode("password123"))
        //         .param("createdBy", UUID.randomUUID())
        //         .param("modifiedBy", UUID.randomUUID())
        //         .update();

    }
    
}
