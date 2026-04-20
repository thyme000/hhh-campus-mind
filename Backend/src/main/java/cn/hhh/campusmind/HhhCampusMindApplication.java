package cn.hhh.campusmind;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.hhh.campusmind.*.mapper")
public class HhhCampusMindApplication {

    public static void main(String[] args) {
        SpringApplication.run(HhhCampusMindApplication.class, args);
    }
}
