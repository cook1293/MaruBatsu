package maruBatsu;

import java.util.Random;

/**
 * マルバツゲームの基本データ
 * @author proglight
 */


public class MaruBatsuBasic {
	 Random rand = new Random();

	//ゲームの盤面 0:空白, 1:人間(○), -1:CPU(×)
	int[][] field = new int[3][3];

	//ターン 1:人間(○), -1:CPU(×)
	int turn = 0;


	//int method = 0;

	//コンストラクタ
	MaruBatsuBasic(){
		startGame();
	}

	//ゲームの開始
	void startGame(){
		resetField();

		//先攻後攻はランダム
		turn = rand.nextInt(2);
		if(turn == 0) {
			turn = -1;
			enemyAction(0);
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
			enemyAction(0);
		}

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

	//敵の行動
	//敵の行動基準 0:ランダム, 1:簡易戦略, 2:Q学習, 3:Q学習時訓練用
	void enemyAction(int method){
		double r;
		if(method == 0){		//ランダム
			randomMethod();
		} else if(method == 1){	//簡易戦略
			easyMethod();
		} else if(method == 3){	//訓練時はランダムと簡易戦略のいずれか
			r = rand.nextDouble();
			if(r < 0.5){
				randomMethod();
			} else {
				easyMethod();
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
	void easyMethod(){
		int tmp;
		int blank = 0;
		boolean marked = false;

		//○か×が2つ並んでいるラインがあれば、そのラインの空いているマスにマークを書く
		//それ以外であればランダム

		//横のラインチェック
		for(int i=0; i<field.length; i++){
			tmp = 0;
			for(int j=0; j<field[i].length; j++){
				tmp += field[i][j];
				if(field[i][j] == 0){
					blank = j;
				}
			}
			if(tmp == 2 || tmp == -2){
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
			if(tmp == 2 || tmp == -2){
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
		if(tmp == 2 || tmp == -2){
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
		if(tmp == 2 || tmp == -2){
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


	//勝敗判定 0:続行 1:人間の勝ち -1:CPUの勝ち 99:引き分け
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
