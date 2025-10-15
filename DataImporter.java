package org.example;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class DataImporter {
    //导入电影数据
    public static void importMovies(String csvFile) { try (Table table = HBaseUtil.getConnection().getTable(TableName.valueOf("movies"));
    BufferedReader br = new BufferedReader(new FileReader(csvFile))) { String line; br.readLine(); // 跳过标题行
         List<Put> puts = new ArrayList<>();
 while ((line = br.readLine()) != null) { String[] fields = line.split(","); if (fields.length >= 3) { String movieId = fields[0]; String title = fields[1]; String genres = fields[2];
 Put put = new Put(Bytes.toBytes(movieId)); put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("title"), Bytes.toBytes(title));
 put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("genres"), Bytes.toBytes(genres));
 puts.add(put); if (puts.size() >= 100) { table.put(puts); puts.clear();
 System.out.println("已导入100条电影数据"); } } } if (!puts.isEmpty()) { table.put(puts); }
 System.out.println("电影数据导入完成"); }
 catch (IOException e) { e.printStackTrace(); } }
 //导入评分数据
 public static void importRatings(String csvFile) { try (Table table = HBaseUtil.getConnection().getTable(TableName.valueOf("ratings"));
 BufferedReader br = new BufferedReader(new FileReader(csvFile))) { String line; br.readLine(); //
 //跳过标题行
     List<Put> puts = new ArrayList<>(); while ((line = br.readLine()) != null) { String[] fields = line.split(",");
 if (fields.length >= 4) { String userId = fields[0];
 String movieId = fields[1]; String rating = fields[2];
 String timestamp = fields[3];
 String rowKey = userId + "_" + movieId;
 Put put = new Put(Bytes.toBytes(rowKey));
 put.addColumn(Bytes.toBytes("rating_info"), Bytes.toBytes("userId"), Bytes.toBytes(userId));
 put.addColumn(Bytes.toBytes("rating_info"), Bytes.toBytes("movieId"), Bytes.toBytes(movieId));
 put.addColumn(Bytes.toBytes("rating_info"), Bytes.toBytes("rating"), Bytes.toBytes(rating));
 put.addColumn(Bytes.toBytes("rating_info"), Bytes.toBytes("timestamp"), Bytes.toBytes(timestamp));
 puts.add(put);
 if (puts.size() >= 100) { table.put(puts); puts.clear(); System.out.println("已导入100条评分数据"); } } }
 if (!puts.isEmpty()) { table.put(puts); } System.out.println("评分数据导入完成"); } catch (IOException e) { e.printStackTrace(); } }
 public static void main(String[] args) {  importMovies("data/movies.csv");
 importRatings("data/ratings.csv"); HBaseUtil.closeConnection(); } }