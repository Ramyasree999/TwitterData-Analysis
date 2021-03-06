//package PACKAGE_NAME.TwitterSpout.java;

import org.apache.storm.topology.base.BaseRichSpout;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import org.apache.storm.Config;
import org.apache.storm.spout.SpoutOutputCollector;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import org.apache.storm.utils.Utils;


@SuppressWarnings("serial")
public class TwitterSpout extends BaseRichSpout
{
    TwitterStream twitterStream;
    LinkedBlockingQueue<Status> queue = null;
    SpoutOutputCollector _collector;
    String[] keyWords;
    String consumerKey = "credential";
    String consumerSecret = "credential";
    String accessToken = "credential";
    String accessTokenSecret = "credential";

 @Override
 public void open(Map conf, TopologyContext context,
                  SpoutOutputCollector collector) {
     queue = new LinkedBlockingQueue<Status>(1000);
     _collector = collector;
     StatusListener listener = new StatusListener() {
         @Override
         public void onStatus(Status status) {
             queue.offer(status);
         }

         @Override
         public void onDeletionNotice(StatusDeletionNotice sdn) {}

         @Override
         public void onTrackLimitationNotice(int i) {}

         @Override
         public void onScrubGeo(long l, long l1) {}

         @Override
         public void onException(Exception ex) {}

         @Override
         public void onStallWarning(StallWarning arg0) {
             // TODO Auto-generated method stub
         }
     };

     ConfigurationBuilder cb = new ConfigurationBuilder();

     cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret).setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret);

     twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
     twitterStream.addListener(listener);
     twitterStream.setOAuthConsumer(consumerKey, consumerSecret);
     AccessToken token = new AccessToken(accessToken, accessTokenSecret);
     twitterStream.setOAuthAccessToken(token);

     if (keyWords.length == 0) {
         twitterStream.sample();
     }else {
         FilterQuery query = new FilterQuery().track(keyWords);
         twitterStream.filter(query);
     }
 }

    @Override
    public void nextTuple() {
        Status ret = queue.poll();

        if (ret == null) {
            Utils.sleep(50);
        } else {
            _collector.emit(new Values(ret));
        }
    }

    @Override
    public void close() {
        twitterStream.shutdown();
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config ret = new Config();
        ret.setMaxTaskParallelism(1);
        return ret;
    }

    @Override
    public void ack(Object id) {}

    @Override
    public void fail(Object id) {}

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("tweet"));
    }
}
