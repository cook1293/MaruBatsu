package maruBatsu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * マルバツゲームの画面処理
 * @author cook1293
 */

public class MaruBatsuGUI extends JFrame{
	public static final int SIZECELL = 75;
	public static final int SIZEPNX = SIZECELL * 3;
	public static final int SIZEPNY = SIZECELL * 3;
	public static final int SIZEWINX = SIZEPNX + 30;
	public static final int SIZEWINY = SIZEPNY + 160;

	//勝敗判定
	int judge = 0;

	//CPUの行動基準	0:ランダム, 1:簡易戦略, 2:Q学習, 3:Q学習時訓練用
	int method = 0;

	//GUI部品
	MBPanel pn = new MBPanel();
	JLabel judgeLb;
	JButton resetBtn;
	JRadioButton[] methodRB = new JRadioButton[3];
	ButtonGroup methodGp = new ButtonGroup();

	MaruBatsuBasic mbData;
	MaruBatsuQlearning mbq;

	//コンストラクタ
	MaruBatsuGUI(String title, MaruBatsuBasic mbData){
		this.mbData = mbData;

		//フレームの準備
		pn.setBackground(Color.white);
		setSize(SIZEWINX, SIZEWINY);
		setTitle(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setLayout(null);
		pn.setBounds(0, 0, SIZEPNX, SIZEPNY);
		add(pn);

		MBActionListener als = new MBActionListener();

		//部品
		judgeLb = new JLabel("");
		judgeLb.setBounds(10, SIZEPNY + 10, 90, 30);
		add(judgeLb);

		resetBtn = new JButton("試合開始");
		resetBtn.addActionListener(als);
		resetBtn.setBounds(10, SIZEPNY + 40, 90, 30);
		add(resetBtn);

		methodRB[0] = new JRadioButton("ランダム", true);
		methodRB[0].setBounds(110, SIZEPNY + 10, 90, 30);

		methodRB[1] = new JRadioButton("簡易戦略");
		methodRB[1].setBounds(110, SIZEPNY + 40, 90, 30);

		methodRB[2] = new JRadioButton("Q学習");
		methodRB[2].setBounds(110, SIZEPNY + 70, 90, 30);

		for(int i=0; i<methodRB.length; i++){
			add(methodRB[i]);
			methodGp.add(methodRB[i]);
		}

	}


	class MBPanel extends JPanel{
		MBPanel(){
			addMouseListener(new MBMouseListener());
		}

		public void paintComponent(Graphics g){
			super.paintComponent(g);

			String mark;
			//マルバツの描画
			for(int i=0; i<mbData.field.length; i++){
				for(int j=0; j<mbData.field[i].length; j++){
					if(mbData.field[i][j] == 1){
						mark = "○";
					} else if(mbData.field[i][j] == -1){
						mark = "✕";
					} else {
						mark = "";
					}
					g.setFont(new Font("SansSerif",Font.PLAIN, SIZECELL));
					g.setColor(Color.black);
					g.drawString(mark, SIZECELL*j, (int)(SIZECELL*(i+0.9)));
				}
			}

			//線の描画
			g.setColor(Color.black);
			for(int i=1; i<mbData.field.length; i++){
				g.drawLine(0, SIZECELL*i, SIZECELL*3, SIZECELL*i);
			}
			for(int j=1; j<mbData.field[0].length; j++){
				g.drawLine(SIZECELL*j, 0, SIZECELL*j, SIZECELL*3);
			}

			//勝敗判定
			if(judge == 1){
				judgeLb.setText("あなたの勝ち");
			} else if(judge == -1){
				judgeLb.setText("CPUの勝ち");
			} else if(judge == 99){
				judgeLb.setText("引き分け");
			} else {
				judgeLb.setText("");
			}

		}
	}


	public class MBActionListener implements ActionListener{
		public void actionPerformed(ActionEvent ae){

			//リセット
			if(ae.getSource() == resetBtn){

				//選択された行動基準を設定
				for(int i=0; i<methodRB.length; i++){
					if(methodRB[i].isSelected()){
						method = i;
					}
				}

				//Q学習時は学習回数を指定して学習
				if(method == 2){
					mbData.qlearning(100000);
				}

				mbData.startGame(method);
				judge = 0;

				repaint();
			}
		}
	}


	class MBMouseListener implements MouseListener{

		int x, y, i, j;
		boolean marked;

		//クリックしたときの処理
		public void mouseClicked(MouseEvent me){
			if(judge == 0){
				x = me.getX();
				y = me.getY();
				i = y / SIZECELL;
				j = x / SIZECELL;

				marked = mbData.mark(i, j);
				if(marked){
					judge = mbData.judge();
					repaint();

					//CPUの行動
					if(judge == 0){

						mbData.cpuAction(method, -1);

						judge = mbData.judge();
						repaint();
					}
				}
			}
		}
		public void mouseEntered(MouseEvent me){}
		public void mouseExited(MouseEvent me){}
		public void mousePressed(MouseEvent me){}
		public void mouseReleased(MouseEvent me){}
	}
}
