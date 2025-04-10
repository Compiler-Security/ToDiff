package org.generator.tools.diffTopo;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;
import org.generator.lib.generator.driver.generate;
import org.generator.lib.topo.driver.topo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class main {
    /**
     *
     * args of topo
     *      areaCount
     *      mxDegree
     *      abrRatio
     *
     * args of genAttri
     *      fastConvergence
     *      other ospf args(ospf, ospf daemon, ospf interface, ospf sumEntry
     *
     * args of ospf equal core
     *      expandRatio
     *      irrOpRatio
     *      mutateRatio
     *
     *  args of phy equal core
     *      ratio
     *      maxRound
     *
     *  args of testing
     *      router_count
     *      max_step
     *      max_step_time
     *      round_num
     */
    static Options getOptions(){
        //FIXME currently we support args of testing
        var options = new Options();
        //testFileDir
        var testFileDir = Option.builder("testFileDir")
                .argName("dirPath")
                .hasArg()
                .desc("generate File's path")
                .build();
        options.addOption(testFileDir);

        options.addOption("routerCount", true, "router's num");
        options.addOption("maxStep", true, "max test step");
        options.addOption("maxStepTime", true, "max wait time of one step");
        options.addOption("roundNum", true, "round num");
        options.addOption("protocol", true, "protocol to generate [ospf,rip]");
        options.addOption("maxDegree", true, "max interface per router");
        return options;
    }
    public static void main(String[] args) {
        //parse and set Args
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        int routerCount = 3,maxStep = 3,maxStepTime = 10, roundNum = 2;
        String dirPath = "";
        try {
            cmd = parser.parse(getOptions(), args);
            dirPath = cmd.getOptionValue("testFileDir");
            if (cmd.hasOption("routerCount")){
                routerCount = Integer.parseInt(cmd.getOptionValue("routerCount"));
            }
            if (cmd.hasOption("maxStep")){
                maxStep = Integer.parseInt(cmd.getOptionValue("maxStep"));
            }
            if (cmd.hasOption("maxStepTime")){
                maxStepTime = Integer.parseInt(cmd.getOptionValue("maxStepTime"));
            }
            if (cmd.hasOption("roundNum")){
                roundNum = Integer.parseInt(cmd.getOptionValue("roundNum"));
            }
            if (cmd.hasOption("mxDegree")){
                topo.mxDegree = Integer.parseInt(cmd.getOptionValue("maxDegree"));
            }
            //MULTI:
            if (cmd.hasOption("protocol")){
                switch (cmd.getOptionValue("protocol")){
                    case "ospf" -> {
                        generate.protocol = generate.Protocol.OSPF;
                    }
                    case "rip" -> {
                        generate.protocol = generate.Protocol.RIP;
                    }
                    case "isis" -> {
                        generate.protocol = generate.Protocol.ISIS;
                    }
                    case "openfabric" -> {
                        generate.protocol = generate.Protocol.OpenFabric;
                    }
                    default -> {
                        System.out.println("Unknown protocol: " + cmd.getOptionValue("protocol"));
                        System.exit(-1);
                    }
                }
            }else{
                System.out.println("Need to set protocol");
                System.exit(-1);
            }
        } catch (ParseException e) {
            System.out.println("Parsing failed. Reason: " + e.getMessage());
            formatter.printHelp("generator", getOptions());
            System.exit(1);
        }

        //generate testFile
        var diff = new diffTopo();
        var res = diff.gen(routerCount, maxStep, maxStepTime, roundNum);

        //write testFile
        var file_name = res.get("conf_name").asText();
        var full_path = Paths.get(dirPath, file_name + ".json").toString();
        var writer = new ObjectMapper().writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(new File(full_path), res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
