package com.imooc.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoMp3 {

    private String ffmpegEXE;

    public MergeVideoMp3(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convetor (String videoInput, String mp3InputPath, double seconds, String videoOutput) throws Exception{
//        String commond = "ffmpeg.exe -i aaa.mp4 =i bgm.mp3 -t 7 -y 新的视频.mp4";

        List<String> commands = new ArrayList<>();
        commands.add(ffmpegEXE);
        commands.add("-i");
        commands.add(videoInput);

        commands.add("-i");
        commands.add(mp3InputPath);

        commands.add("-t");
        commands.add(String.valueOf(seconds));
        commands.add("-y");
        commands.add(videoOutput);

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
        String videoOutput = "F:\\test\\ccc.mp4";
        String mp3Path = "D:\\imooc_video_dev\\bgm\\一路向北指弹.mp3";
        try {
            new MergeVideoMp3(ffempgPath).convetor(videoInput,mp3Path,10.0,videoOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
