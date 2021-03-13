/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wspackage.resources;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import org.mapstruct.Context;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Dell
 */

@RestController
@CrossOrigin
@RequestMapping({"/v1"})

public class Resource {
    
    @GetMapping("/createDynamoDBConnection/{jo}")
    public void createDynamoDBConnection(@RequestParam JSONObject jo, HttpSession httpSession, @Context HttpServletRequest requestContext) throws Exception {
        String awsAccessKey = (String) jo.get("awsAccessKey");
        String awsSecretKey = (String) jo.get("awsSecretKey");
        String region = (String) jo.get("region");

        httpSession.setAttribute("awsAccessKey", awsAccessKey);
        httpSession.setAttribute("awsSecretKey", awsSecretKey);
        httpSession.setAttribute("region", region);

    }

    //LIST TABLES
    @GetMapping("/listTables")
    public void listTables(HttpSession httpSession) throws Exception {

        String awsAccessKey = (String) httpSession.getAttribute("awsAccessKey");
        String awsSecretKey = (String) httpSession.getAttribute("awsSecretKey");
        String region = (String) httpSession.getAttribute("region");
        final AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        final AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(region).withCredentials(credentialsProvider).build();
        DynamoDB dynamoDB = new DynamoDB(client);

        try {
            TableCollection<ListTablesResult> tables = dynamoDB.listTables();
            Iterator<Table> iterator = tables.iterator();

            System.out.println("Listing table names");

            while (iterator.hasNext()) {
                Table table = iterator.next();
                System.out.println(table.getTableName());
            }
        } catch (Exception e) {
            System.err.println("Listing of tables failed.");
            System.err.println(e.getMessage());

        }
    }
    
    @GetMapping("/queryTable")
    public void queryTable(@RequestParam JSONObject jo, HttpSession httpSession){
        String awsAccessKey = (String) httpSession.getAttribute("awsAccessKey");
        String awsSecretKey = (String) httpSession.getAttribute("awsSecretKey");
        String region = (String) httpSession.getAttribute("region");
        final AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        final AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(region).withCredentials(credentialsProvider).build();
        DynamoDB dynamoDB = new DynamoDB(client);
        String tableName = (String)jo.get("table_name");
        
        Table table = dynamoDB.getTable(tableName);
        String country = (String)jo.get("country");

        ScanSpec scanSpec = new ScanSpec()
            .withFilterExpression("#country = :country").withNameMap(new NameMap().with("#country", "country"))
            .withValueMap(new ValueMap().withString(":country", country));

        try {
            ItemCollection<ScanOutcome> items = table.scan(scanSpec);

            Iterator<Item> iter = items.iterator();
            while (iter.hasNext()) {
                Item item = iter.next();
                System.out.println(item.toString());
            }

        }
        catch (Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
        }
    }    
}
