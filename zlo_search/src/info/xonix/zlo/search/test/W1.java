package info.xonix.zlo.search.test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Author: Vovan
 * Date: 14.12.2007
 * Time: 20:19:48
 */
public class W1 {
    public static void main(String[] args) {
        JFrame f = new JFrame("fr1");
        f.setSize(400, 400);
        JButton b = new JButton("Push me");
        b.setSize(50, 20);
        JPanel p = new JPanel();
        final JTextField tf = new JTextField("qqqq");
        p.add(b);
        p.add(tf);
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getActionCommand());
                tf.setVisible(!tf.isVisible());
            }
        });
        f.add(p);
        f.setVisible(true);
    }
}
