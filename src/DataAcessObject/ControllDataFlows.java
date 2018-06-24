/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataAcessObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import xephinh.Interface;

/**
 *
 * @author MTC
 */
public class ControllDataFlows {

    DataAccess dao;
    Connection con;
    DefaultTableModel dftm = new DefaultTableModel();
    Interface puzzle;

    public ControllDataFlows(Interface puzzle) {
        dao = new DataAccess(new DBContext());
        con = null;
        this.puzzle = puzzle;
    }

    public void control() {
        loadData();
    }

    private void loadData() {
        try {
            String sql = "SELECT TOP 5 [Name Player], [Size Map], [Played Time (s)], Clicked, Date FROM"
                    + " dbo.RankTable ORDER BY [Played Time (s)];";
            ResultSet rs = dao.getPS(sql).executeQuery();
            Object[] title = {"Name Player", "Size Map", "Played Time (s)", "Clicked", "Date"};
            dftm.setColumnIdentifiers(title);
            dftm.setRowCount(0);
            while (rs.next()) {
                Vector vec = new Vector();
                vec.add(rs.getString(1));
                vec.add(rs.getString(2));
                vec.add(rs.getInt(3));
                vec.add(rs.getInt(4));
                vec.add(rs.getDate(5));
                dftm.addRow(vec);
            }
            puzzle.getTblRank().setModel(dftm);
        } catch (Exception ex) {
            System.out.println("Error while connect to Db " + ex.getMessage());
        }
    }

    public void update(String name, int rows, int cols, int timeCount, int clicked) {
        try {
            PreparedStatement ps;
            String sql = "INSERT INTO dbo.RankTable ([Name Player], [Size Map], [Played Time (s)], Clicked, Date)\n"
                    + "VALUES  ( ?, ?, ?, ?, GETDATE())";
            ps = dao.getPS(sql);
            ps.setString(1, name);
            ps.setString(2, rows + "x" + cols);
            ps.setInt(3, timeCount);
            ps.setInt(4, clicked);
            ps.executeUpdate();
            loadData();
        } catch (Exception ex) {

        }
    }

}
