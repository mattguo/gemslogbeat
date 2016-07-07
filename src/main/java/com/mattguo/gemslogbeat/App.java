package com.mattguo.gemslogbeat;

import org.apache.commons.lang3.time.StopWatch;

import com.mattguo.gemslogbeat.config.Cfg;

public class App {

    public static void main(String[] args) throws InterruptedException {

//        Pattern segmentsPattern = Pattern.compile(Cfg.one().patterns().segment());
//        String sampleLog = "2016-06-30T16:56:43.846-0400  CEF:1 | good | com.good.server.core.gd-core | 0.5.31 | INFO  | unknown | 5 | ID=54 THR=167772749-172179 CAT=GdAuthenticatorImpl              MSG=Authorized User: \\"UserId\\" = \\"dennis.a.maag@chase.com\\";  \\"AppId\\" = \\"com.good.gcs.g3\\";  \\"ContainerId\\" = \\"DA9B0F83-DB35-4F38-AF7B-3DF7CE79B69F\\";  \\"EnterpriseId\\" = \\"null\\";";
//        Matcher matcher = segmentsPattern.matcher(sampleLog);
//        if (matcher.find()) {
//            for(int i = 0; i < matcher.groupCount(); i++) {
//                System.out.println(matcher.group(i));
//            }
//        }
        LogFileReader fr = new LogFileReader();
        StopWatch sw = new StopWatch();
        sw.start();
        fr.readDir("F:\\logs\\FIRST-11590\\5-23-2016 - Gems-GP-GC-Client Logs\\GEMS Logs");
        System.out.println(Cfg.one().getLineHeader());
        System.out.println("Elapsed time: " + sw.toString());
    }

}
