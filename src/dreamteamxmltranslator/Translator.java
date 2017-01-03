/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamteamxmltranslator;

import com.mycompany.dreamteamxml.IOException_Exception;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Buhrkall
 */
public class Translator {

    static final String LISTENING_QUEUE_NAME = "DreamTeamXMLTranslatorQueue";
    static final String EXCHANGE_NAME = "TranslatorExchange";

    
    static String message = "";

    public static void main(String[] args) throws IOException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("datdb.cphbusiness.dk");
        factory.setUsername("Dreamteam");
        factory.setPassword("bastian");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(LISTENING_QUEUE_NAME, false, false, false, null);
        channel.queueBind(LISTENING_QUEUE_NAME, EXCHANGE_NAME, "DreamTeamBankXML");

        
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                try {
                    message = new String(body, "UTF-8");
                    System.out.println(" [x] Received '" + message + "'");
                    
                    String[] arr = message.split(",");
                    tester(arr);
                } catch (IOException_Exception ex) {
                    Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        };
        channel.basicConsume(LISTENING_QUEUE_NAME, true, consumer);

    }

    private static void tester(String[] arr) throws IOException_Exception {
        System.out.println("We in here");
        String test = request(arr[0], Integer.parseInt(arr[1]), Double.parseDouble(arr[2]), Integer.parseInt(arr[3]));
        System.out.println(test);
    }

    private static String request(java.lang.String ssn, int creditScore, double loanAmount, int loanDuration) throws IOException_Exception {
        com.mycompany.dreamteamxml.GetLoan service = new com.mycompany.dreamteamxml.GetLoan();
        com.mycompany.dreamteamxml.DreamTeamXML port = service.getDreamTeamXMLPort();
        return port.request(ssn, creditScore, loanAmount, loanDuration);
    }

  

}
