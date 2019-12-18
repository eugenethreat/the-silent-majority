/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.themajoritytwitterbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.api.TweetsResources;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author eugene
 */
class MajorityTwitterBot {

    public MajorityTwitterBot() {
        /*
        generating the "bot" itself; oauth token and such
         */
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb = cbProperties(cb); //all the properties in their own method

        TwitterFactory tf = new TwitterFactory(cb.build());

        TweetsResources tr = tf.getInstance().tweets();
        /*
        with more verbosity...
        Twitter t = tf.getInstance();
        TweetsResources tr = t.tweets();
         */

        ArrayList<String> adjectives = wordListGenerator();
        //the arraylist that contains the list of words 
        timeToTweet(tr, adjectives);

        //System.out.println("success!");
    }

    private ConfigurationBuilder cbProperties(ConfigurationBuilder cb) {
        cb.setDebugEnabled(true);

        File api = new File("api.txt");
            //my key is NOT in a file called api.txt 
        try {
            BufferedReader rdr = new BufferedReader(new FileReader(api));
            String oack = rdr.readLine();
            String oacks = rdr.readLine();
            String oaat = rdr.readLine();
            String oaas = rdr.readLine();

            cb.setOAuthConsumerKey(oack);
            cb.setOAuthConsumerSecret(oacks);
            cb.setOAuthAccessToken(oaat);
            cb.setOAuthAccessTokenSecret(oaas);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MajorityTwitterBot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MajorityTwitterBot.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return cb;
    }

    private ArrayList<String> wordListGenerator() {

        File adjectivesFromFile = new File("wordlist.txt");
        Scanner scnr;
        ArrayList<String> adjectives = new ArrayList();

        try {
            scnr = new Scanner(adjectivesFromFile);

            while (scnr.hasNext()) {
                adjectives.add(scnr.nextLine());
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MajorityTwitterBot.class.getName()).log(Level.SEVERE, null, ex);
        }

        return adjectives;
    }

    private void tweetWriter(TweetsResources tr, ArrayList<String> adjectives) {
        try {
            String[] template = {"the ", "", " majority"};

            Random someRandom = new Random();
            int randInt = someRandom.nextInt(adjectives.size() - 1);
            //-1 ensures that the fetched int will never exceed the size of the wordlist. 
            template[1] = adjectives.get(randInt);

            String tweet = template[0] + template[1] + template[2];
            tr.updateStatus(tweet);

            System.out.println("new tweet posted");

        } catch (TwitterException ex) {
            Logger.getLogger(MajorityTwitterBot.class.getName()).log(Level.SEVERE, null, ex);
        }
        //from http://twitter4j.org/javadoc/twitter4j/api/TweetsResources.html 
    }

    private void timeToTweet(TweetsResources tr, ArrayList<String> adjectives) {

        Runnable runningTweetWriter = new Runnable() {
            @Override
            public void run() {
                tweetWriter(tr, adjectives);
            }
        };

        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.scheduleAtFixedRate(runningTweetWriter, 0, 1, TimeUnit.HOURS);
        //sends a new tweet every hour

        //https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledExecutorService.html
        //https://stackoverflow.com/questions/33073671/how-to-execute-a-method-every-minute
        /*
        need to figure out how to keep this running forever so it tweets repeatedly 
         */
    }
}
