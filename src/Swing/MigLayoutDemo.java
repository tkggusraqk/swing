package Swing;
 
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import javax.swing.JDesktopPane;

import java.awt.FlowLayout;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JScrollPane;

import com.alibaba.fastjson.JSON;

public class MigLayoutDemo extends JFrame {

	JDesktopPane desktopPane;
	int lastY = 0;
	Date lastDate = null;
	Timer scrollTimer = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MigLayoutDemo frame = new MigLayoutDemo();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MigLayoutDemo() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		desktopPane = new JDesktopPane();
		desktopPane.setLayout(new MigLayout("wrap 6"));

		JScrollPane scrollPane = new JScrollPane(desktopPane);

		JScrollBar vJScrollBar = scrollPane.getVerticalScrollBar();

		vJScrollBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if (scrollTimer == null) {
					scrollTimer = new Timer();
					scrollTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							loadScreen(scrollPane, desktopPane.getY(), new Date());
						}
					}, 500, 1500);
				}
				lastDate = new Date();
				lastY = desktopPane.getY();
			}
		});

		getContentPane().add(scrollPane, BorderLayout.CENTER);

		for (int i = 0; i < 10; i++) {
			Test test = new Test();
			test.setTitle("test" + i);
			test.setVisible(true);
			desktopPane.add(test);
		}
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int i = 0;
				while (i < 10) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							Test test = new Test();
							test.setTitle("test-new-" + desktopPane.getComponentCount());
							test.setVisible(true);
							desktopPane.add(test);
						}
					});
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					i++;
				}
			}
		});
		thread.start();

	}

	private void loadScreen(JScrollPane scrollPane, int curY, Date curDate) {
		// 两分钟以上坐标没有发生变化，停止定时器
		if (lastDate != null && new Date(curDate.getTime() - lastDate.getTime()).getMinutes() >= 1) {
			lastDate = curDate;
			scrollTimer.cancel();
			scrollTimer = null;
		}
		if (lastY != curY) {
			return;
		}
		int clsCount = desktopPane.getComponentCount();
		for (int i = 0; i < clsCount; i++) {
			// 可视范围
			Point point = scrollPane.getLocation();
			Rectangle rectangle = new Rectangle(point.x, point.y, scrollPane.getWidth(), scrollPane.getHeight());
			System.out.println("可视范围：" + JSON.toJSONString(rectangle));

			// 子对象的起点坐标 和终点坐标
			Test test = (Test) desktopPane.getComponent(i);
			Rectangle startItemRectangle = test.getBounds();
			startItemRectangle.y = Math.abs(Math.abs(desktopPane.getY()) - startItemRectangle.y);

			System.out.println("子项坐标：" + test.getTitle() + "," + JSON.toJSONString(startItemRectangle));
			System.out.println("desktopPane:x:" + desktopPane.getX() + ",y:" + Math.abs(desktopPane.getY()) + ",h:"
					+ desktopPane.getHeight());

			// 起点坐标在可视范围内
			boolean isStartPoint = rectangle.width > (startItemRectangle.x + 150)
					&& rectangle.height > (startItemRectangle.y + 160);
			boolean isEndPoint = Math.abs((test.getBounds().y + 480) - 160) >= Math.abs(desktopPane.getY());
			if (isStartPoint && isEndPoint) {
				System.out.println("对象:" + test.getTitle() + "在可视范围内");
				test.setBtnBack(Color.red);
			} else {
				test.setBtnBack(Color.gray);
			}
		}
	}
}
