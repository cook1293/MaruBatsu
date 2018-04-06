package maruBatsu;

/**
 * マルバツゲームの実行クラス
 * @author cook1293
 */

public class MaruBatsuMain {

	public static void main(String[] args) {
		MaruBatsuBasic mbData = new MaruBatsuBasic();

		MaruBatsuGUI mbgui = new MaruBatsuGUI("マルバツゲーム", mbData);
		mbgui.setVisible(true);
	}

}
