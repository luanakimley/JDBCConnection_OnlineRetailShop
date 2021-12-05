import java.sql.*;

public class App
{
    static final String dbUrl = "jdbc:mysql://localhost:3306/online_retail_shop";
    static final String username = "root";
    static final String password = "";
    public static void main(String[] args)
    {

        try (Connection connection = DriverManager.getConnection(dbUrl, username, password);)
        {
            System.out.println("Connected!");
            Statement stmt = connection.createStatement();

            // Show user name, user county, and user credit card expiry date for users living in Louth
            System.out.println("Show user name, user county, and user credit card expiry date for users living in Louth");
            ResultSet rs = stmt.executeQuery("SELECT u.user_name AS \"User Name\", a.county AS \"County\", p.credit_card_expiry_date AS \"Credit Card Expiry Date\"\n" +
                    "FROM users u, shipping_address a, payment_details p, user_address_payment uap\n" +
                    "WHERE u.user_id = uap.user_id AND uap.address_id = a.address_id AND uap.payment_id = p.payment_id\n" +
                    "AND a.county LIKE \"Louth\";");
            System.out.printf("%-20s%15s%37s\n", "User Name", "County", "Credit Card Expiry Date");
            System.out.println("=========================    ================    =======================");
            while (rs.next())
            {
                System.out.printf("%-19s%15s%20s\n", rs.getString(1), rs.getString(2), rs.getString(3));
            }


            // Update table since shoes price are 20% off for Black Friday and show changes
            System.out.println("\nShoes price before discount");
            String update_test_command = "SELECT product_id, product_name, product_price FROM products WHERE category_id = (SELECT category_id FROM category WHERE category_name LIKE \"Shoes\");";
            rs = stmt.executeQuery(update_test_command);
            System.out.printf("%-19s%-35s%-20s\n", "Product ID", "Product Name", "Product Price");
            System.out.println("===============    ===============================    =============");
            while (rs.next())
            {
                System.out.printf("%-19s%-35s%-30s\n", rs.getString(1), rs.getString(2), rs.getDouble(3));
            }
            // Update price
            stmt.executeUpdate("UPDATE products \n" +
                    "SET product_price = product_price * 0.8\n" +
                    "WHERE category_id = (SELECT category_id FROM category WHERE category_name LIKE \"Shoes\");");

            System.out.println("\nShoes price after discount");

            rs = stmt.executeQuery(update_test_command);
            System.out.printf("%-19s%-35s%-20s\n", "Product ID", "Product Name", "Product Price");
            System.out.println("===============    ===============================    =============");
            while (rs.next())
            {
                System.out.printf("%-19s%-35s%-30s\n", rs.getString(1), rs.getString(2), rs.getDouble(3));
            }

            // Insert new user and show
            System.out.println("\nInsert new user named \"Karen Fitzgerald\" and show");
            stmt.executeUpdate("INSERT INTO users VALUES (null, 'Karen Fitzgerald', 'kfitzg@yahoo.com', '0827364812');");
            rs = stmt.executeQuery("SELECT * FROM users");
            System.out.printf("%-13s%-27s%-30s%-20s\n", "User ID", "User Name", "User Email", "User Phone");
            System.out.println("=========    =======================    ==========================    ===========");
            while (rs.next())
            {
                System.out.printf("%-13s%-27s%-30s%-20s\n", rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
            }

            // Ethan McGowan cancelled the order he made on the 2nd of December 2021 and show that it has been cancelled
            System.out.println("\nDelete order on 2nd December 2021 made by Ethan McGowan");
            String delete_test_command = "SELECT DISTINCT u.user_name, o.order_date_time, o.order_id\n" +
                    "FROM orders o, user_address_payment uap, users u\n" +
                    "WHERE uap.user_id = u.user_id  AND o.user_address_payment_id = o.user_address_payment_id\n" +
                    "AND u.user_name LIKE \"%Ethan McGowan%\" AND o.order_date_time LIKE \"2021-12-02%\";";

            System.out.println("Before delete:");
            rs = stmt.executeQuery(delete_test_command);
            System.out.printf("%-17s%-20s%-20s%-30s\n", "User Name", "Order Date", "Order Time", "Order ID");
            System.out.println("=============    ================    ================    ========");

            while(rs.next()) {
                System.out.printf("%-17s%-20s%-20s%-30s\n", rs.getString("user_name"), rs.getDate("order_date_time"), rs.getTime("order_date_time"), rs.getString("order_id"));
            }

            stmt.executeUpdate("DELETE FROM order_shopping_cart\n" +
                    "WHERE order_id = (SELECT DISTINCT o.order_id \n" +
                    "FROM orders o, user_address_payment uap, users u\n" +
                    "WHERE uap.user_id = u.user_id  AND o.user_address_payment_id = o.user_address_payment_id\n" +
                    "AND u.user_name LIKE \"%Ethan McGowan%\" AND o.order_date_time LIKE \"2021-12-02%\");");

            stmt.executeUpdate("DELETE FROM orders\n" +
                    "WHERE order_id = (SELECT DISTINCT o.order_id \n" +
                    "FROM orders o, user_address_payment uap, users u\n" +
                    "WHERE uap.user_id = u.user_id  AND o.user_address_payment_id = o.user_address_payment_id\n" +
                    "AND u.user_name LIKE \"%Ethan McGowan%\" AND o.order_date_time LIKE \"2021-12-02%\");");

            System.out.println("\nAfter delete:");
            rs = stmt.executeQuery(delete_test_command);
            System.out.printf("%-17s%-20s%-20s%-30s\n", "User Name", "Order Date", "Order Time", "Order ID");
            System.out.println("=============    ================    ================    ========");

            while(rs.next()) {
                System.out.printf("%-17s%-20s%-20s%-30s\n", rs.getString("user_name"), rs.getDate("order_date_time"), rs.getTime("order_date_time"), rs.getString("order_id"));
            }

            rs.close();
        }
        catch (SQLException e)
        {
            for (Throwable t : e)
            {
                t.printStackTrace();
            }
        }
    }
}
