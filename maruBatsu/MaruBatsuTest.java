package maruBatsu;

/**
 * マルバツゲーム各手法の比較
 * @author proglight
 */

public class MaruBatsuTest {
	static MaruBatsuBasic mbData;

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		mbData = new MaruBatsuBasic();


		//ゲーム回数
		int gamesCnt = 100000;
		//Q学習回数
		int learnCnt = 100000;

		//Q学習
		mbData.qlearning(learnCnt);

		//テストゲーム
		System.out.println();
		System.out.println(gamesCnt + "回対戦(相手：簡易戦略)");

		System.out.println("ランダム");
		gameTest(0, gamesCnt);

		System.out.println("簡易戦略");
		gameTest(1, gamesCnt);

		System.out.println(learnCnt + "回Q学習後");
		gameTest(2, gamesCnt);
	}

	//テストゲーム
	//対象CPUの行動基準	0:ランダム, 1:簡易戦略, 2:Q学習, 3:Q学習時訓練用
	static void gameTest(int method, int gamesCnt){
		int games = 0;
		int judge = 0;
		int winDraw = 0;
		int win = 0;

		while(games < gamesCnt){
			mbData.startTrain();

			while(true){

				//対象CPUの行動
				mbData.cpuAction(method, -1);

				judge = mbData.judge();
				if(judge != 0) {
					if(judge == -1){
						winDraw++;
						win++;
					} else if(judge == 99){
						winDraw++;
					}
					break;
				} else {
					mbData.cpuAction(1, 1);		//相手CPU(人間の代わり)の行動
				}

				judge = mbData.judge();
				if(judge != 0){
					if(judge == 99){
						winDraw++;
					}
					break;
				}
			}
			games++;
		}

		System.out.println("無敗率：" + (double)winDraw/games);
		System.out.println("勝　率：" + (double)win/games);
		System.out.println();
	}

}
