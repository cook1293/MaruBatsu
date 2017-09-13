package maruBatsu;

import java.util.Random;

/**
 * Q学習によるマルバツゲーム
 * @author proglight
 */

public class MaruBatsuQlearning {

	public static final int MAXSTATE = (int)Math.pow(3, 9);

	Random rand = new Random();
	MaruBatsuBasic mbData;
	ConvertBaseNum cbn = new ConvertBaseNum();

	//Q値[状態][行動]
	double[][] q = new double[MAXSTATE][9];


	//割引率
	double gammaRate;
	//学習率
	double learningRate;
	//ε
	double epsilonRate;

	//ターン(1:訓練用敵CPU, -1:学習するCPU)
	int turn;

	//ステップ数
	int step;
	//学習回数
	int learningCnt;

	//コンストラクタ
	MaruBatsuQlearning(MaruBatsuBasic mbData, double gr, double lr, double er){
		this.mbData = mbData;
		this.gammaRate = gr;
		this.learningRate = lr;
		this.epsilonRate = er;

		//Q値の初期化
		initializeQ();
	}

	//Q値の初期化(後ろから初期化していく）
	void initializeQ(){
		int fieldNum, num;
		for(int i=0; i<q.length; i++){
			//ハッシュである10進数を3進数に変換
			fieldNum = cbn.convertToN(i, 3);
			for(int j=0; j<q[i].length; j++){
				//マークできない場所は指定値にしておく
				num = fieldNum % 10;
				fieldNum = fieldNum / 10;

				if(num != 0){
					q[i][q[i].length-j-1] = -999;
				} else {
					//q[i][q[i].length-j-1] = rand.nextInt(10);
					q[i][q[i].length-j-1] = 0;
				}
			}
		}
	}

	//実際のフィールド状態→3進数→10進数ハッシュ
	int convertToHash(int[][] field){
		int fieldNum = 0;
		int hashNum;
		int tmp;

		//フィールドを3進数に変換
		for(int i=0; i<field.length; i++){
			for(int j=0; j<field[i].length; j++){
				tmp = field[i][j];
				if(tmp == -1) tmp = 2;		//-1は2に変換する
				fieldNum  = fieldNum * 10 + tmp;
			}
		}
		//3進数を10進数のハッシュに変換
		hashNum = cbn.convertTo10(fieldNum, 3);
		return hashNum;
	}


	//Q学習
	void qlearn(int gamesCnt){

		//簡易戦略で学習していく
		int method = 3;
		mbData.startTrain();


		int hashNum, nextHashNum;
		int place;
		int judge = 0;
		int nowR;
		double maxNextQ;

		int games  = 1;

		//学習回数分学習
		while(games <= gamesCnt){
			hashNum = convertToHash(mbData.field);

			place = eGreedy(hashNum);	//行動
			judge = mbData.judge();		//判定
			nowR = 0;
			//報酬の計算
			if(judge == -1){			//CPUの勝ち
				nowR = 10;
			} else if(judge == 99){		//引き分け
				nowR = 0;
			}
			if(judge == 0){
				//訓練用CPUの行動
				mbData.enemyAction(method);
				judge = mbData.judge();	//判定

				if(judge == 1){			//CPUの負け
					nowR = -100;
				} else if(judge == 99){	//引き分け
					nowR = 0;
				}
			}

			//行動後のQ値の最大値を計算
			nextHashNum = convertToHash(mbData.field);
			if(judge == 0){
				maxNextQ = q[nextHashNum][getMaxQPlace(nextHashNum)];
			} else {
				maxNextQ = nowR;	//勝敗決定時は、報酬そのものにする
			}

			//Q値の計算
			q[hashNum][place] =
				(1-learningRate) * q[hashNum][place] + learningRate * (nowR+gammaRate*maxNextQ);


			//ゲーム終了時
			if(judge != 0){
				//ゲームをリセットする
				mbData.startTrain();
				judge = 0;
				games++;
			}
		}
		mbData.resetField();

	}

	//ε-greedy法：εの確率でランダムに選び、1-εの確率でQ値が最大となるものを選ぶ
	int eGreedy(int hashNum){

		int i, j;
		int maxPlace = 0, place = 0;
		boolean marked = false;

		double rate = rand.nextDouble();

		if(rate >= epsilonRate){
			//Q値が最大のものを選ぶ
			maxPlace = getMaxQPlace(hashNum);
			i = maxPlace / 3;
			j = maxPlace % 3;
			marked = mbData.mark(i, j);
			if(marked){
				place = maxPlace;
				return place;
			}
		}

		//ランダム(最大Q値が無効の場合もランダムにする)
		while(!marked){
			i = rand.nextInt(mbData.field.length);
			j = rand.nextInt(mbData.field.length);
			marked = mbData.mark(i, j);
			place = i * 3 + j;
		}
		return place;
	}

	//Q値が最大となるマーク位置を返す
	int getMaxQPlace(int hashNum){
		int maxPlace = 0;
		for(int j=1; j<q[hashNum].length; j++){
			if(q[hashNum][j] > q[hashNum][maxPlace]){
				maxPlace = j;
			}
		}
		return maxPlace;
	}


	//Q学習後の敵行動
	void qlearnMethod(){
		int i, j;
		int hashNum = convertToHash(mbData.field);
		int place = getMaxQPlace(hashNum);
		boolean marked = false;

		//各Q値の出力
		/*
		for(int k=0; k<9; k++){
			System.out.printf("%.3f, ", q[hashNum][k]);
			if((k+1)%3==0){
				System.out.println();
			}
		}
		System.out.println();
		*/

		i = place / 3;
		j = place % 3;
		marked = mbData.mark(i, j);

		if(marked) return ;

		while(!marked){
			i = rand.nextInt(mbData.field.length);
			j = rand.nextInt(mbData.field.length);
			marked = mbData.mark(i, j);
		}
		return ;
	}

}




