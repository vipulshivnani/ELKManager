package ELK;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@ComponentScan("test.builder")
public class Application {

    public static void main(String[] args) {
        /*System.out.print("Hi from main before kafka call");
        Thread t1= new Thread(new KafkaProducer());
        t1.start();*/

        //Poll a= new Poll();

        SpringApplication.run(Application.class, args);

    }
}
