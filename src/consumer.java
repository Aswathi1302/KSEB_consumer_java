import netscape.javascript.JSObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Properties;

public class consumer {
    public static void main(String[] args) {
        KafkaConsumer consumer;
        String topic="kseb";
        String broker="localhost:9092";
        Properties props=new Properties();
        props.put("bootstrap.servers",broker);
        props.put("group.id","test.group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer =new KafkaConsumer(props);
        consumer.subscribe(Arrays.asList(topic));
        while (true)
        {
            ConsumerRecords<String,String> records =consumer.poll(100);
            for(ConsumerRecord<String,String> record:records)
            {
                System.out.println(record.value());
                JSONObject obj=new JSONObject(record.value()) ;
                String userid=String.valueOf(obj.getInt("userid"));
                String unit=String.valueOf(obj.getInt("unit"));
                System.out.println(userid);
                System.out.println(unit);
                try{
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection con= DriverManager.getConnection("jdbc:mysql://localhost:3306/admindb","root","");
                    String sql="INSERT INTO `usagetbl`(`userid`, `unit`, `datetime`) VALUES (?,?,now())";
                    PreparedStatement stmt=con.prepareStatement((sql));
                    stmt.setString(1,userid);
                    stmt.setString(2,unit);
                    stmt.executeUpdate();
                    System.out.println("value inserted successfully.........!");

                }
                catch (Exception e){
                    System.out.println((e));
                }


            }
        }

    }
}
