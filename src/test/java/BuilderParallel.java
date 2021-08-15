
import java.util.*;

import org.apache.storm.StormSubmitter;
import org.apache.storm.lambda.SerializableBiConsumer;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;

public class BuilderParallel {
    public static double e;
    public static String filename = null;
    public static Double threshold = -1.0;
    public static void main(String[] args) throws Exception{

        Config conf = new Config();

        conf.setDebug(true);
        TopologyBuilder builder = new TopologyBuilder();
        if(args.length > 2){
            threshold = Double.parseDouble(args[2]);
        }
        e = Double.parseDouble(args[1]);
        builder.setSpout("twitter-spout", new TwitterSpout());
        builder.setBolt("Hashtag-bolt", (SerializableBiConsumer<Tuple, BasicOutputCollector>) new HashTagBolt());
        LossyCountingParallel ls1 = new LossyCountingParallel(e, threshold);
        LossyCountingParallel ls2 = new LossyCountingParallel(e, threshold);
       // builder.setBolt("HashTag-Lossy", ls1,4).setNumTasks(8).fieldsGrouping("Hashtag-bolt", new Fields("hashTag"));
        //builder.setBolt("Report-HashTag-Bolt", new ReportBolt()).globalGrouping("HashTag-Lossy");

        StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        Thread.sleep(10000);

    }
}
