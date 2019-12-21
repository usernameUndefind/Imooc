package com.imooc.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CoverPath {

    private String ffmpegEXE;

    public CoverPath(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convetor (String videoInput, String  coverOut) throws Exception{
//        String commond = "ffmpeg.exe -i aaa.mp4 =i bgm.mp3 -t 7 -y 新的视频.mp4";
//        ffmpeg.exe -ss 00:00:01 -y -i F:\aa.mp4 -vframes 1 new.jpg
        List<String> commands = new ArrayList<>();
        commands.add(ffmpegEXE);
        commands.add("-ss");
        commands.add("00:00:02");

        commands.add("-y");
        commands.add("-i");

        commands.add(videoInput);
        commands.add("-vframes");
        commands.add("1");
        commands.add(coverOut);

//        for (String command : commands) {
//            System.out.print(command + " ");
//        }

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        Process process = processBuilder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String str = null;
        while ( (str = br.readLine()) != null) {
        }

        if (br != null) {
            br.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }

    }

    public static void main(String[] args) {
        String ffempgPath = "F:\\MySoftware\\ffmpeg\\ffmpeg\\bin\\ffmpeg.exe";
        String videoInput = "F:\\aa.mp4";
        String coverOut = "F:\\new.jpg";
        try {
            new CoverPath(ffempgPath).convetor(videoInput,coverOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
