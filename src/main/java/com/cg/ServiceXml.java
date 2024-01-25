package com.cg;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;

import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ServiceXml {

    @Autowired
    private Environment env;

    private Map<String, List<DataEntry>> userSessionDataMap = new HashMap<>();

    private static class DataEntry {
        private String nama;
        private String channel;
        private String key;
        private String[] data;

        public DataEntry(String nama, String channel, String key, String[] data) {
            this.nama = nama;
            this.channel = channel;
            this.key = key;
            this.data = data;
        }

        public String getNama() {
            return nama;
        }

        public String getChannel() {
            return channel;
        }

        public String getKey() {
            return key;
        }

        public String[] getData() {
            return data;
        }
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        userSessionDataMap.clear();
        if (userId == null) {
            userId = "user" + System.currentTimeMillis();
            session.setAttribute("userId", userId);
            userSessionDataMap.put(userId, new ArrayList<>());
        }

        List<DataEntry> dataMap = userSessionDataMap.get(userId);
        model.addAttribute("dataMap", dataMap);
        return "index";
    }

    @PostMapping("/saveData")
    public ResponseEntity<Object> saveData(@RequestParam("nama") String nama,
                                           @RequestParam("channel") String channel,
                                           @RequestParam("key") String key,
                                           @RequestParam("data") String[] data,
                                           HttpSession session) {

        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            userId = "user" + System.currentTimeMillis();
            session.setAttribute("userId", userId);
            userSessionDataMap.put(userId, new ArrayList<>());
        }

        Map<String, Object> responseData = new HashMap<>();
        userSessionDataMap.computeIfAbsent(userId, k -> new ArrayList<>())
                .add(new DataEntry(nama, channel, key, data));

        responseData.put("data", userSessionDataMap.get(userId));
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @RequestMapping("/getData")
    public ResponseEntity<Map> getData(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            userId = "user" + System.currentTimeMillis();
            session.setAttribute("userId", userId);
            userSessionDataMap.put(userId, new ArrayList<>());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("data", userSessionDataMap.get(userId));
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @RequestMapping(path = "/saveXml", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> downloadLargeFile(HttpSession session) throws IOException {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            userId = "user" + System.currentTimeMillis();
            session.setAttribute("userId", userId);
            userSessionDataMap.put(userId, new ArrayList<>());
        }

        List<DataEntry> dataMap = userSessionDataMap.get(userId);
        final HttpHeaders httpHeaders = new HttpHeaders();
        String xmlResult = generateXmlFromMap(dataMap);
        String nm = dataMap.get(0).getNama();
        String nama = nm.replace(" ", "_");

        try (FileWriter fileWriter = new FileWriter(env.getProperty("URL.FILE_IN") +
                nama + ".xml")) {
            fileWriter.write(xmlResult);
            System.out.println("XML saved to file: " + env.getProperty("URL.FILE_IN") +
                    nama + ".xml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8))) {
            writer.write(xmlResult);
        }
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nama + ".xml\"");
        ByteArrayResource byteArrayResource = new ByteArrayResource(byteArrayOutputStream.toByteArray());
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArrayResource);
    }

    private String generateXmlFromMap(List<DataEntry> dataMap2) {
        StringBuilder xmlBuilder = new StringBuilder();
        Integer tigak = 3000;
        String cnl = dataMap2.get(0).getChannel();
        String btv = "{156ACC91-A335-481B-AD9C-40353AC0A827}";
        String idtv = "{156ACC91-A335-481B-AD9C-40353AC0A827}";

        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuilder.append("<archive version=\"1.0\" creator=\"trio\" creator_version=\"2.11.2 (Build 15185)\">\n");
        xmlBuilder.append("<vdom>\n");
        xmlBuilder.append("<entry name=\"storage\">\n");
        xmlBuilder.append("<entry name=\"show\">\n");
        if (cnl.equals("BTV")) {
            xmlBuilder.append("<entry name=\"" + btv + "\">\n");
        } else {
            xmlBuilder.append("<entry name=\"" + idtv + "\">\n");
        }

        for (int i = 1; i <= dataMap2.size(); i++) {

            Integer elementName = tigak + i;
            String kunci = dataMap2.get(i - 1).getKey();
            String[] data = dataMap2.get(i - 1).getData();
            xmlBuilder.append(
                    "<element loaded=\"0.00\" description=\"\" available=\"1.00\" layer=\"\" templatedescription=\"\" take_count=\"0\" showautodescription=\"True\" name=\""
                            + elementName + "\">\n");
            if (cnl.equals("BTV")) {
                xmlBuilder.append(
                        "<ref name=\"master_template\">/storage/shows/" + btv + "/mastertemplates/"
                                + kunci + "</ref>\n")
                        .append("\n");
            } else {
                xmlBuilder.append(
                        "<ref name=\"master_template\">/storage/shows/" + idtv + "/mastertemplates/"
                                + kunci + "</ref>\n")
                        .append("\n");
            }
            xmlBuilder.append("<entry name=\"data\">\n").append("\n");
            for (int j = 1; j <= data.length; j++) {
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
