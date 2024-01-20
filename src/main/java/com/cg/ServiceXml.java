package com.cg;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Controller
public class ServiceXml {

    @Autowired
    private Environment env;
    
    private static class DataEntry {
        private String nama;
        private String key;
        private String[] data;

        public DataEntry(String nama, String key, String[] data) {
            this.nama = nama;
            this.key = key;
            this.data = data;
        }

        public String getNama() {
            return nama;
        }

        public String getKey() {
            return key;
        }

        public String[] getData() {
            return data;
        }
    }

    private List<DataEntry> dataMap = new ArrayList<>();

    @GetMapping("/")
    public String index(Model model) {
        dataMap.clear();
        model.addAttribute("dataMap",dataMap);
        return "index";
    }

    @PostMapping("/saveData")
    public ResponseEntity<Object> saveData( @RequestParam("nama") String nama,
            @RequestParam("key") String key,
            @RequestParam("data") String[] data) {

        Map<String, Object> responseData = new HashMap<>();
        dataMap.add(new DataEntry(nama, key, data));
        // saveXmlToFile(dataMap);
        responseData.put("data", dataMap);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @RequestMapping("/getData")
    public ResponseEntity<Map> getData() {
        Map data = new HashMap<>();
        data.put("data", dataMap);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/saveXml")
    public ResponseEntity<Map> saveXml() {
        Map data = new HashMap<>();
        saveXmlToFile(dataMap);
        data.put("icon", "success");
        data.put("message", "File XML Berhasil Dibuat.");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    private void saveXmlToFile(List<DataEntry> dataMap2) {
        String xmlResult = generateXmlFromMap(dataMap2);
        // String filePath = "C:/Users/fadhl/OneDrive/Documents/result.xml";
        String nm = dataMap2.get(0).getNama();
        String nama = nm.replace(" ", "_");
        try (FileWriter fileWriter = new FileWriter(env.getProperty("URL.FILE_IN") + nama + ".xml")) {
            fileWriter.write(xmlResult);
            System.out.println("XML saved to file: " + env.getProperty("URL.FILE_IN") + nama + ".xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateXmlFromMap(List<DataEntry> dataMap2) {
        StringBuilder xmlBuilder = new StringBuilder();
        Integer tigak = 3000;

        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuilder.append("<archive version=\"1.0\" creator=\"trio\" creator_version=\"2.11.2 (Build 15185)\">\n");
        xmlBuilder.append("<vdom>\n");
        xmlBuilder.append("<entry name=\"storage\">\n");
        xmlBuilder.append("<entry name=\"show\">\n");
        xmlBuilder.append("<entry name=\"{40F77AF2-AC11-4596-A12B-CA0F3BCE3E8C}\">\n");

        for (int i = 1; i <= dataMap2.size() ; i++) {

            Integer elementName = tigak + i;
            String kunci = dataMap2.get(i - 1).getKey();
            String[] data = dataMap2.get(i - 1).getData();
            xmlBuilder.append("<element loaded=\"0.00\" description=\"\" available=\"1.00\" layer=\"\" templatedescription=\"\" take_count=\"0\" showautodescription=\"True\" name=\"" + elementName + "\">\n");
            xmlBuilder.append("<ref name=\"master_template\">/storage/shows/{40F77AF2-AC11-4596-A12B-CA0F3BCE3E8C}/mastertemplates/" + kunci + "</ref>\n").append("\n");
            xmlBuilder.append("<entry name=\"data\">\n").append("\n");
            for (int j = 1; j <= data.length ; j++) {
                xmlBuilder.append("<entry name=\"" + 0 + j + "\">" + data[j - 1] + "</entry>\n").append("\n");
            }
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
