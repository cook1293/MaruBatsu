package maruBatsu;

/**
 * マルバツゲームの実行クラス
 * @author proglight
 */

public class MaruBatsuMain {

	public static void main(String[] args) {
		MaruBatsuBasic mbData = new MaruBatsuBasic();

		MaruBatsuGUI mbgui = new MaruBatsuGUI("マルバツゲーム", mbData);
		mbgui.setVisible(true);
	}

}
