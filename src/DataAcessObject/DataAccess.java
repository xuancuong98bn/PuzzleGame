/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataAcessObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author MTC
 */
public class DataAccess {
    DBContext db;
        Connection con;

        public DataAccess() {
        }

        public DataAccess(DBContext db) {
            this.db = db;
            try {
                this.con = db.getConnection();
            } catch (Exception ex) {
                System.out.println("Can't conect");
            }
        }

        public ResultSet getData(String sql) {
            ResultSet rs = null;
            try {
                PreparedStatement ps = con.prepareStatement(sql);
                rs = ps.executeQuery();
            } catch (SQLException ex) {
                System.out.println("Error");
            }
            return rs;
        }

        public PreparedStatement getPS(String sql) throws SQLException {
            return con.prepareStatement(sql);
        }
}
