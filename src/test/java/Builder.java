import java.util.*;

import org.apache.storm.StormSubmitter;
import org.apache.storm.lambda.SerializableBiConsumer;
import org.apache.storm.lambda.SerializableConsumer;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
public class Builder{
    public static double e;
    public static String filename = null;
    public static Double threshold = -1.0;
    public static void main(String[] args) throws Exception{

        Config conf = new Config();

        conf.setDebug(true);
        // conf.setNumWorkers(4);

        TopologyBuilder builder = new TopologyBuilder();
       // e = Double.parseDouble(args[1]);
        e = 0.05;
        if(args.length > 2){
            threshold = Double.parseDouble(args[2]);
        }
        builder.setSpout("twitter-spout", new TwitterSpout());

        builder.setBolt("Hashtag-bolt", new HashTagBolt()).shuffleGrouping("twitter-spout");
      // LossyCounting lc1 = new LossyCounting(e, threshold);
       // LossyCounting lc2 = new LossyCounting(e, threshold);
      // builder.setBolt("HashTag-Lossy", lc1).shuffleGrouping("Hashtag-bolt");
      //  builder.setBolt("Report-HashTag-Bolt", new ReportBolt()).globalGrouping("HashTag-Lossy");

       // StormSubmitter.submitTopology(args[0], conf, builder.createTopology());

        Thread.sleep(10000);

    }
}