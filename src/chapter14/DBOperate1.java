package chapter14;

import java.sql.*;

/**
 * description:
 * author: Leslie Leung
 * date: 2020/12/7
 */
public class DBOperate1 {
    public static void main(String[] args) throws SQLException {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/STUDENTDB2?characterEncoding=utf8&useSSL=false&serverTimezone=Hongkong", "root", "password");

            String sql = "select NO,NAME,AGE,CLASS from students where name like ? and age=?";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setObject(1, "小%");
            stmt.setObject(2,23);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
            {
                System.out.print(rs.getString(1)+"\t");
                System.out.print(rs.getString(2)+"\t");
                System.out.print(rs.getInt(3)+"\t");
                System.out.print(rs.getString(4)+"\n");
            }
            System.out.println("------------------------------------");

            String sql1 = "insert into STUDENTS(NO,NAME,AGE,CLASS) values(?,?,?,?)";
            stmt = connection.prepareStatement(sql1);
            stmt.setObject(1,"2018100xxxx");
            stmt.setObject(2,"xxx");
            stmt.setObject(3, 21);
            stmt.setObject(4,"软件工程1801");

            stmt.executeUpdate();
            sql = "select NO,NAME,AGE,CLASS from STUDENTS ";
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next())
            {
                System.out.print(rs.getString(1)+"\t");
                System.out.print(rs.getString(2)+"\t");
                System.out.print(rs.getInt(3)+"\t");
                System.out.print(rs.getString(4)+"\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
}
