package maruBatsu;

import java.util.Random;

/**
 * マルバツゲームの基本データ
 * @author proglight
 */


public class MaruBatsuBasic {
	 Random rand = new Random();
	 MaruBatsuQlearning mbq;

	//ゲームの盤面 0:空白, 1:人間・訓練用CPU(○), -1:CPU(×)
	int[][] field = new int[3][3];

	//ターン 1:人間・訓練用CPU(○), -1:CPU(×)
	int turn = 0;


	//コンストラクタ
	MaruBatsuBasic(){
		startGame(0);
	}

	//ゲームの開始
	//CPUの行動基準	0:ランダム, 1:簡易戦略, 2:Q学習, 3:Q学習時訓練用
	void startGame(int method){
		resetField();

		//先攻後攻はランダム
		turn = rand.nextInt(2);
		if(turn == 0) {
			turn = -1;
			cpuAction(method, -1);
		}
	}

	//訓練の開始
	void startTrain(){
		resetField();

		//先攻後攻はランダム
		turn = rand.nextInt(2);
		if(turn == 0) {
			turn = -1;
		} else {
			cpuAction(0, 1);	//訓練用CPUは人間の代わり
		}

	}

	//Q学習の初期設定・学習
	void qlearning(int learningCnt){
		//Q学習 (フィールドデータ, 割引率, 学習率, ε)
		mbq = new MaruBatsuQlearning(this, 0.8, 0.2, 0.3);
		mbq.qlearn(learningCnt);
	}

	//フィールドのリセット
	void resetField(){
		for(int i=0; i<field.length; i++){
			for(int j=0; j<field[i].length; j++){
				field[i][j] = 0;
			}
		}
	}

	//マークを書く
	boolean mark(int i, int j){
		if(field[i][j] == 0){
			field[i][j] = turn;
			turn *= -1;
			return true;
		}
		return false;
	}

	//CPUの行動
	//CPUの行動基準 0:ランダム, 1:簡易戦略, 2:Q学習, 3:Q学習時訓練用
	void cpuAction(int method, int myTurn){
		double r;
		if(method == 0){		//ランダム
			randomMethod();

		} else if(method == 1){	//簡易戦略
			easyMethod(myTurn);

		} else if(method == 2){	//Q学習
			mbq.qlearnMethod();

		} else if(method == 3){	//Q学習訓練時はランダムと簡易戦略のいずれか
			r = rand.nextDouble();
			if(r < 0.5){
				randomMethod();
			} else {
				easyMethod(myTurn);
			}
		}
	}

	//ランダムな手
	void randomMethod(){
		int i, j;
		boolean marked = false;
		while(!marked){
			i = rand.nextInt(field.length);
			j = rand.nextInt(field.length);
			marked = mark(i, j);
		}
	}

	//簡易戦略的な手
	//myTurn 1:人間の代わりとして(訓練用CPU) -1:人間の敵として
	void easyMethod(int myTurn){
		int tmp;
		int blank = 0;
		boolean marked = false;
		int myMarks = 2 * myTurn;
		int enemyMarks = -2 * myTurn;

		//以下の優先順位で実行
		//1. 自身のマークが2つ並んでいるラインがあれば、そのラインの空いているマスにマークを描く
		//2. 相手のマークが2つ並んでいるラインがあれば、そのラインの空いているマスにマークを描く
		//3. ランダムの空いているマスにマークを描く

		//自身のマークの並びチェック
		//横のラインチェック
		for(int i=0; i<field.length; i++){
			tmp = 0;
			for(int j=0; j<field[i].length; j++){
				tmp += field[i][j];
				if(field[i][j] == 0){
					blank = j;
				}
			}
			if(tmp == myMarks){
				mark(i, blank);
				return;
			}
		}
		//縦のラインチェック
		for(int j=0; j<field[0].length; j++){
			tmp = 0;
			for(int i=0; i<field.length; i++){
				tmp += field[i][j];
				if(field[i][j] == 0){
					blank = i;
				}
			}
			if(tmp == myMarks){
				mark(blank, j);
				return;
			}
		}
		//斜めのラインチェック
		tmp = 0;
		for(int i=0; i<field.length; i++){
			tmp += field[i][i];
			if(field[i][i] == 0){
				blank = i;
			}
		}
		if(tmp == myMarks){
			mark(blank, blank);
			return;
		}
		tmp = 0;
		for(int i=0; i<field.length; i++){
			tmp += field[field.length-i-1][i];
			if(field[field.length-i-1][i] == 0){
				blank = i;
			}
		}
		if(tmp == myMarks){
			mark(field.length-blank-1, blank);
			return;
		}

		//相手のマークの並びチェック
		//横のラインチェック
		for(int i=0; i<field.length; i++){
			tmp = 0;
			for(int j=0; j<field[i].length; j++){
				tmp += field[i][j];
				if(field[i][j] == 0){
					blank = j;
				}
			}
			if(tmp == enemyMarks){
				mark(i, blank);
				return;
			}
		}
		//縦のラインチェック
		for(int j=0; j<field[0].length; j++){
			tmp = 0;
			for(int i=0; i<field.length; i++){
				tmp += field[i][j];
				if(field[i][j] == 0){
					blank = i;
				}
			}
			if(tmp == enemyMarks){
				mark(blank, j);
				return;
			}
		}
		//斜めのラインチェック
		tmp = 0;
		for(int i=0; i<field.length; i++){
			tmp += field[i][i];
			if(field[i][i] == 0){
				blank = i;
			}
		}
		if(tmp == enemyMarks){
			mark(blank, blank);
			return;
		}
		tmp = 0;
		for(int i=0; i<field.length; i++){
			tmp += field[field.length-i-1][i];
			if(field[field.length-i-1][i] == 0){
				blank = i;
			}
		}
		if(tmp == enemyMarks){
			mark(field.length-blank-1, blank);
			return;
		}

		//上記以外ならランダムな手
		int i, j;
		while(!marked){
			i = rand.nextInt(field.length);
			j = rand.nextInt(field.length);
			marked = mark(i, j);
		}

	}


	//勝敗判定 0:続行 1:○の勝ち -1:×の勝ち 99:引き分け
	int judge(){
		int tmp;

		//横のラインチェック
		for(int i=0; i<field.length; i++){
			tmp = 0;
			for(int j=0; j<field[i].length; j++){
				tmp += field[i][j];
			}
			if(tmp == 3){
				return 1;
			} else if(tmp == -3){
				return -1;
			}
		}

		//縦のラインチェック
		for(int j=0; j<field[0].length; j++){
			tmp = 0;
			for(int i=0; i<field.length; i++){
				tmp += field[i][j];
			}
			if(tmp == 3){
				return 1;
			} else if(tmp == -3){
				return -1;
			}
		}

		//斜めのラインチェック
		tmp = 0;
		for(int i=0; i<field.length; i++){
			tmp += field[i][i];
		}
		if(tmp == 3){
			return 1;
		} else if(tmp == -3){
			return -1;
		}
		tmp = 0;
		for(int i=0; i<field.length; i++){
			tmp += field[field.length-i-1][i];
		}
		if(tmp == 3){
			return 1;
		} else if(tmp == -3){
			return -1;
		}

		//引き分けチェック
		tmp = 0;
		for(int i=0; i<field.length; i++){
			for(int j=0; j<field[i].length; j++){
				if(field[i][j] != 0){
					tmp++;
				}
			}
		}
		if(tmp == 9){
			return 99;
		}

		return 0;
	}

}
