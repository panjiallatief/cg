package com.cg;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ServiceXml {

    final Map<String, String> dataMap = new ConcurrentHashMap<>();

    @GetMapping("/")
    public String index(Model model) {
        dataMap.clear();
        model.addAttribute("dataMap",dataMap);
        return "index";
    }

    @PostMapping("/saveData")
    public ResponseEntity<Map<String, Object>> saveData(
            @RequestParam("key") String key,
            @RequestParam("data") String[] data) {

        Map<String, Object> responseData = new HashMap<>();


        // Convert the String array to a single String
        String dataAsString = Arrays.toString(data);

        // Put the key-value pair in the map
        dataMap.put(key, dataAsString);

        // Optionally, you can save the XML to file here if needed
        // saveXmlToFile();

        // Put the dataMap in the responseData map
        responseData.put("data", dataMap);

        // Return the ResponseEntity with the responseData and HttpStatus.OK
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @RequestMapping("/getData")
    public ResponseEntity<Map> getData() {
        Map data = new HashMap<>();

        data.put("data", dataMap);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    private void saveXmlToFile() {
        String xmlResult = generateXmlFromMap();
        String filePath = "C:/Users/Raihan.fadhlullah/Pictures/result.xml";

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(xmlResult);
            System.out.println("XML saved to file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateXmlFromMap() {
        StringBuilder xmlBuilder = new StringBuilder();
        Integer tigak = 3000;
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuilder.append("<archive version=\"1.0\" creator=\"trio\" creator_version=\"2.11.2 (Build 15185)\">\n");
        xmlBuilder.append("<vdom>\n");
        xmlBuilder.append("<entry name=\"storage\">\n");
        xmlBuilder.append("<entry name=\"show\">\n");
        xmlBuilder.append("<entry name=\"{40F77AF2-AC11-4596-A12B-CA0F3BCE3E8C}\">\n");

        for (int i = 1; i <= 3; i++) {
            Integer elementName = tigak + 1;
    
            xmlBuilder.append("<element loaded=\"0.00\" description=\"\" available=\"1.00\" layer=\"\" templatedescription=\"\" take_count=\"0\" showautodescription=\"True\" name=\"" + elementName + "\">\n");
            xmlBuilder.append("<ref name=\"master_template\">/storage/shows/{40F77AF2-AC11-4596-A12B-CA0F3BCE3E8C}/mastertemplates/" + elementName + "</ref>\n").append("\n");
            xmlBuilder.append("<entry name=\"data\">\n").append("\n");
            xmlBuilder.append("<entry name=\"01\">location [ max 65 char ]</entry>\n").append("\n");
            xmlBuilder.append("<entry name=\"02\">news info [ max 41 char ]</entry>\n").append("\n");
            xmlBuilder.append("</entry>\n");
            xmlBuilder.append("</element>");
        }
        xmlBuilder.append("</entry>\n");
        xmlBuilder.append("</entry>\n");
        xmlBuilder.append("</entry>\n");
        xmlBuilder.append("</vdom>\n");
        xmlBuilder.append("</archive>\n");

        return xmlBuilder.toString();
    }
}
