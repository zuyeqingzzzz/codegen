package com.zyq.velocity.service;

import com.zyq.velocity.domain.GenTable;
import lombok.extern.slf4j.Slf4j;
import com.zyq.velocity.domain.GenTableColumn;
import com.zyq.velocity.util.VelocityUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YiQing
 * @version 1.0
 * 查pg数据库的表结构
 */
@Slf4j
public class QueryPgSQL extends AbstractQueryDataSource{


    @Override
    public GenTable queryDb(String tableName) {

        GenTable table = new GenTable();

        Connection connection = null;
        Statement statement = null;
        ResultSet tableResult = null;
        ResultSet columnResult = null;


        String columnSql = "SELECT table_name, column_name, column_default, udt_name, description, ordinal_position\n" +
                "FROM information_schema.columns c \n" +
                "LEFT JOIN pg_catalog.pg_description pgd ON (pgd.objsubid = c.ordinal_position AND c.table_name = '" + tableName + "" + "')\n" +
                "\t\tleft join pg_catalog.pg_statio_all_tables as st on (pgd.objoid = st.relid and st.schemaname = 'stdwk')\n" +
                "WHERE table_schema = 'stdwk' AND table_name = '" + tableName + "" + "' AND description <> ''\t and st.relname = '" + tableName + "" + "'\n" +
                "UNION ALL\n" +
                "SELECT table_name, column_name, column_default, udt_name, '' AS description, ordinal_position\n" +
                "FROM information_schema.columns c\n" +
                "WHERE table_schema = 'stdwk' AND table_name = '" + tableName + "" + "' AND (column_name, table_name) NOT IN (\n" +
                "    SELECT column_name, table_name\n" +
                "    FROM information_schema.columns c\n" +
                "    LEFT JOIN pg_catalog.pg_description pgd ON (pgd.objsubid = c.ordinal_position AND c.table_name = '" + tableName + "" + "')\n" +
                "\t\tleft join pg_catalog.pg_statio_all_tables as st on (pgd.objoid = st.relid and st.schemaname = 'stdwk')\n" +
                "    WHERE table_schema = 'stdwk' AND table_name = '" + tableName + "" + "'  AND description <> ''\n" +
                "\t\tand st.relname = '" + tableName + "" + "'\n" +
                "\t\t\n" +
                ") ORDER BY ordinal_position;";

        String tableCommentSql = "SELECT obj_description('" + tableName + "" + "'::regclass) AS comment\n" +
                "FROM pg_class\n" +
                "WHERE relname = '" + tableName + "" + "' order by oid desc limit 1;";



        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, userName, passWord);
            statement = connection.createStatement();

            // 表注释
            tableResult = statement.executeQuery(tableCommentSql);
            table.setTableName(tableName);
            while (tableResult.next()) {
                table.setTableComment(tableResult.getString("comment"));
            }

            // 字段
            List<GenTableColumn> columns = new ArrayList<>();
            columnResult = statement.executeQuery(columnSql);
            while (columnResult.next()) {

                GenTableColumn column = new GenTableColumn();
                String columnDefault = columnResult.getString("column_default");
                String columnName = columnResult.getString("column_name");
                String udtName = columnResult.getString("udt_name");
                String description = columnResult.getString("description");

                column.setColumnName(columnName);
                column.setColumnType(udtName);
                column.setColumnComment(null == description ? "" : description);

                // 确认主键
                if (null != columnDefault && columnDefault.startsWith("nextval")) {
                    column.setIsPk("1");
                    table.setPkColumn(column);
                }
                columns.add(column);
            }

            table.setColumns(columns);

        } catch (SQLException | ClassNotFoundException e) {
            log.error(e.getMessage());
        }
        finally {
            try {
                if(null != connection) {
                    connection.close();
                }
                if (null != statement) {
                    statement.close();
                }
                if(null != tableResult) {
                    tableResult.close();
                }
                if(null != columnResult) {
                    columnResult.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        // 设置java类型
        List<GenTableColumn> columns = table.getColumns();
        columns.forEach(e -> {
            VelocityUtils.initColumnField(e, table);
        });
        return table;
    }

}
