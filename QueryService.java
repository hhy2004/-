//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;

public class QueryService {
    public static Map<String, String> getMovieById(String movieId) {
        Map<String, String> movie = new HashMap();

        try (Table table = HBaseUtil.getConnection().getTable(TableName.valueOf("movies"))) {
            Get get = new Get(Bytes.toBytes(movieId));
            Result result = table.get(get);
            if (!result.isEmpty()) {
                movie.put("movieId", movieId);
                byte[] title = result.getValue(Bytes.toBytes("info"), Bytes.toBytes("title"));
                byte[] genres = result.getValue(Bytes.toBytes("info"), Bytes.toBytes("genres"));
                if (title != null) {
                    movie.put("title", Bytes.toString(title));
                }

                if (genres != null) {
                    movie.put("genres", Bytes.toString(genres));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movie;
    }
    public static List<Map<String, String>> getUserRatings(String userId) {
        List<Map<String, String>> ratings = new ArrayList<>();
        try (Table table = HBaseUtil.getConnection().getTable(TableName.valueOf("ratings"))) {
            Scan scan = new Scan();
            scan.setFilter(new PrefixFilter(Bytes.toBytes(userId + "_")));
            try (ResultScanner scanner = table.getScanner(scan)) {
                for (Result result : scanner) {
                    Map<String, String> rating = new HashMap<>();
                    String movieId = Bytes.toString(result.getValue(Bytes.toBytes("rating_info"), Bytes.toBytes("movieId")));
                    String ratingValue = Bytes.toString(result.getValue(Bytes.toBytes("rating_info"), Bytes.toBytes("rating")));
                    String timestamp = Bytes.toString(result.getValue(Bytes.toBytes("rating_info"), Bytes.toBytes("timestamp")));

                    rating.put("movieId", movieId != null ? movieId : "");
                    rating.put("rating", ratingValue != null ? ratingValue : "");
                    rating.put("timestamp", timestamp != null ? timestamp : "");
                    ratings.add(rating);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    public static void main(String[] args) {
        Map<String, String> movie = getMovieById("1");
        System.out.println("电影信息：" + String.valueOf(movie));
        getUserRatings("1");
        HBaseUtil.closeConnection();
    }
}
