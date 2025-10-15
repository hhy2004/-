package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.QueryService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class WebServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 主页
        server.createContext("/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                String response = getIndexPage();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes("UTF-8"));
                os.close();
            }
        });

        // 电影查询API
        server.createContext("/movie", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.startsWith("id=")) {
                    String movieId = query.substring(3);
                    Map<String, String> movie = QueryService.getMovieById(movieId);
                    String response = getMoviePage(movie, movieId);
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes("UTF-8"));
                    os.close();
                }
            }
        });

        // 用户评分查询API - 新增
        server.createContext("/user", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                String query = exchange.getRequestURI().getQuery();
                if (query != null && query.startsWith("id=")) {
                    String userId = query.substring(3);
                    List<Map<String, String>> ratings = QueryService.getUserRatings(userId);
                    String response = getUserRatingsPage(ratings, userId);
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes("UTF-8"));
                    os.close();
                }
            }
        });

        server.start();
        System.out.println("服务器启动在端口8080");
        System.out.println("访问 http://localhost:8080");
    }

    private static String getIndexPage() {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'> <title>电影数据查询</title>" +
                "<style>" +
                "body{font-family:Arial;margin:40px;} " +
                ".form-group{margin:20px 0;} " +
                "input{padding:8px;width:200px;} " +
                "button{padding:8px 16px;background:#007cba;color:white;border:none;margin:5px;}" +
                ".container{display:flex;gap:40px;}" +
                ".query-box{border:1px solid #ddd;padding:20px;border-radius:5px;}" +
                "</style>" +
                "</head><body>" +
                "<h1>MovieLens数据查询系统</h1>" +
                "<div class='container'>" +
                "<div class='query-box'>" +
                "<h3>查询电影信息</h3>" +
                "<form action='/movie' method='get'>" +
                "<input type='text' name='id' placeholder='输入电影ID' required>" +
                "<button type='submit'>查询电影</button>" +
                "</form>" +
                "</div>" +
                "<div class='query-box'>" +
                "<h3>查询用户评分</h3>" +
                "<form action='/user' method='get'>" +
                "<input type='text' name='id' placeholder='输入用户ID' required>" +
                "<button type='submit'>查询评分</button>" +
                "</form>" +
                "</div>" +
                "</div>" +
                "</body></html>";
    }

    private static String getMoviePage(Map<String, String> movie, String movieId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>电影详情</title>");
        sb.append("<style>body{font-family:Arial;margin:40px;} .back{color:#007cba;text-decoration:none;} table{border-collapse:collapse;width:100%;} td,th{border:1px solid #ddd;padding:8px;}</style>");
        sb.append("</head><body>");
        sb.append("<a href='/' class='back'>← 返回首页</a>");
        sb.append("<h1>电影详情</h1>");

        if (!movie.isEmpty()) {
            sb.append("<table>");
            sb.append("<tr><th></th><th></th></tr>");
            for (Map.Entry<String, String> entry : movie.entrySet()) {
                sb.append("<tr><td><strong>").append(entry.getKey()).append("</strong></td>");
                sb.append("<td>").append(entry.getValue()).append("</td></tr>");
            }
            sb.append("</table>");
        } else {
            sb.append("<p>未找到电影ID: ").append(movieId).append(" 的信息</p>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    // 新增：用户评分页面生成方法
    private static String getUserRatingsPage(List<Map<String, String>> ratings, String userId) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>用户评分</title>");
        sb.append("<style>");
        sb.append("body{font-family:Arial;margin:40px;} ");
        sb.append(".back{color:#007cba;text-decoration:none;} ");
        sb.append("table{border-collapse:collapse;width:100%;margin-top:20px;} ");
        sb.append("td,th{border:1px solid #ddd;padding:12px;text-align:left;} ");
        sb.append("th{background-color:#f2f2f2;} ");
        sb.append("tr:nth-child(even){background-color:#f9f9f9;}");
        sb.append("</style>");
        sb.append("</head><body>");
        sb.append("<a href='/' class='back'>← 返回首页</a>");
        sb.append("<h1>用户评分记录</h1>");
        sb.append("<p><strong>用户ID: ").append(userId).append("</strong></p>");

        if (!ratings.isEmpty()) {
            sb.append("<table>");
            sb.append("<tr><th>电影ID</th><th>评分</th><th>时间戳</th></tr>");
            for (Map<String, String> rating : ratings) {
                sb.append("<tr>");
                sb.append("<td>").append(rating.get("movieId")).append("</td>");
                sb.append("<td>").append(rating.get("rating")).append("</td>");
                sb.append("<td>").append(rating.get("timestamp")).append("</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");
            sb.append("<p>共找到 ").append(ratings.size()).append(" 条评分记录</p>");
        } else {
            sb.append("<p>未找到用户ID: ").append(userId).append(" 的评分记录</p>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }
}